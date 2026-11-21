# Changelog

All notable changes to FerroFabric-API. The version number follows the FerroLoom convention:
`<minecraft>-ferro.<revision>`.

## [Unreleased]

## [26.3-ferro.1] — 2026-11-20

First release, with the modules FerroLoom Plan 3 needs to run an unmodified third-party zoom mod.

### Added

- `fabric-api-base`: the real `Event`/`EventFactory` with `invoker()`, phases and phase ordering,
  plus `AutoInvokingEvent`, `TriState`, `EventResult`, `BooleanFunction` and `NbtType` (adapted
  from Fabric API, Apache-2.0).
- `fabric-key-binding-api-v1`: `KeyMappingHelper.registerKeyMapping`, mapping a `KeyMapping` to
  the engine's `Registrar` (translation key to `<mod>_<key>`, 26.3 SDL/HID scancode to a Horizon
  key name). The key is rebindable in Options > Controls.
- `fabric-lifecycle-events-v1`: `ClientTickEvents` with START/END client tick and level tick,
  `ClientLifecycleEvents` (CLIENT_STARTED/CLIENT_STOPPING) and `ClientLevelEvents`
  (AFTER_CLIENT_LEVEL_CHANGE), fired from the engine's client hooks.
- `dev.ferroloom.fabric.impl.FerroFabricApi.init`, the bridge FerroLoom calls after Knot starts.
- The jar is labelled `fabric-api` `26.3-ferro.1` when FerroLoom adds it as a builtin mod, and each module id in `META-INF/ferrofabric/modules.json` is a builtin mod of its own.
- JUnit tests for event dispatch, key registration and the engine tick forwarder.
- CI that checks out FerroLoom, builds its shim and runtime, and runs the suite.