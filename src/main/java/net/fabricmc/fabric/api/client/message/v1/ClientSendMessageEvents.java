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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientSendMessageEvents {
	private ClientSendMessageEvents() {
	}

	public static final Event<AllowChat> ALLOW_CHAT = EventFactory.createArrayBacked(AllowChat.class,
			listeners -> message -> {
				for (AllowChat listener : listeners) {
					if (!listener.allowSendChatMessage(message)) return false;
				}
				return true;
			});

	public static final Event<AllowCommand> ALLOW_COMMAND = EventFactory.createArrayBacked(AllowCommand.class,
			listeners -> command -> {
				for (AllowCommand listener : listeners) {
					if (!listener.allowSendCommandMessage(command)) return false;
				}
				return true;
			});

	public static final Event<ModifyChat> MODIFY_CHAT = EventFactory.createArrayBacked(ModifyChat.class,
			listeners -> message -> {
				for (ModifyChat listener : listeners) {
					message = listener.modifySendChatMessage(message);
				}
				return message;
			});

	public static final Event<ModifyCommand> MODIFY_COMMAND = EventFactory.createArrayBacked(ModifyCommand.class,
			listeners -> command -> {
				for (ModifyCommand listener : listeners) {
					command = listener.modifySendCommandMessage(command);
				}
				return command;
			});

	public static final Event<Chat> CHAT = EventFactory.createArrayBacked(Chat.class,
			listeners -> message -> {
				for (Chat listener : listeners) listener.onSendChatMessage(message);
			});

	public static final Event<Command> COMMAND = EventFactory.createArrayBacked(Command.class,
			listeners -> command -> {
				for (Command listener : listeners) listener.onSendCommandMessage(command);
			});

	public static final Event<ChatCanceled> CHAT_CANCELED = EventFactory.createArrayBacked(ChatCanceled.class,
			listeners -> message -> {
				for (ChatCanceled listener : listeners) listener.onSendChatMessageCanceled(message);
			});

	public static final Event<CommandCanceled> COMMAND_CANCELED = EventFactory.createArrayBacked(CommandCanceled.class,
			listeners -> command -> {
				for (CommandCanceled listener : listeners) listener.onSendCommandMessageCanceled(command);
			});

	@FunctionalInterface
	public interface AllowChat {
		boolean allowSendChatMessage(String message);
	}

	@FunctionalInterface
	public interface AllowCommand {
		boolean allowSendCommandMessage(String command);
	}

	@FunctionalInterface
	public interface ModifyChat {
		String modifySendChatMessage(String message);
	}

	@FunctionalInterface
	public interface ModifyCommand {
		String modifySendCommandMessage(String command);
	}

	@FunctionalInterface
	public interface Chat {
		void onSendChatMessage(String message);
	}

	@FunctionalInterface
	public interface Command {
		void onSendCommandMessage(String command);
	}

	@FunctionalInterface
	public interface ChatCanceled {
		void onSendChatMessageCanceled(String message);
	}

	@FunctionalInterface
	public interface CommandCanceled {
		void onSendCommandMessageCanceled(String command);
	}
}