package dev.ferroloom.fabric.impl;

import dev.ferroloom.api.FerroApi;
import dev.ferroloom.shim.ShimRuntime;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;

/** One client command source; feedback and errors go to the chat through the engine's write queue. */
public final class ClientCommandSource implements FabricClientCommandSource {
	@Override
	public void sendFeedback(Component message) {
		FerroApi.sendChat(message == null ? "" : message.getString());
	}

	@Override
	public void sendError(Component message) {
		sendFeedback(message);
	}

	@Override
	public Minecraft getClient() {
		return Minecraft.getInstance();
	}

	@Override
	public ClientLevel getWorld() {
		return ShimRuntime.level();
	}
}