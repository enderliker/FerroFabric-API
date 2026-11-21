# Architecture

FerroFabric-API is the Java face of [FerroLoom](https://github.com/enderliker/FerroLoom). Horizon
is Minecraft 26.3 rewritten in Rust and has no Java Minecraft classes, so FerroLoom supplies two
Java pieces: the generated `net.minecraft.*` shim, whose members delegate to Rust over JNI, and
this API, whose classes are what a Fabric mod calls.

```
┌──────────────────────── JVM (Knot class loader) ────────────────────────┐
│  Fabric mod  ──calls──▶  FerroFabric-API  ──calls──▶  net.minecraft.*  │
│  (Knot)                 (this project)                shim (Knot)      │
│                                                         │              │
│  dev.ferroloom.api / ShimRuntime (parent class loader)  │              │
└─────────────────────────────────────────────────────────┼──────────────┘
                                                          │ JNI
                                            FerroLoom runtime (Rust)
                                                          │ hooks
                                                     Horizon engine
```

## Class-loader split

- **Knot** defines the game-facing jars: the shim and FerroFabric-API. Mixin can transform the
  shim, and a mod sees both with one class identity.
- **The parent class loader** keeps Fabric Loader, its libraries, FerroLoom's loader and its
  runtime (`dev.ferroloom.api`, `FerroRuntime`, `FerroNative`). FerroLoom's provider keeps those
  jars on the parent class path and filters the game-facing jars out of it: the shim (by game
  classes) and this API (by its `-Dferro.apiJar` path) are defined by Knot.
- The API jar is not in the mods folder. The provider adds it to Knot from `-Dferro.apiJar` and
  registers it as a `BuiltinMod` (`fabric-api`, `26.3-ferro.1`), so dependency resolution sees it
  and Fabric Loader does not scan it twice.

## Registration window

Horizon accepts key, command and layer declarations only during Registration, through
`&mut dyn Registrar`. Fabric's normal lifecycle runs `main` entrypoints at registration and
`client` entrypoints at client setup, but a Fabric mod registers its key in
`ClientModInitializer.onInitializeClient`. FerroLoom therefore runs both entrypoint phases during
Registration (`docs/compatibility.md`), collects the API's registrations, and returns them to Rust
in one report.

`KeyMappingHelper.registerKeyMapping` (and the `KeyMapping` constructor itself) records
`KEY <mod> <key> <default>` for the report. Rust registers it with the engine's `Registrar` under
the FerroLoom mod id and polls `ClientHost::is_down("ferroloom", "<mod>_<key>")` each tick.

## Events

`ClientTickEvents.END_CLIENT_TICK` is fired from `FerroFabricApi.init`'s listener on
`FerroEvents.TICKS`. FerroLoom's `GameBridge.onTick` pushes the player position, then dispatches
the tick inside a callback window; Rust's key dispatch reads the engine and calls
`GameBridge.onKey`, which records the key state before that tick. The API fires the Fabric event
inside the window. `Event.dispatch` catches a throwing listener, logs it and runs the rest.

## Key state

`GameBridge.onKey(modId, keyId, down)` records the state in `ShimRuntime` (keyed by
`<mod>_<key>`). `KeyMapping.isDown()` derives the same id from its translation key
(`key.<mod>.<key>`) and reads it. `KeyMapping` construction maps the 26.3 key code (an SDL/HID
scancode: `KEY_C` is 6, `KEY_V` is 25) to Horizon's key name (`key.keyboard.c`,
`key.keyboard.v`).

## Adding behaviour

The API never reads the engine directly. Everything goes through the shim (which checks that the
call happens inside a callback) or through a `dev.ferroloom.api` class FerroLoom exposes. See
[adding-a-module.md](adding-a-module.md).