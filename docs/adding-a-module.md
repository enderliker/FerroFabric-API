# Adding a module

A module enters when a target Fabric mod needs it. This keeps the port honest: every class is
there because a real jar calls it, and every one has a test.

## 1. Find what the mod calls

Put the 26.3 jar in a scratch directory and list its references:

```sh
javap -v -p -classpath mod.jar <classes> | grep -E "// (Method|Field) net/fabricmc"
```

Group them by Fabric API module (`fabric-key-binding-api-v1`, `fabric-lifecycle-events-v1`, ...).
A mod that mixes into a game class also needs a live call site for that method in FerroLoom; open
the FerroLoom issue first if it does not exist.

## 2. Port the public classes

Copy the package and class names from Fabric API. Keep the module's semantics, not only its
signatures:

- When does the event fire? What does it pass? What may a listener do inside it?
- What happens to a listener that throws? (Here: logged, the others run.)
- What is registered, with what id, and who reads it?

Compile against FerroLoom's shim. A member the shim does not implement throws
`FerroUnimplementedException` at the call, naming the member; add the shim row in FerroLoom rather
than working around it here.

## 3. Wire it

- If the module needs a game call that does not exist, it needs a FerroLoom call site (design
  §6.5). Until that lands, the module is not ready.
- If it needs state pushed from the engine, it belongs on `FerroEvents` or `ShimRuntime` in
  FerroLoom; the API does not read the engine on its own.
- Update `FerroFabricApi.init` only for a new engine-to-event bridge.

## 4. Test

Add a JUnit test in `src/test/java` that fails without the module. Event dispatch, key
registration and the tick forwarder are unit-testable; the game is not needed. For a module that
only works in the game, add the FerroLoom in-game evidence to the pull request.

## 5. Document

- Add the module to the README table and `docs/compatibility.md`.
- Record every deviation, with the reason a mod can notice.
- Update `CHANGELOG.md` under Unreleased.