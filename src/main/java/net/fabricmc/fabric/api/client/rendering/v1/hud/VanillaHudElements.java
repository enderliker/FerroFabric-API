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

import net.minecraft.resources.Identifier;

/**
 * The vanilla HUD layer ids, in the order vanilla draws them. FerroLoom has no vanilla HUD elements
 * of its own, so these identify positions only: an element attached relative to one falls back to
 * the start or the end of the order.
 */
public final class VanillaHudElements {
	public static final Identifier MISC_OVERLAYS = id("misc_overlays");
	public static final Identifier CROSSHAIR = id("crosshair");
	public static final Identifier SPECTATOR_MENU = id("spectator_menu");
	public static final Identifier HOTBAR = id("hotbar");
	public static final Identifier ARMOR_BAR = id("armor_bar");
	public static final Identifier HEALTH_BAR = id("health_bar");
	public static final Identifier FOOD_BAR = id("food_bar");
	public static final Identifier AIR_BAR = id("air_bar");
	public static final Identifier MOUNT_HEALTH = id("mount_health");
	public static final Identifier INFO_BAR = id("info_bar");
	public static final Identifier EXPERIENCE_LEVEL = id("experience_level");
	public static final Identifier INFO_BAR_FOREGROUND = id("info_bar_foreground");
	public static final Identifier HELD_ITEM_TOOLTIP = id("held_item_tooltip");
	public static final Identifier SPECTATOR_TOOLTIP = id("spectator_tooltip");
	public static final Identifier MOB_EFFECTS = id("mob_effects");
	public static final Identifier BOSS_BAR = id("boss_bar");
	public static final Identifier SLEEP = id("sleep");
	public static final Identifier DEMO_TIMER = id("demo_timer");
	public static final Identifier SCOREBOARD = id("scoreboard");
	public static final Identifier OVERLAY_MESSAGE = id("overlay_message");
	public static final Identifier TITLE_AND_SUBTITLE = id("title_and_subtitle");
	public static final Identifier CHAT = id("chat");
	public static final Identifier PLAYER_LIST = id("player_list");
	public static final Identifier SUBTITLES = id("subtitles");

	private VanillaHudElements() {
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath("minecraft", path);
	}
}