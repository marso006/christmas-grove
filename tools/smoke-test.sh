#!/usr/bin/env bash
# Boots a dedicated dev server for one loader, generates chunks in the Christmas Grove, runs a few
# checks, stops the server, and prints any errors. Catches broken worldgen JSON, missing registry
# entries and client-only classes leaking into the server.
#
# Usage: tools/smoke-test.sh fabric|neoforge [extra server commands...]
#        MC_TARGET=1.21 tools/smoke-test.sh fabric   (test another version from versions/*.properties;
#                                                     uses a fresh world, deleting the old dev one)
# Requires runs/server/eula.txt with eula=true in the loader folder.
set -u
loader="${1:?usage: smoke-test.sh fabric|neoforge [commands...]}"
shift
cd "$(dirname "$0")/.."
log="build/smoke-$loader.log"
mkdir -p build
: > "$log"

if [ -n "${MC_TARGET:-}" ]; then
  rm -rf "$loader/runs/server/world"   # worlds can't be opened by an older Minecraft version
  runner=(tools/build-target.sh "$MC_TARGET" ":$loader:runServer")
else
  runner=(./gradlew ":$loader:runServer" --console=plain)
fi

g="execute in christmas_grove:christmas_grove run"
commands=(
  "$g forceload add -64 -64 64 64"
  "@wait 45"
  "$g locate biome christmas_grove:gingerbread_grove"
  "$@"
)

(
  for _ in $(seq 1 180); do grep -qE 'Done \(|Exception|FAILURE|BUILD FAILED' "$log" && break; sleep 2; done
  for c in "${commands[@]}"; do
    if [[ "$c" == @wait* ]]; then sleep "${c#@wait }"; else echo "$c"; sleep 3; fi
  done
  echo stop
  sleep 40
) | timeout 900 "${runner[@]}" > "$log" 2>&1
status=$?

echo "== $loader server exit: $status   (full log: $log)"
grep -nE 'Done \(|/ERROR|Exception|Caused by|Failed to|Couldn.t|Unknown|Marked|nearest|Placed|There is no|BUILD' "$log" \
  | grep -vE 'Problems report' | cut -c1-260 | head -60
