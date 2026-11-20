# Security

FerroFabric-API runs inside the JVM FerroLoom embeds in Horizon, with the player's permissions.
It executes or is executed by arbitrary Java code from the jars in `ferroloom-mods/`; there is no
sandbox. Treat a Fabric jar like any other executable.

## Reporting a vulnerability

Do not open a public issue. Use GitHub's private vulnerability reporting ("Security" tab →
"Report a vulnerability") on this repository, or contact the maintainer directly. Include the
FerroFabric-API commit, the FerroLoom commit, the Horizon build, the JDK and a reproduction.

## Scope

- The API-to-shim boundary: values read from the engine and what a mod may do with them.
- Event dispatch: one listener must not be able to stop the others or escape the callback window.
- Key registration: the translation key to Registrar id mapping and the 26.3 scancode to Horizon
  key name mapping.
- Class loading: this jar must stay on Knot's class path and must not leak onto the parent class
  path next to FerroLoom's own loader classes.

## Supported versions

Only the current `main` receives fixes. Versions are tied to a FerroLoom/Horizon build.