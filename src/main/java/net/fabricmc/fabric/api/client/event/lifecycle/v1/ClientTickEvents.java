package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;

/**
 * The client tick events a mod subscribes to. FerroLoom fires {@link #END_CLIENT_TICK} from the
 * engine's client tick after the mods' key state has been pushed.
 */
public final class ClientTickEvents {
    public static final Event<EndTick> END_CLIENT_TICK = new Event<>() {};

    private ClientTickEvents() {}

    @FunctionalInterface
    public interface EndTick {
        void onEndTick(Minecraft client);
    }

    /** Called by FerroLoom once per client tick, after the engine's key state is up to date. */
    public static void fireEnd() {
        END_CLIENT_TICK.dispatch(listener -> listener.onEndTick(Minecraft.getInstance()));
    }
}