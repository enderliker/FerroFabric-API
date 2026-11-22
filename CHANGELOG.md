# Changelog

All notable changes to FerroFabric-API. The version number follows the FerroLoom convention:
`<minecraft>-ferro.<revision>`.

## [Unreleased]

### Added

- `fabric-message-api-v1`: `ClientSendMessageEvents` (ALLOW_CHAT, MODIFY_CHAT, ALLOW_COMMAND,
  MODIFY_COMMAND and their notify/cancelled events) and `ClientReceiveMessageEvents` (ALLOW_GAME,
  MODIFY_GAME, GAME, ALLOW_CHAT, CHAT and their cancelled events), fired from the engine's
  `client_chat` and `client_chat_received` hooks. A line starting with `/` uses the command
  events. The sender is null, a chat line is plain text, and a modified game message is plain
  text too (styles are lost).
- The shim now models `Component`/`MutableComponent` (`literal`, `empty`, `getString`, `copy`,
  `append`), a minimal `PlayerChatMessage` and `ChatType.Bound`.
- `fabric-events-interaction-v0`: `UseBlockCallback`, `UseItemCallback` and
  `UseEntityCallback`, fired from the engine's `right_click_block`, `right_click_item` and
  `entity_interact` hooks on the client half. A result other than PASS cancels the click; the
  objects are minimal (the player and level come from the synthetic client, an entity carries its
  id, a stack its count) and the attack callbacks are not present.
- The shim's generator can initialise static fields from an impl table, so 26.3's
  `InteractionResult` constants and `ItemStack.EMPTY` are real singletons instead of nulls.
- `fabric-rendering-v1` (HUD part): `HudElement`, `HudElementRegistry` and
  `VanillaHudElements`, drawn in FerroLoom's `ferroloom:hud` layer through a modelled
  `GuiGraphicsExtractor` (text, fills, gradients). The vanilla layer ids are positions only; a
  `Font.width` approximation centres text, and sprites, items and clipping are not modelled.
- `fabric-command-api-v2`: `ClientCommandRegistrationCallback`, `ClientCommands` and
  `FabricClientCommandSource` over the real Brigadier 1.3.11 dispatcher. FerroLoom builds the
  dispatcher after the entrypoints, hands its usages to the engine's command list, and runs a
  typed line through it before the engine sends an unknown command to the server. The build
  context is null until a registry bridge exists, and feedback/errors become chat lines.

### Documentation

- The compatibility page now carries the full support matrix (base vs gated, with the engine
  capability each gated module needs) and a real-mod check with AppleSkin, which is refused by
  the networking gate and has no live Java HUD call site.

### Changed

- Nested game types are named by their binary name in the API source (`ChatType$Bound`), because
  the generated shim carries no `InnerClasses` attribute; the descriptors match upstream.

## [26.3-ferro.1] — 2026-11-20

First release, with the modules FerroLoom Plan 3 needs to run an unmodified third-party zoom mod.

### Added

- `fabric-api-base`: the real `Event`/`EventFactory` with `invoker()`, phases and phase ordering,
  plus `AutoInvokingEvent`, `TriState`, `EventResult`, `BooleanFunction` and `NbtType` (adapted
  from Fabric API, Apache-2.0).
- `fabric-key-binding-api-v1`: `KeyMappingHelper.registerKeyMapping` (explicit, duplicate throws)
  and `getBoundKeyOf` (the player's Controls binding), mapping a `KeyMapping` to the engine's
  `Registrar` (translation key to `<mod>_<key>`, 26.3 SDL/HID scancode to a Horizon key name).
  The key is rebindable in Options > Controls.
- `fabric-lifecycle-events-v1`: `ClientTickEvents` with START/END client tick and level tick,
  `ClientLifecycleEvents` (CLIENT_STARTED/CLIENT_STOPPING) and `ClientLevelEvents`
  (AFTER_CLIENT_LEVEL_CHANGE), fired from the engine's client hooks.
- `dev.ferroloom.fabric.impl.FerroFabricApi.init`, the bridge FerroLoom calls after Knot starts.
- The jar is labelled `fabric-api` `26.3-ferro.1` when FerroLoom adds it as a builtin mod, and each module id in `META-INF/ferrofabric/modules.json` is a builtin mod of its own.
- JUnit tests for event dispatch, key registration and the engine tick forwarder.
- CI that checks out FerroLoom, builds its shim and runtime, and runs the suite.