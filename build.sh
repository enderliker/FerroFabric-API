#!/usr/bin/env bash
# Builds FerroFabric-API beside FerroLoom; `build.sh test` runs its tests too.
#
# FerroLoom's shim and runtime jars must exist first; this script builds them when the checkout
# is a sibling. Set FERROLOOM_REPO (or -PferroRepo for Gradle) to use another checkout.
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
if [ -n "${FERROLOOM_REPO:-}" ]; then
    FERRO="$(cd "$FERROLOOM_REPO" && pwd)"
else
    FERRO="$(cd "$HERE/../FerroLoom" && pwd)"
fi
TASK="${1:-build}"

(cd "$FERRO/java" && ./gradlew -q :ferroloom-runtime:jar :ferroloom-shim:jar)
"$HERE/gradlew" -q -p "$HERE" -PferroRepo="$FERRO" "$TASK"
echo "ferrofabric-api: $HERE/build/libs"