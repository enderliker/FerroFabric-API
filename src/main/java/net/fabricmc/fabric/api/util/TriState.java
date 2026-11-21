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

import java.util.function.BooleanSupplier;

import net.minecraft.util.StringRepresentable;

/** A boolean value which can be true, false, or refer to a default value. */
public enum TriState implements StringRepresentable {
	FALSE("false"),
	DEFAULT("default"),
	TRUE("true");

	private final String name;

	TriState(String name) {
		this.name = name;
	}

	public static TriState of(boolean bool) {
		return bool ? TRUE : FALSE;
	}

	public boolean toBoolean() {
		return this == TRUE;
	}

	public boolean orElse(boolean value) {
		return this == DEFAULT ? value : toBoolean();
	}

	public boolean orElseGet(BooleanSupplier supplier) {
		return this == DEFAULT ? supplier.getAsBoolean() : toBoolean();
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}