package dev.ferroloom.fabric;

import static org.junit.jupiter.api.Assertions.*;

import com.mojang.blaze3d.platform.InputConstants$Type;
import dev.ferroloom.api.FerroRegistrations;
import dev.ferroloom.shim.ShimRuntime;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeyMappingTest {
    @BeforeEach
    void reset() {
        FerroRegistrations.clear();
    }

    @Test
    void construction_alone_does_not_register_and_a_duplicate_throws() {
        KeyMapping first = new KeyMapping("key.test.zoom", InputConstants$Type.KEYBOARD, 25, null);
        assertTrue(FerroRegistrations.report().isEmpty(), "the constructor must not register");

        assertSame(first, KeyMappingHelper.registerKeyMapping(first));
        assertTrue(FerroRegistrations.report().contains("KEY\ttest\tzoom\tkey.keyboard.v"),
                FerroRegistrations.report());

        KeyMapping duplicate = new KeyMapping("key.test.zoom", InputConstants$Type.KEYBOARD, 6, null);
        assertThrows(IllegalArgumentException.class,
                () -> KeyMappingHelper.registerKeyMapping(duplicate));
    }

    @Test
    void the_bound_key_reflects_the_binding_the_engine_pushes() {
        KeyMapping zoom = new KeyMapping("key.test.zoom", InputConstants$Type.KEYBOARD, 25, null);
        KeyMappingHelper.registerKeyMapping(zoom);

        assertEquals("key.keyboard.v", KeyMappingHelper.getBoundKeyOf(zoom).getName());

        ShimRuntime.setBoundKey("test", "zoom", "key.keyboard.c");
        assertEquals("key.keyboard.c", KeyMappingHelper.getBoundKeyOf(zoom).getName());
    }
}