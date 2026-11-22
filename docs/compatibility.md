# Compatibility

What this release covers, and where it differs from upstream Fabric API. FerroLoom's own record
is in
[`docs/ferrofabric-deviations.md`](https://github.com/enderliker/FerroLoom/blob/master/docs/ferrofabric-deviations.md);
this page is the API's view.

## Modules present

| Module | Public classes | Notes |
|---|---|---|
| `fabric-api-base` | `Event`, `EventFactory`, `AutoInvokingEvent`, `TriState`, `EventResult`, `BooleanFunction`, `NbtType` | The real event system: `invoker()`, `register(T)`, phases and `addPhaseOrdering`, adapted from Fabric API. A throwing listener is logged and the others still run. |
| `fabric-key-binding-api-v1` | `net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper` | `registerKeyMapping` registers with the engine's `Registrar` and throws on a duplicate; `getBoundKeyOf` returns the player's Controls binding, pushed by the engine each tick. |
| `fabric-lifecycle-events-v1` | `ClientTickEvents`, `ClientLifecycleEvents`, `ClientLevelEvents` | START/END client and level tick; CLIENT_STARTED/STOPPING; AFTER_CLIENT_LEVEL_CHANGE, fired from the engine hooks. |
| `fabric-message-api-v1` | `ClientSendMessageEvents`, `ClientReceiveMessageEvents` | Send and receive events from `client_chat` and `client_chat_received`. Plain text, null sender, no styles. |
| `fabric-command-api-v2` | `ClientCommandRegistrationCallback`, `ClientCommands`, `FabricClientCommandSource` | Client-side Brigadier commands. The dispatcher is built after the entrypoints; usages reach the engine, and an unknown command falls through to the server. |

## Behaviour differences that a mod can notice

1. **Client entrypoints run at registration.** Horizon accepts key declarations only during
   Registration. A mod that registers a key in `onInitializeClient` works; a mod that expects the
   full client (a real renderer, screens) at that moment does not. Client setup is a readiness
   marker, not the first client code.
2. **The JVM is shared and long-lived.** One JVM per game process; a second world does not repeat
   `main`, as in Fabric.
3. **Engine reads happen on the game thread and are pushed before the tick.** A tick listener
   sees the position pushed for that tick. A read from a mod-created thread fails with
   `FerroNotInCallbackException`; there is no world to read outside a callback.
4. **No Java rendering.** Anything that draws (screens, HUD, world render events) has no Java
   implementation yet; the API does not pretend otherwise.
5. **Key codes are 26.3 SDL/HID scancodes.** `InputConstants.KEY_C` is 6, not GLFW's 67. The API
   converts them to Horizon key names; a mod that hardcodes GLFW numbers will ask for the wrong
   key.
6. **Message events carry plain text and no sender.** The engine's hook reports a component tree
   and a sender UUID; the bridge passes the flattened text and a null `GameProfile`. A
   `PlayerChatMessage` carries the signed body and the signature flag only, and
   `ChatType.Bound.chatType()` is null (the name is the registry name the engine reported).
   `MODIFY_GAME` returns text, so the replacement line loses the original styles. A line that
   starts with `/` uses the command events, as upstream's send path does.
7. **A client command gets a null build context.** `ClientCommandRegistrationCallback.register`
   receives null instead of a `CommandBuildContext` until a registry bridge exists; a command that
   reads it fails at the call. Feedback and errors become chat lines, and a command the dispatcher
   does not know returns unhandled so the engine can send it to the server (a syntax error is
   reported instead). `FabricClientCommandSource` models `sendFeedback`, `sendError`, `getClient`,
   `getWorld`/`getLevel`, `getPlayer`, `getEntity`, `getMeta` and `attended`; the suggestion-provider
   surface and the position/rotation helpers are not present yet.
8. **Nested game types are named by their binary name in this project's source**
   (`ChatType$Bound`). The generated shim omits the `InnerClasses` attribute, so Java cannot
   write `ChatType.Bound`; the compiled descriptor is the same.

## Not present yet

`fabric-screen-api-v1`, `fabric-rendering-v1`, `fabric-item-api-v1`,
`fabric-item-group-api-v1`, `fabric-object-builder-api-v1`, `fabric-registry-sync-v0`,
`fabric-resource-loader-v1`, `fabric-client-tags-api-v1`, `fabric-transfer-api-v1`, networking
events, client-level entity/chunk events and every server-side event. The order is in FerroLoom's
design §6.1 and §6.8; the full fork is Plan 4.

## Targets checked

| Mod | Version | Modules it needs | Works |
|---|---|---|---|
| ZoomX | 26.3 | key-binding, lifecycle | Yes, key and FOV: the key registers, the `Camera.calculateFov` mixin is on the live FOV path, and `fov_modifier` goes 1.0 → 0.3333. Its `MouseHandler.onScroll` and `ItemInHandRenderer.submitHandsWithItems` mixins are inert: the engine calls neither method (no call site). Scroll zoom and hiding the hands do nothing; the key zoom works. |