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

package net.fabricmc.fabric.api.event;

import java.util.function.Function;

import net.minecraft.resources.Identifier;

import net.fabricmc.fabric.impl.base.event.EventFactoryImpl;

/**
 * Helper for creating {@link Event} classes, adapted from Fabric API.
 */
public final class EventFactory {
	private EventFactory() { }

	public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
		return EventFactoryImpl.createArrayBacked(type, invokerFactory);
	}

	public static <T> Event<T> createArrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
		return createArrayBacked(type, listeners -> {
			if (listeners.length == 0) {
				return emptyInvoker;
			} else if (listeners.length == 1) {
				return listeners[0];
			} else {
				return invokerFactory.apply(listeners);
			}
		});
	}

	public static <T> Event<T> createWithPhases(Class<? super T> type, Function<T[], T> invokerFactory,
			Identifier... defaultPhases) {
		EventFactoryImpl.ensureContainsDefault(defaultPhases);
		EventFactoryImpl.ensureNoDuplicates(defaultPhases);

		Event<T> event = createArrayBacked(type, invokerFactory);

		for (int i = 1; i < defaultPhases.length; ++i) {
			event.addPhaseOrdering(defaultPhases[i - 1], defaultPhases[i]);
		}

		return event;
	}

	@Deprecated
	public static String getHandlerName(Object handler) {
		return handler.getClass().getName();
	}

	@Deprecated
	public static boolean isProfilingEnabled() {
		return false;
	}

	@Deprecated(forRemoval = true)
	public static void invalidate() {
		EventFactoryImpl.invalidate();
	}
}