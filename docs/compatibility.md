# Compatibility

What this release covers, and where it differs from upstream Fabric API. FerroLoom's own record
is in
[`docs/ferrofabric-deviations.md`](https://github.com/enderliker/FerroLoom/blob/master/docs/ferrofabric-deviations.md);
this page is the API's view.

## Modules present

| Module | Public classes | Notes |
|---|---|---|
| `fabric-event` | `net.fabricmc.fabric.api.event.Event` | `register(T)`; `dispatch(Consumer<T>)` added for modules. No `invoker()` factory. A failing listener is logged and the others run. |
| `fabric-key-binding-api-v1` | `net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper` | `registerKeyMapping(KeyMapping)` registers with the engine's `Registrar`. Constructor registration and the helper are idempotent per (mod, key). |
| `fabric-lifecycle-events-v1` | `net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents` | `END_CLIENT_TICK` only, fired from the engine tick after the key state is pushed. |

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
6. **`Event` has no listener priorities or phases.** Registration order is dispatch order.

## Not present yet

`fabric-screen-api-v1`, `fabric-rendering-v1`, `fabric-item-api-v1`,
`fabric-item-group-api-v1`, `fabric-object-builder-api-v1`, `fabric-registry-sync-v0`,
`fabric-resource-loader-v0`, `fabric-client-tags-api-v1`, `fabric-transfer-api-v1`, networking
events, `START_CLIENT_TICK`, world load/unload and every server-side event. The order is in
FerroLoom's design §6.1 and §6.8; the full fork is Plan 4.

## Targets checked

| Mod | Version | Modules it needs | Works |
|---|---|---|---|
| ZoomX | 26.3 | key-binding, lifecycle | Yes, key and FOV: the key registers, the `Camera.calculateFov` mixin is on the live FOV path, and `fov_modifier` goes 1.0 → 0.3333. Its `MouseHandler.onScroll` and `ItemInHandRenderer.submitHandsWithItems` mixins are inert: the engine calls neither method (no call site). Scroll zoom and hiding the hands do nothing; the key zoom works. |