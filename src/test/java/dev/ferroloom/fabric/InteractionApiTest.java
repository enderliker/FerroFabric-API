package dev.ferroloom.fabric;

import static org.junit.jupiter.api.Assertions.*;

import dev.ferroloom.api.FerroEvents;
import dev.ferroloom.fabric.impl.FerroFabricApi;
import dev.ferroloom.shim.GameBridge;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.junit.jupiter.api.Test;

class InteractionApiTest {
    @Test
    void a_use_block_callback_cancels_the_click_with_success() {
        GameBridge.installClientNow();
        FerroEvents.clear();
        FerroFabricApi.init();
        UseBlockCallback.EVENT.register((player, level, hand, hit) ->
                hit.getBlockPos().getX() == 3 && hand == InteractionHand.MAIN_HAND
                        ? InteractionResult.SUCCESS
                        : InteractionResult.PASS);

        assertEquals(1, GameBridge.onUseBlock(0, 3, 64, 0, 1, 0, 0, 1));
        assertEquals(0, GameBridge.onUseBlock(0, 4, 64, 0, 1, 0, 0, 1));
    }

    @Test
    void a_use_item_callback_fails_the_click_on_the_off_hand() {
        GameBridge.installClientNow();
        FerroEvents.clear();
        FerroFabricApi.init();
        UseItemCallback.EVENT.register((player, level, hand) ->
                hand == InteractionHand.OFF_HAND ? InteractionResult.FAIL : InteractionResult.PASS);

        assertEquals(4, GameBridge.onUseItem(1, 1));
        assertEquals(0, GameBridge.onUseItem(0, 1));
    }

    @Test
    void a_use_entity_callback_consumes_the_click_for_its_entity() {
        GameBridge.installClientNow();
        FerroEvents.clear();
        FerroFabricApi.init();
        UseEntityCallback.EVENT.register((player, level, hand, entity, hit) ->
                entity.getId() == 7 ? InteractionResult.CONSUME : InteractionResult.PASS);

        assertEquals(3, GameBridge.onUseEntity(0, 7, 1));
        assertEquals(0, GameBridge.onUseEntity(0, 8, 1));
    }
}