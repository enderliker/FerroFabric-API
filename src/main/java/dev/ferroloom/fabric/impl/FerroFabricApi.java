package dev.ferroloom.fabric.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.ferroloom.api.FerroEvents;
import dev.ferroloom.shim.ShimImpl;
import dev.ferroloom.shim.ShimRuntime;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.ChatType$Bound;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * The glue between FerroLoom's engine hooks and the API's events. FerroBootstrap calls these
 * reflectively; they are safe to call more than once.
 */
public final class FerroFabricApi {
    private static final UUID NIL_UUID = new UUID(0, 0);

    private static final FerroEvents.TickListener FORWARD_TICK = (tick, x, y, z) -> {
        ClientTickEvents.fireStart();
        ClientLevel level = ShimRuntime.level();
        if (level != null) {
            ClientTickEvents.START_LEVEL_TICK.invoker().onStartTick(level);
            ClientTickEvents.END_LEVEL_TICK.invoker().onEndTick(level);
        }
        ClientTickEvents.fireEnd();
    };

    /**
     * The engine reports a raw line; a leading slash makes it a command, as upstream's send path
     * does. A cancelled line does not reach MODIFY_* or the notify events.
     */
    private static final FerroEvents.ChatSendListener FORWARD_SEND = message -> {
        if (message.startsWith("/")) {
            if (!ClientSendMessageEvents.ALLOW_COMMAND.invoker().allowSendCommandMessage(message)) {
                ClientSendMessageEvents.COMMAND_CANCELED.invoker()
                        .onSendCommandMessageCanceled(message);
                return null;
            }
            String modified =
                    ClientSendMessageEvents.MODIFY_COMMAND.invoker().modifySendCommandMessage(message);
            ClientSendMessageEvents.COMMAND.invoker().onSendCommandMessage(modified);
            return modified;
        }
        if (!ClientSendMessageEvents.ALLOW_CHAT.invoker().allowSendChatMessage(message)) {
            ClientSendMessageEvents.CHAT_CANCELED.invoker().onSendChatMessageCanceled(message);
            return null;
        }
        String modified = ClientSendMessageEvents.MODIFY_CHAT.invoker().modifySendChatMessage(message);
        ClientSendMessageEvents.CHAT.invoker().onSendChatMessage(modified);
        return modified;
    };

    /**
     * A system message fires the game-message events, a player line the chat events, as upstream
     * does. The engine has no profile for the sender, so it is null; modifying a game message
     * returns plain text and loses its styles (recorded in docs/compatibility.md).
     */
    private static final FerroEvents.ChatReceiveListener FORWARD_RECEIVE = event -> {
        Component message = Component.literal(event.message);
        if (event.system) {
            if (!ClientReceiveMessageEvents.ALLOW_GAME.invoker()
                    .allowReceiveGameMessage(message, event.overlay)) {
                ClientReceiveMessageEvents.GAME_CANCELED.invoker()
                        .onReceiveGameMessageCanceled(message, event.overlay);
                event.cancel = true;
                return;
            }
            Component modified = ClientReceiveMessageEvents.MODIFY_GAME.invoker()
                    .modifyReceivedGameMessage(message, event.overlay);
            ClientReceiveMessageEvents.GAME.invoker()
                    .onReceiveGameMessage(modified, event.overlay);
            String text = modified.getString();
            if (!text.equals(event.message)) {
                event.modified = text;
            }
            return;
        }
        PlayerChatMessage playerMessage = event.hasPlayerMessage
                ? PlayerChatMessage.unsigned(NIL_UUID, event.playerBody)
                : null;
        ChatType$Bound bound = event.chatType == null ? null : ShimImpl.chatTypeBound(event.chatType);
        Instant timeStamp = Instant.ofEpochMilli(event.timestampMillis);
        if (!ClientReceiveMessageEvents.ALLOW_CHAT.invoker()
                .allowReceiveChatMessage(message, playerMessage, null, bound, timeStamp)) {
            ClientReceiveMessageEvents.CHAT_CANCELED.invoker()
                    .onReceiveChatMessageCanceled(message, playerMessage, null, bound, timeStamp);
            event.cancel = true;
            return;
        }
        ClientReceiveMessageEvents.CHAT.invoker()
                .onReceiveChatMessage(message, playerMessage, null, bound, timeStamp);
    };

    private static final FabricClientCommandSource COMMAND_SOURCE = new ClientCommandSource();

    /**
     * Builds the client dispatcher after the entrypoints registered their callbacks. The build
     * context is null until a registry bridge exists; a command that needs it must say so.
     */
    private static final FerroEvents.CommandRegistrationListener REGISTER_COMMANDS = () -> {
        CommandDispatcher<FabricClientCommandSource> dispatcher = new CommandDispatcher<>();
        ClientCommandRegistrationCallback.EVENT.invoker().register(dispatcher, null);
        ClientCommandInternals.setDispatcher(dispatcher);
        return List.of(dispatcher.getAllUsage(dispatcher.getRoot(), COMMAND_SOURCE, false));
    };

    /**
     * Runs the line through Brigadier first. An unknown command returns false so the engine can
     * send it to the server, as a client does; a syntax error is reported to the player.
     */
    private static final FerroEvents.ClientCommandListener RUN_COMMAND = line -> {
        CommandDispatcher<FabricClientCommandSource> dispatcher =
                ClientCommandInternals.getActiveDispatcher();
        if (dispatcher == null) {
            return false;
        }
        String command = line.startsWith("/") ? line.substring(1) : line;
        try {
            dispatcher.execute(command, COMMAND_SOURCE);
            return true;
        } catch (CommandSyntaxException invalid) {
            if (invalid.getType()
                    == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()) {
                return false;
            }
            COMMAND_SOURCE.sendError(Component.literal(invalid.getMessage()));
            return true;
        }
    };

    private static final FerroEvents.UseBlockListener USE_BLOCK =
            (hand, x, y, z, faceX, faceY, faceZ, count) -> {
                BlockHitResult hit = ShimImpl.blockHit(x, y, z, faceX, faceY, faceZ,
                        x + 0.5, y + 0.5, z + 0.5);
                InteractionResult result = UseBlockCallback.EVENT.invoker()
                        .interact(player(), level(), ShimImpl.hand(hand), hit);
                return ShimImpl.resultCode(result);
            };

    private static final FerroEvents.UseItemListener USE_ITEM = (hand, count) -> {
        InteractionResult result = UseItemCallback.EVENT.invoker()
                .interact(player(), level(), ShimImpl.hand(hand));
        return ShimImpl.resultCode(result);
    };

    private static final FerroEvents.UseEntityListener USE_ENTITY = (hand, entityId, count) -> {
        Entity entity = ShimImpl.entity(entityId);
        EntityHitResult hit = ShimImpl.entityHit(entity);
        InteractionResult result = UseEntityCallback.EVENT.invoker()
                .interact(player(), level(), ShimImpl.hand(hand), entity, hit);
        return ShimImpl.resultCode(result);
    };

    private static Player player() {
        Minecraft client = Minecraft.getInstance();
        return client == null ? null : client.player;
    }

    private static Level level() {
        return ShimRuntime.level();
    }

    private FerroFabricApi() {}

    public static void init() {
        // Membership rather than a flag: a test or reload may clear FerroEvents first.
        if (!FerroEvents.TICKS.contains(FORWARD_TICK)) {
            FerroEvents.onClientTick(FORWARD_TICK);
        }
        if (!FerroEvents.CHAT_SEND.contains(FORWARD_SEND)) {
            FerroEvents.onChatSend(FORWARD_SEND);
        }
        if (!FerroEvents.CHAT_RECEIVED.contains(FORWARD_RECEIVE)) {
            FerroEvents.onChatReceived(FORWARD_RECEIVE);
        }
        if (!FerroEvents.COMMAND_REGISTRATION.contains(REGISTER_COMMANDS)) {
            FerroEvents.onRegisterCommands(REGISTER_COMMANDS);
        }
        if (!FerroEvents.CLIENT_COMMANDS.contains(RUN_COMMAND)) {
            FerroEvents.onClientCommand(RUN_COMMAND);
        }
        if (!FerroEvents.USE_BLOCK.contains(USE_BLOCK)) {
            FerroEvents.onUseBlock(USE_BLOCK);
        }
        if (!FerroEvents.USE_ITEM.contains(USE_ITEM)) {
            FerroEvents.onUseItem(USE_ITEM);
        }
        if (!FerroEvents.USE_ENTITY.contains(USE_ENTITY)) {
            FerroEvents.onUseEntity(USE_ENTITY);
        }
    }

    public static void onClientSetup() {
        ClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted(Minecraft.getInstance());
    }

    public static void onClientStopping() {
        ClientLifecycleEvents.CLIENT_STOPPING.invoker().onClientStopping(Minecraft.getInstance());
    }

    public static void onClientLevelLoad() {
        ClientLevel level = ShimRuntime.level();
        if (level != null) {
            ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.invoker()
                    .afterLevelChange(Minecraft.getInstance(), level);
        }
    }

    public static void onClientLevelUnload() {
        // API 26.3 has no level-unload event; the tick events simply stop seeing a level.
    }
}