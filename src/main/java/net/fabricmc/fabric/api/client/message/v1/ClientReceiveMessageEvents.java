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

package net.fabricmc.fabric.api.client.message.v1;

import java.time.Instant;

import com.mojang.authlib.GameProfile;
// The generated shim carries no InnerClasses attribute, so a nested game type is named by its
// binary name (as FerroLoom's ShimImpl does); the descriptor is identical to upstream's
// ChatType$Bound.
import net.minecraft.network.chat.ChatType$Bound;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The chat and game message events. FerroLoom fires them from {@code client_chat_received}; the
 * title/subtitle events of upstream are not present yet.
 */
public final class ClientReceiveMessageEvents {
	private ClientReceiveMessageEvents() {
	}

	public static final Event<AllowChat> ALLOW_CHAT = EventFactory.createArrayBacked(AllowChat.class,
			listeners -> (message, playerChatMessage, sender, boundChatType, timeStamp) -> {
				boolean allow = true;
				for (AllowChat listener : listeners) {
					allow &= listener.allowReceiveChatMessage(message, playerChatMessage, sender, boundChatType, timeStamp);
				}
				return allow;
			});

	public static final Event<AllowGame> ALLOW_GAME = EventFactory.createArrayBacked(AllowGame.class,
			listeners -> (message, overlay) -> {
				boolean allow = true;
				for (AllowGame listener : listeners) {
					allow &= listener.allowReceiveGameMessage(message, overlay);
				}
				return allow;
			});

	public static final Event<ModifyGame> MODIFY_GAME = EventFactory.createArrayBacked(ModifyGame.class,
			listeners -> (message, overlay) -> {
				for (ModifyGame listener : listeners) {
					message = listener.modifyReceivedGameMessage(message, overlay);
				}
				return message;
			});

	public static final Event<Chat> CHAT = EventFactory.createArrayBacked(Chat.class,
			listeners -> (message, playerChatMessage, sender, boundChatType, timeStamp) -> {
				for (Chat listener : listeners) {
					listener.onReceiveChatMessage(message, playerChatMessage, sender, boundChatType, timeStamp);
				}
			});

	public static final Event<Game> GAME = EventFactory.createArrayBacked(Game.class,
			listeners -> (message, overlay) -> {
				for (Game listener : listeners) {
					listener.onReceiveGameMessage(message, overlay);
				}
			});

	public static final Event<ChatCanceled> CHAT_CANCELED = EventFactory.createArrayBacked(ChatCanceled.class,
			listeners -> (message, playerChatMessage, sender, boundChatType, timeStamp) -> {
				for (ChatCanceled listener : listeners) {
					listener.onReceiveChatMessageCanceled(message, playerChatMessage, sender, boundChatType, timeStamp);
				}
			});

	public static final Event<GameCanceled> GAME_CANCELED = EventFactory.createArrayBacked(GameCanceled.class,
			listeners -> (message, overlay) -> {
				for (GameCanceled listener : listeners) {
					listener.onReceiveGameMessageCanceled(message, overlay);
				}
			});

	@FunctionalInterface
	public interface AllowChat {
		boolean allowReceiveChatMessage(Component message, PlayerChatMessage playerChatMessage,
				GameProfile sender, ChatType$Bound boundChatType, Instant timeStamp);
	}

	@FunctionalInterface
	public interface AllowGame {
		boolean allowReceiveGameMessage(Component message, boolean overlay);
	}

	@FunctionalInterface
	public interface ModifyGame {
		Component modifyReceivedGameMessage(Component message, boolean overlay);
	}

	@FunctionalInterface
	public interface Chat {
		void onReceiveChatMessage(Component message, PlayerChatMessage playerChatMessage,
				GameProfile sender, ChatType$Bound boundChatType, Instant timeStamp);
	}

	@FunctionalInterface
	public interface Game {
		void onReceiveGameMessage(Component message, boolean overlay);
	}

	@FunctionalInterface
	public interface ChatCanceled {
		void onReceiveChatMessageCanceled(Component message, PlayerChatMessage playerChatMessage,
				GameProfile sender, ChatType$Bound boundChatType, Instant timeStamp);
	}

	@FunctionalInterface
	public interface GameCanceled {
		void onReceiveGameMessageCanceled(Component message, boolean overlay);
	}
}