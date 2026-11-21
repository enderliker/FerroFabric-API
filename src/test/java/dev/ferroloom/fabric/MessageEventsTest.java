package dev.ferroloom.fabric;

import static org.junit.jupiter.api.Assertions.*;

import dev.ferroloom.api.FerroEvents;
import dev.ferroloom.fabric.impl.FerroFabricApi;
import dev.ferroloom.shim.GameBridge;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import org.junit.jupiter.api.Test;

/**
 * The bridge between the engine's chat hooks and the message events. Listeners are keyed to one
 * message each: Fabric events keep every listener for the JVM's life, so a test that registered a
 * global "cancel everything" listener would leak into the others.
 */
class MessageEventsTest {
    @Test
    void a_cancelled_chat_send_is_reported_to_the_bridge() {
        FerroEvents.clear();
        FerroFabricApi.init();
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> !message.equals("blocked"));

        assertEquals("C", GameBridge.onChatSend("blocked"));
        assertEquals("Mallowed", GameBridge.onChatSend("allowed"));
    }

    @Test
    void a_modified_chat_send_returns_the_new_text() {
        FerroEvents.clear();
        FerroFabricApi.init();
        ClientSendMessageEvents.MODIFY_CHAT.register(
                message -> message.equals("modify") ? "modified" : message);

        assertEquals("Mmodified", GameBridge.onChatSend("modify"));
    }

    @Test
    void a_command_send_uses_the_command_events() {
        FerroEvents.clear();
        FerroFabricApi.init();
        ClientSendMessageEvents.MODIFY_COMMAND.register(
                command -> command.equals("/base") ? "/base now" : command);
        ClientSendMessageEvents.ALLOW_COMMAND.register(command -> !command.equals("/forbidden"));

        assertEquals("M/base now", GameBridge.onChatSend("/base"));
        assertEquals("C", GameBridge.onChatSend("/forbidden"));
    }

    @Test
    void a_game_message_listener_sees_the_text_and_the_overlay_flag() {
        FerroEvents.clear();
        FerroFabricApi.init();
        List<String> seen = new ArrayList<>();
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message.getString().equals("game-hello")) {
                seen.add(message.getString() + ":" + overlay);
            }
        });

        assertEquals("K", GameBridge.onChatReceived(true, true, null, "game-hello", false, "", false, 0L));
        assertEquals(List.of("game-hello:true"), seen);
    }

    @Test
    void a_modified_game_message_replaces_the_text() {
        FerroEvents.clear();
        FerroFabricApi.init();
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) ->
                message.getString().equals("modify-game") ? message.copy().append("!") : message);

        assertEquals("Mmodify-game!",
                GameBridge.onChatReceived(true, false, null, "modify-game", false, "", false, 0L));
    }

    @Test
    void a_player_chat_listener_sees_the_bound_type_the_message_and_can_cancel() {
        FerroEvents.clear();
        FerroFabricApi.init();
        List<String> seen = new ArrayList<>();
        ClientReceiveMessageEvents.ALLOW_CHAT.register(
                (message, playerMessage, sender, bound, timeStamp) -> {
                    if (!message.getString().equals("cancel-me")) {
                        return true;
                    }
                    seen.add(message.getString() + "|" + bound.name().getString() + "|"
                            + playerMessage.signedContent() + "|" + (sender == null) + "|"
                            + timeStamp.toEpochMilli());
                    return false;
                });

        assertEquals("C", GameBridge.onChatReceived(false, false, "minecraft:chat", "cancel-me",
                true, "body", true, 1234L));
        assertEquals(List.of("cancel-me|minecraft:chat|body|true|1234"), seen);
    }
}