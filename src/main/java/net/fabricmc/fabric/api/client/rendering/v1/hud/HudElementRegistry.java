/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.client.rendering.v1.hud;

import java.util.Objects;
import java.util.function.Function;

import net.minecraft.resources.Identifier;

import dev.ferroloom.fabric.impl.HudElementRegistryImpl;

/**
 * A registry of identified HUD elements. FerroLoom draws all of them in its own {@code
 * ferroloom:hud} layer, in registration order; the vanilla layers it names in
 * {@link VanillaHudElements} do not exist in the engine's HUD, so attaching relative to a vanilla
 * id falls back to the start or the end of the list.
 */
public interface HudElementRegistry {
	static void addFirst(Identifier id, HudElement element) {
		Objects.requireNonNull(id, "identifier");
		Objects.requireNonNull(element, "hudElement");
		HudElementRegistryImpl.addFirst(id, element);
	}

	static void addLast(Identifier id, HudElement element) {
		Objects.requireNonNull(id, "identifier");
		Objects.requireNonNull(element, "hudElement");
		HudElementRegistryImpl.addLast(id, element);
	}

	static void attachElementBefore(Identifier beforeThis, Identifier identifier, HudElement element) {
		Objects.requireNonNull(beforeThis, "beforeThis");
		Objects.requireNonNull(identifier, "identifier");
		Objects.requireNonNull(element, "hudElement");
		HudElementRegistryImpl.attachElementBefore(beforeThis, identifier, element);
	}

	static void attachElementAfter(Identifier afterThis, Identifier identifier, HudElement element) {
		Objects.requireNonNull(afterThis, "afterThis");
		Objects.requireNonNull(identifier, "identifier");
		Objects.requireNonNull(element, "hudElement");
		HudElementRegistryImpl.attachElementAfter(afterThis, identifier, element);
	}

	static void removeElement(Identifier identifier) {
		Objects.requireNonNull(identifier, "identifier");
		HudElementRegistryImpl.removeElement(identifier);
	}

	static void replaceElement(Identifier identifier, Function<HudElement, HudElement> replacer) {
		Objects.requireNonNull(identifier, "identifier");
		Objects.requireNonNull(replacer, "replacer");
		HudElementRegistryImpl.replaceElement(identifier, replacer);
	}
}