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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import dev.ferroloom.fabric.impl.Failures;

public final class ClientLifecycleEvents {
	private ClientLifecycleEvents() {
	}

	/** Called when the client has started and is ready to play. */
	public static final Event<ClientStarted> CLIENT_STARTED = EventFactory.createArrayBacked(ClientStarted.class,
			callbacks -> client -> {
				for (ClientStarted callback : callbacks) {
					Failures.run("ClientLifecycleEvents.CLIENT_STARTED", () -> callback.onClientStarted(client));
				}
			});

	/** Called when the client is closing, while its settings and resources are still available. */
	public static final Event<ClientStopping> CLIENT_STOPPING = EventFactory.createArrayBacked(ClientStopping.class,
			callbacks -> client -> {
				for (ClientStopping callback : callbacks) {
					Failures.run("ClientLifecycleEvents.CLIENT_STOPPING", () -> callback.onClientStopping(client));
				}
			});

	@FunctionalInterface
	public interface ClientStarted {
		void onClientStarted(Minecraft client);
	}

	@FunctionalInterface
	public interface ClientStopping {
		void onClientStopping(Minecraft client);
	}
}