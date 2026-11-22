package dev.ferroloom.fabric;

import static org.junit.jupiter.api.Assertions.*;

import dev.ferroloom.api.FerroEvents;
import dev.ferroloom.fabric.impl.FerroFabricApi;
import dev.ferroloom.shim.GameBridge;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import org.junit.jupiter.api.Test;

/** One throwing mod listener must not stop the others: that is what the modules promise. */
class IsolationTest {
    @Test
    void a_throwing_tick_listener_does_not_stop_the_next_one() {
        GameBridge.installClientNow();
        List<Minecraft> seen = new ArrayList<>();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            throw new IllegalStateException("boom");
        });
        ClientTickEvents.END_CLIENT_TICK.register(seen::add);

        ClientTickEvents.fireEnd();

        assertTrue(seen.contains(Minecraft.getInstance()));
    }

    @Test
    void a_throwing_use_block_listener_leaves_the_next_one_to_answer() {
        GameBridge.installClientNow();
        FerroEvents.clear();
        FerroFabricApi.init();
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            throw new IllegalStateException("boom");
        });
        UseBlockCallback.EVENT.register((player, level, hand, hit) ->
                hit.getBlockPos().getX() == 777 ? InteractionResult.SUCCESS : InteractionResult.PASS);

        assertEquals(1, GameBridge.onUseBlock(0, 777, 64, 0, 1, 0, 0, 1));
    }
}