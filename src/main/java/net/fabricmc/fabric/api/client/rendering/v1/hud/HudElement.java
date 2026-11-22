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

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** A mod-added HUD element, registered through {@link HudElementRegistry}. */
public interface HudElement {
	/**
	 * Draws the element through the extractor FerroLoom installed for this call.
	 *
	 * @param graphics the {@link GuiGraphicsExtractor} used for rendering
	 * @param deltaTracker the {@link DeltaTracker} providing timing information
	 */
	void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
}