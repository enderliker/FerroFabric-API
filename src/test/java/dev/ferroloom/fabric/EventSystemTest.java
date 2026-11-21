package dev.ferroloom.fabric;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

class EventSystemTest {
    @FunctionalInterface
    interface Listener {
        void run();
    }

    private static Event<Listener> arrayBacked(List<String> seen) {
        return EventFactory.createArrayBacked(Listener.class, listeners -> () -> {
            for (Listener listener : listeners) listener.run();
        });
    }

    @Test
    void an_array_backed_event_calls_listeners_in_registration_order() {
        List<String> seen = new ArrayList<>();
        Event<Listener> event = arrayBacked(seen);
        event.register(() -> seen.add("a"));
        event.register(() -> seen.add("b"));

        event.invoker().run();

        assertEquals(List.of("a", "b"), seen);
    }

    @Test
    void phases_run_in_their_declared_order() {
        List<String> seen = new ArrayList<>();
        Identifier first = Identifier.fromNamespaceAndPath("test", "first");
        Identifier second = Identifier.fromNamespaceAndPath("test", "second");
        Event<Listener> event = arrayBacked(seen);
        event.addPhaseOrdering(first, second);
        event.register(second, () -> seen.add("second"));
        event.register(first, () -> seen.add("first"));

        event.invoker().run();

        assertEquals(List.of("first", "second"), seen);
    }

    @Test
    void an_event_with_no_listener_has_an_invoker_that_does_nothing() {
        List<String> seen = new ArrayList<>();
        arrayBacked(seen).invoker().run();
        assertTrue(seen.isEmpty());
    }

    @Test
    void client_tick_events_use_the_invoker() {
        dev.ferroloom.shim.GameBridge.installClientNow();
        List<Minecraft> seen = new ArrayList<>();
        ClientTickEvents.END_CLIENT_TICK.register(seen::add);

        ClientTickEvents.fireEnd();

        assertEquals(List.of(Minecraft.getInstance()), seen);
    }
}