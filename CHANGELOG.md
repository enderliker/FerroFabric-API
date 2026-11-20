# Changelog

All notable changes to FerroFabric-API. The version number follows the FerroLoom convention:
`<minecraft>-ferro.<revision>`.

## [Unreleased]

## [26.3-ferro.1] — 2026-11-20

First release, with the modules FerroLoom Plan 3 needs to run an unmodified third-party zoom mod.

### Added

- `fabric-event`: `Event<T>` with `register(T)` and `dispatch(Consumer<T>)`; a failing listener is
  logged and the others run.
- `fabric-key-binding-api-v1`: `KeyMappingHelper.registerKeyMapping`, mapping a `KeyMapping` to
  the engine's `Registrar` (translation key to `<mod>_<key>`, 26.3 SDL/HID scancode to a Horizon
  key name). The key is rebindable in Options > Controls.
- `fabric-lifecycle-events-v1`: `ClientTickEvents.END_CLIENT_TICK`, fired from the engine's client
  tick after the key state is pushed.
- `dev.ferroloom.fabric.impl.FerroFabricApi.init`, the bridge FerroLoom calls after Knot starts.
- The jar is labelled `fabric-api` `26.3-ferro.1` when FerroLoom adds it as a builtin mod.
- JUnit tests for event dispatch, key registration and the engine tick forwarder.
- CI that checks out FerroLoom, builds its shim and runtime, and runs the suite.