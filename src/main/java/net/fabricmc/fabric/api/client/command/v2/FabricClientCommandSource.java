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

package net.fabricmc.fabric.api.client.command.v2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

/**
 * The source of a client-side command. FerroLoom models the members a command normally uses; the
 * suggestion-provider surface and the position/rotation helpers of upstream are not present yet.
 */
public interface FabricClientCommandSource {
	/** Sends a feedback message to the player; here it becomes a chat line. */
	void sendFeedback(Component message);

	/** Sends an error message to the player. */
	void sendError(Component message);

	Minecraft getClient();

	/** The level the command ran in, or null before a world is open. */
	ClientLevel getWorld();

	/** Upstream's name for {@link #getWorld()}. */
	default ClientLevel getLevel() {
		return getWorld();
	}

	default LocalPlayer getPlayer() {
		Minecraft client = getClient();
		return client == null ? null : client.player;
	}

	default Entity getEntity() {
		return getPlayer();
	}

	default Object getMeta(String key) {
		return null;
	}

	/** Whether the line was typed by the player rather than run from a component. */
	default boolean attended() {
		return true;
	}
}