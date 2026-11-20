package net.fabricmc.fabric.api.client.keymapping.v1;

import net.minecraft.client.KeyMapping;

/**
 * Registers a mod's key mapping with FerroLoom so the engine polls its physical key and Horizon's
 * Controls screen can show it. The mapping's translation key names the mod and the key.
 */
public final class KeyMappingHelper {
    private KeyMappingHelper() {}

    public static KeyMapping registerKeyMapping(KeyMapping keyMapping) {
        return dev.ferroloom.shim.ShimImpl.ensureRegistered(keyMapping);
    }
}