package dev.ferroloom.fabric.impl;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

/** The dispatcher FerroLoom keeps between the registration hook and the command hook. */
public final class ClientCommandInternals {
	private static volatile CommandDispatcher<FabricClientCommandSource> dispatcher;

	private ClientCommandInternals() {
	}

	public static void setDispatcher(CommandDispatcher<FabricClientCommandSource> value) {
		dispatcher = value;
	}

	public static CommandDispatcher<FabricClientCommandSource> getActiveDispatcher() {
		return dispatcher;
	}
}