#!/usr/bin/env bash
# Boots a dedicated dev server for one loader, generates chunks in the Gingerbread Grove, runs a few
# checks, stops the server, and prints any errors. Catches broken worldgen JSON, missing registry
# entries and client-only classes leaking into the server.
#
# Usage: tools/smoke-test.sh fabric|neoforge [extra server commands...]
# Requires runs/server/eula.txt with eula=true in the loader folder.
set -u
loader="${1:?usage: smoke-test.sh fabric|neoforge [commands...]}"
shift
cd "$(dirname "$0")/.."
log="build/smoke-$loader.log"
mkdir -p build
: > "$log"

g="execute in gingerbread_grove:gingerbread_grove run"
commands=(
  "$g forceload add -64 -64 64 64"
  "@wait 45"
  "$g locate biome gingerbread_grove:gingerbread_grove"
  "$@"
)

(
  for _ in $(seq 1 180); do grep -qE 'Done \(|Exception|FAILURE|BUILD FAILED' "$log" && break; sleep 2; done
  for c in "${commands[@]}"; do
    if [[ "$c" == @wait* ]]; then sleep "${c#@wait }"; else echo "$c"; sleep 3; fi
  done
  echo stop
  sleep 40
) | timeout 900 ./gradlew ":$loader:runServer" --console=plain > "$log" 2>&1
status=$?

echo "== $loader server exit: $status   (full log: $log)"
grep -nE 'Done \(|/ERROR|Exception|Caused by|Failed to|Couldn.t|Unknown|Marked|nearest|Placed|There is no|BUILD' "$log" \
  | grep -vE 'Problems report' | cut -c1-260 | head -60
