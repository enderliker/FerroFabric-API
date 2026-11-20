# Contributing to FerroFabric-API

Thanks for helping. This project is the Fabric API compatibility layer for
[FerroLoom](https://github.com/enderliker/FerroLoom): the Java classes a Fabric mod links against,
implemented over FerroLoom's `net.minecraft.*` shim and JNI bridge. It is Apache-2.0 licensed (see
[LICENSE](LICENSE)): use it, modify it, fork it and contribute.

## Before you start

- Open an issue (or a draft pull request) before large changes, so we agree on the approach.
- A new module is a port of a published Fabric API module, under its package and class names.
  Describe what a target mod needs; do not add API surface speculatively.
- Do not copy Fabric API source blindly. Adapted code is welcome when it keeps its Apache-2.0
  headers and the attribution in [NOTICE](NOTICE); prefer reimplementing against the shim. Never
  copy Mojang code or assets.
- Comments and documentation are written in English.

## Development setup

Prerequisites: a JDK 25+, network access for Gradle on the first run, and FerroLoom checked out
beside this repository (the default `../FerroLoom`; override with `FERROLOOM_REPO` or
`-PferroRepo`).

```sh
./build.sh test     # builds FerroLoom's shim and runtime, then this jar and its tests
```

FerroLoom's shim generator needs the official 26.3 client jar; run
`(cd ../FerroLoom && ./tools/fetch-runtime.sh)` once.

## How to contribute

1. Fork the repository and create a topic branch.
2. Add the change with a test, following the rules below.
3. Open a pull request against `main` and paste the test output.

Every commit must be signed off with the Developer Certificate of Origin:

```sh
git commit -s
```

The sign-off certifies that you wrote the change or have the right to submit it under the
project's licence. See <https://developercertificate.org/>.

## Pull requests

- One topic per pull request. Small, reviewable commits with conventional messages
  (`feat:`, `fix:`, `test:`, `docs:`, `chore:`).
- Include a test that fails without the change. Event dispatch, key registration and the
  API-to-shim calls are unit-testable without the game.
- If the change relies on new shim members, link the FerroLoom pull request that adds them and
  note it in [docs/compatibility.md](docs/compatibility.md).
- Run `./build.sh test` before opening the PR and paste the result in the description.
- Update the README, `docs/` and [docs/compatibility.md](docs/compatibility.md) when behavior or
  coverage changes.

## Adding a module

1. Read FerroLoom's design (§6.1, §6.8) and the module's upstream Fabric API source.
2. Port the public classes under `net.fabricmc.fabric.api.*`; compile them against FerroLoom's
   shim (`../FerroLoom/java/ferroloom-shim`).
3. Keep module semantics, not only signatures: when an event fires, what it passes, and what a
   listener may do inside it.
4. If the upstream module has mixins, they need a live call site in the engine. Propose it in
   FerroLoom first; until then the module is not ready.
5. Record the deviations and the modules present in
   [docs/compatibility.md](docs/compatibility.md), and add the module to the README table.

## Style

- Java: release 25 bytecode, JUnit 5, no runtime dependencies beyond the JDK and FerroLoom's
  runtime classes.
- Keep files focused; one public responsibility per file.
- A failing listener is logged and the others still run; an error names the event and the class.