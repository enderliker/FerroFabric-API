# FerroFabric-API

[![CI](https://github.com/enderliker/FerroFabric-API/actions/workflows/ci.yml/badge.svg)](https://github.com/enderliker/FerroFabric-API/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

The Fabric API compatibility layer for [FerroLoom](https://github.com/enderliker/FerroLoom), a
JVM host for Horizon (Minecraft 26.3 rewritten in Rust).

A Fabric mod is compiled against `net.fabricmc.fabric.api.*`. FerroLoom provides the
`net.minecraft.*` shim and the JNI bridge to the Rust engine; this project provides the API
classes those mods call, implemented over that shim and bridge. It plays the same role for
FerroLoom that Sinytra Connector's Forgified Fabric API plays for NeoForge: module by module,
under Fabric API's published package and class names, so a mod cannot tell the difference.

It is a **builtin mod**: Fabric Loader sees it as `fabric-api` version `26.3-ferro.1`, and each
implemented module id is its own builtin mod (from `META-INF/ferrofabric/modules.json`), so a
mod's `depends: fabric-api` or `depends: fabric-lifecycle-events-v1` resolves without the jar being
copied into the mods folder. FerroLoom's provider adds `ferrofabric-api-26.3-ferro.1.jar` to Knot's
class path through `-Dferro.apiJar` and lists the builtins in `getBuiltinMods`.

## Modules

| Module | Classes | What it does |
|---|---|---|
| `fabric-api-base` | `Event`, `EventFactory`, `TriState`, `EventResult` | The real event system: `invoker()`, `register(T)`, phases and `addPhaseOrdering`, ported from Fabric API. |
| `fabric-key-binding-api-v1` | `KeyMappingHelper` | `registerKeyMapping` registers with the engine's `Registrar` (duplicate throws); `getBoundKeyOf` returns the player's Controls binding, pushed by the engine each tick. |
| `fabric-lifecycle-events-v1` | `ClientTickEvents`, `ClientLifecycleEvents`, `ClientLevelEvents` | START/END client and level tick; CLIENT_STARTED/STOPPING; AFTER_CLIENT_LEVEL_CHANGE. Fired from the engine hooks after the pushed key state. |

The full Fabric API fork is the roadmap. A module enters when a target mod needs it and its
classes compile against the shim; what is missing is listed in
[docs/compatibility.md](docs/compatibility.md).

## How it fits

```
Fabric mod (Knot) → FerroFabric-API (Knot) → net.minecraft.* shim (Knot)
                                              ↓ JNI (bridge thread)
                                      FerroLoom runtime → Horizon engine
```

See [docs/architecture.md](docs/architecture.md) for the class-loader split, the registration
window and the event flow.

## Build

Requirements: JDK 25+, the FerroLoom checkout beside this repository (or `FERROLOOM_REPO`), and
network access for Gradle on the first run.

```sh
./build.sh          # jar
./build.sh test     # jar + JUnit tests
```

The result is `build/libs/ferrofabric-api-26.3-ferro.1.jar` and its sources jar. FerroLoom's
`tools/stage.sh` builds this project automatically when `../FerroFabric-API` exists and copies the
jar to `~/.local/share/horizon/ferroloom-lib/ferrofabric-api.jar`.

CI checks out FerroLoom beside this repository, builds its shim and runtime, and runs the suite.

## Status

Version `26.3-ferro.1`, client only. This is not a complete Fabric API: it covers the surface the
current target mods need. Compatible with FerroLoom Plan 3 (ZoomX 26.3) and later plans.
Deviations from upstream are recorded in [docs/compatibility.md](docs/compatibility.md).

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md). Every commit is signed off with the Developer
Certificate of Origin; the code is Apache-2.0.

## Licence

Apache License 2.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE). Why Apache-2.0: Fabric API,
whose public interface this project implements and whose modules it may later port, is
Apache-2.0. That keeps the porting path unambiguous, includes an explicit patent grant, and is
compatible with FerroLoom's MIT licence. The event system is adapted from Fabric API under the
Apache License 2.0 (see [NOTICE](NOTICE)); the rest is original code implementing its published
interfaces.