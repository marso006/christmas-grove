#!/usr/bin/env bash
# Builds this branch for another Minecraft version listed in versions/<mc>.properties, by passing
# each line as a -P override to Gradle (overrides beat gradle.properties).
#
# Usage: tools/build-target.sh <mc-version> [gradle tasks...]   (default task: build)
#   e.g. tools/build-target.sh 1.21
#        tools/build-target.sh 1.21 :fabric:runServer
set -euo pipefail
cd "$(dirname "$0")/.."
mc="${1:?usage: build-target.sh <mc-version> [tasks...]}"
shift
file="versions/$mc.properties"
[ -f "$file" ] || { echo "no $file"; exit 1; }
args=()
while IFS='=' read -r key value; do
  [[ -z "$key" || "$key" == \#* ]] && continue
  args+=("-P$key=$value")
done < "$file"
tasks=("$@")
[ ${#tasks[@]} -eq 0 ] && tasks=(build)
exec ./gradlew "${args[@]}" "${tasks[@]}" --console=plain
