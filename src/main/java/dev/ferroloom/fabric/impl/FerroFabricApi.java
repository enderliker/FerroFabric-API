package dev.ferroloom.fabric.impl;

import dev.ferroloom.api.FerroEvents;
import dev.ferroloom.shim.ShimRuntime;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

/**
 * The glue between FerroLoom's engine hooks and the API's events. FerroBootstrap calls these
 * reflectively; they are safe to call more than once.
 */
public final class FerroFabricApi {
    private static final FerroEvents.TickListener FORWARD_TICK = (tick, x, y, z) -> {
        ClientTickEvents.fireStart();
        ClientLevel level = ShimRuntime.level();
        if (level != null) {
            ClientTickEvents.START_LEVEL_TICK.invoker().onStartTick(level);
            ClientTickEvents.END_LEVEL_TICK.invoker().onEndTick(level);
        }
        ClientTickEvents.fireEnd();
    };

    private FerroFabricApi() {}

    public static void init() {
        // Membership rather than a flag: a test or reload may clear FerroEvents first.
        if (!FerroEvents.TICKS.contains(FORWARD_TICK)) {
            FerroEvents.onClientTick(FORWARD_TICK);
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