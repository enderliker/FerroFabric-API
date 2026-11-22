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

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandBuildContext;

import dev.ferroloom.fabric.impl.Failures;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/** Called when FerroLoom builds the client command dispatcher, after the mods' entrypoints. */
public interface ClientCommandRegistrationCallback {
	Event<ClientCommandRegistrationCallback> EVENT = EventFactory.createArrayBacked(ClientCommandRegistrationCallback.class, (callbacks) -> (dispatcher, buildContext) -> {
		for (ClientCommandRegistrationCallback callback : callbacks) {
			Failures.run("ClientCommandRegistrationCallback",
					() -> callback.register(dispatcher, buildContext));
		}
	});

	/**
	 * Called when registering client commands.
	 *
	 * @param dispatcher the command dispatcher to register commands to
	 * @param buildContext object exposing access to the game's holders; null in FerroLoom until a
	 *                     registry bridge exists
	 */
	void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext);
}