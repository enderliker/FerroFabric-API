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

package net.fabricmc.fabric.api.util;

import net.minecraft.util.StringRepresentable;

/** The result of an event that controls the execution of an action. */
public enum EventResult implements StringRepresentable {
	ALLOW("allow"),
	PASS("pass"),
	DENY("deny");

	private final String name;

	EventResult(String name) {
		this.name = name;
	}

	public boolean allowAction() {
		return this != DENY;
	}

	public boolean allowAction(boolean passResult) {
		return switch (this) {
			case PASS -> passResult;
			case ALLOW -> true;
			case DENY -> false;
		};
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}