package dev.ferroloom.fabric;

import static org.junit.jupiter.api.Assertions.*;

import com.mojang.blaze3d.platform.InputConstants$Type;
import dev.ferroloom.api.FerroEvents;
import dev.ferroloom.api.FerroRegistrations;
import dev.ferroloom.shim.GameBridge;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.junit.jupiter.api.Test;

class FabricApiTest {
    @Test
    void an_event_calls_every_registered_listener() {
        Event<Runnable> event = net.fabricmc.fabric.api.event.EventFactory.createArrayBacked(
                Runnable.class, listeners -> () -> {
                    for (Runnable listener : listeners) listener.run();
                });
        List<String> seen = new ArrayList<>();
        event.register(() -> seen.add("a"));
        event.register(() -> seen.add("b"));

        event.invoker().run();

        assertEquals(List.of("a", "b"), seen);
    }

    @Test
    void client_tick_events_fire_end_tick_with_the_installed_client() {
        GameBridge.installClientNow();
        List<Minecraft> seen = new ArrayList<>();
        ClientTickEvents.END_CLIENT_TICK.register(seen::add);

        ClientTickEvents.fireEnd();

        assertEquals(List.of(Minecraft.getInstance()), seen);
    }

    @Test
    void key_mapping_helper_registers_the_mapping_once_with_its_real_default() {
        FerroRegistrations.clear();
        KeyMapping zoom = new KeyMapping("key.testmod.zoom", InputConstants$Type.KEYBOARD, 6, null);

        assertSame(zoom, KeyMappingHelper.registerKeyMapping(zoom));

        String report = FerroRegistrations.report();
        assertEquals(1, report.lines().filter(line -> line.startsWith("KEY\t")).count(), report);
        assertTrue(report.contains("KEY\ttestmod\tzoom\tkey.keyboard.c"), report);
    }

    @Test
    void init_forwards_each_engine_tick_to_end_client_tick_once() {
        GameBridge.installClientNow();
        FerroEvents.clear();
        AtomicInteger ticks = new AtomicInteger();
        ClientTickEvents.END_CLIENT_TICK.register(client -> ticks.incrementAndGet());

        dev.ferroloom.fabric.impl.FerroFabricApi.init();
        dev.ferroloom.fabric.impl.FerroFabricApi.init();
        GameBridge.onTick(1, 0.0, 0.0, 0.0);

        assertEquals(1, ticks.get());
    }
}