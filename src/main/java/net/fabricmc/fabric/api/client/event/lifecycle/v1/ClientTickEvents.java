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

package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The client tick events a mod subscribes to. FerroLoom fires them from the engine's client tick,
 * after the key state is pushed.
 */
public final class ClientTickEvents {
	private ClientTickEvents() {
	}

	public static final Event<StartTick> START_CLIENT_TICK = EventFactory.createArrayBacked(StartTick.class,
			callbacks -> client -> {
				for (StartTick event : callbacks) {
					event.onStartTick(client);
				}
			});

	public static final Event<EndTick> END_CLIENT_TICK = EventFactory.createArrayBacked(EndTick.class,
			callbacks -> client -> {
				for (EndTick event : callbacks) {
					event.onEndTick(client);
				}
			});

	public static final Event<StartLevelTick> START_LEVEL_TICK = EventFactory.createArrayBacked(StartLevelTick.class,
			callbacks -> level -> {
				for (StartLevelTick callback : callbacks) {
					callback.onStartTick(level);
				}
			});

	public static final Event<EndLevelTick> END_LEVEL_TICK = EventFactory.createArrayBacked(EndLevelTick.class,
			callbacks -> level -> {
				for (EndLevelTick callback : callbacks) {
					callback.onEndTick(level);
				}
			});

	@FunctionalInterface
	public interface StartTick {
		void onStartTick(Minecraft client);
	}

	@FunctionalInterface
	public interface EndTick {
		void onEndTick(Minecraft client);
	}

	@FunctionalInterface
	public interface StartLevelTick {
		void onStartTick(ClientLevel level);
	}

	@FunctionalInterface
	public interface EndLevelTick {
		void onEndTick(ClientLevel level);
	}

	/** Called by FerroLoom at the start of a client tick. */
	public static void fireStart() {
		START_CLIENT_TICK.invoker().onStartTick(Minecraft.getInstance());
	}

	/** Called by FerroLoom at the end of a client tick. */
	public static void fireEnd() {
		END_CLIENT_TICK.invoker().onEndTick(Minecraft.getInstance());
	}
}