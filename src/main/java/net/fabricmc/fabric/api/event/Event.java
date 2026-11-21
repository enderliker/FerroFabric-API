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

import net.minecraft.resources.Identifier;

/**
 * Base class for Fabric's event implementations, adapted from Fabric API.
 *
 * @param <T> The listener type.
 * @see EventFactory
 */
public abstract class Event<T> {
	/**
	 * The invoker field. This should be updated by the implementation to
	 * always refer to an instance containing all code that should be
	 * executed upon event emission.
	 */
	protected volatile T invoker;

	/**
	 * Returns the invoker instance, which hides multiple registered listeners
	 * of type T under one instance of type T.
	 *
	 * @return the invoker instance
	 */
	public final T invoker() {
		return invoker;
	}

	/**
	 * Register a listener to the event, in the default phase.
	 *
	 * @param listener the desired listener
	 */
	public abstract void register(T listener);

	/**
	 * The identifier of the default phase.
	 */
	public static final Identifier DEFAULT_PHASE = Identifier.fromNamespaceAndPath("fabric", "default");

	/**
	 * Register a listener to the event for the specified phase.
	 *
	 * @param phase    identifier of the phase this listener should be registered for
	 * @param listener the desired listener
	 */
	public void register(Identifier phase, T listener) {
		// Kept non-abstract for compatibility with existing Event subclasses.
		register(listener);
	}

	/**
	 * Request that listeners registered for one phase be executed before listeners registered
	 * for another phase.
	 *
	 * @param firstPhase  the identifier of the phase that should run before the other
	 * @param secondPhase the identifier of the phase that should run after the other
	 */
	public void addPhaseOrdering(Identifier firstPhase, Identifier secondPhase) {
	}
}