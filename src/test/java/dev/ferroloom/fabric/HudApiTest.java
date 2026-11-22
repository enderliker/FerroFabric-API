package dev.ferroloom.fabric;

import static org.junit.jupiter.api.Assertions.*;

import dev.ferroloom.api.FerroEvents;
import dev.ferroloom.fabric.impl.FerroFabricApi;
import dev.ferroloom.shim.GameBridge;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

class HudApiTest {
    @Test
    void a_hud_element_draws_text_and_fills_through_the_bridge() {
        GameBridge.installClientNow();
        FerroEvents.clear();
        FerroFabricApi.init();
        Identifier id = Identifier.fromNamespaceAndPath("test", "hud");
        HudElementRegistry.addLast(id, (graphics, delta) -> {
            graphics.fill(2, 3, 12, 8, 0x80FF0000);
            graphics.text(Minecraft.getInstance().font, "hi", 4, 5, 0xFFFFFFFF);
            graphics.centeredText(Minecraft.getInstance().font, "mid", 100, 10, 0xFFFFFFFF);
        });

        try {
            assertTrue(GameBridge.hasHudElements());
            String ops = GameBridge.drawHud("ferroloom:hud", 320, 240);

            assertTrue(ops.contains("FILL\t2\t3\t10\t5\t2164195328"), ops);
            assertTrue(ops.contains("TEXT\t4\t5\t4294967295\ttrue\thi"), ops);
            assertTrue(ops.contains("TEXT\t91\t10\t4294967295\ttrue\tmid"), ops);
        } finally {
            HudElementRegistry.removeElement(id);
        }
    }

    @Test
    void an_element_registered_before_another_draws_first() {
        GameBridge.installClientNow();
        FerroEvents.clear();
        FerroFabricApi.init();
        Identifier first = Identifier.fromNamespaceAndPath("test", "first");
        Identifier second = Identifier.fromNamespaceAndPath("test", "second");
        HudElementRegistry.addLast(second, (graphics, delta) -> graphics.fill(0, 0, 1, 1, 0xFFFFFFFF));
        HudElementRegistry.addFirst(first, (graphics, delta) -> graphics.fill(0, 0, 2, 2, 0xFF000000));

        try {
            String ops = GameBridge.drawHud("ferroloom:hud", 320, 240);
            assertTrue(ops.indexOf("FILL\t0\t0\t2\t2") < ops.indexOf("FILL\t0\t0\t1\t1"), ops);
        } finally {
            HudElementRegistry.removeElement(first);
            HudElementRegistry.removeElement(second);
        }
    }
}