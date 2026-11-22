package dev.ferroloom.fabric;

import static org.junit.jupiter.api.Assertions.*;

import dev.ferroloom.api.FerroEvents;
import dev.ferroloom.fabric.impl.FerroFabricApi;
import dev.ferroloom.shim.GameBridge;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

class CommandApiTest {
    @Test
    void a_registered_command_lists_its_usage_and_runs() {
        FerroEvents.clear();
        FerroFabricApi.init();
        System.clearProperty("test.command.ran");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) ->
                dispatcher.register(ClientCommands.literal("base").then(ClientCommands.literal("test")
                        .executes(context -> {
                            context.getSource().sendFeedback(Component.literal("ran"));
                            System.setProperty("test.command.ran", "1");
                            return 1;
                        }))));

        String usages = GameBridge.registerClientCommands();

        assertTrue(usages.contains("base test"), usages);
        assertTrue(GameBridge.runClientCommand("base test"));
        assertEquals("1", System.getProperty("test.command.ran"));
    }

    @Test
    void an_unknown_command_is_left_for_the_server() {
        FerroEvents.clear();
        FerroFabricApi.init();
        GameBridge.registerClientCommands();

        assertFalse(GameBridge.runClientCommand("not-a-client-command"));
    }
}