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

package net.fabricmc.fabric.api.event.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import dev.ferroloom.api.FerroApi;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callback for right-clicking ("using") an entity, fired from the engine's
 * {@code entity_interact} hook.
 *
 * <p>On the logical client, the return values have the following meaning:
 * <ul>
 *     <li>SUCCESS cancels further processing, causes a hand swing, and sends a packet to the server.</li>
 *     <li>CONSUME cancels further processing, and sends a packet to the server. It does NOT cause a hand swing.</li>
 *     <li>PASS falls back to further processing.</li>
 *     <li>FAIL cancels further processing and does not send a packet to the server.</li>
 * </ul>
 */
public interface UseEntityCallback {
	Event<UseEntityCallback> EVENT = EventFactory.createArrayBacked(UseEntityCallback.class,
			(listeners) -> (player, level, hand, entity, hitResult) -> {
				for (UseEntityCallback event : listeners) {
					InteractionResult result;
					try {
						result = event.interact(player, level, hand, entity, hitResult);
					} catch (Throwable failure) {
						FerroApi.log("UseEntityCallback listener failed: " + failure);
						continue;
					}

					if (result != InteractionResult.PASS) {
						return result;
					}
				}

				return InteractionResult.PASS;
			}
	);

	InteractionResult interact(Player player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult);
}