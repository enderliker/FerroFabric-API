package dev.ferroloom.fabric.impl;

import dev.ferroloom.api.FerroEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * The glue between FerroLoom's engine tick and the API's events. FerroBootstrap calls
 * {@link #init()} reflectively once the loader is up; it is safe to call more than once.
 */
public final class FerroFabricApi {
    private static final FerroEvents.TickListener FORWARD_TICK =
            (tick, x, y, z) -> ClientTickEvents.fireEnd();

    private FerroFabricApi() {}

    public static void init() {
        // Membership rather than a flag: a test or reload may clear FerroEvents first.
        if (!FerroEvents.TICKS.contains(FORWARD_TICK)) {
            FerroEvents.onClientTick(FORWARD_TICK);
        }
    }
}