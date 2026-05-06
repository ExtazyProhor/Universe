#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="${UNIVERSE_HOME}/c3po/uni"
CP_FILE="$BASE_DIR/.uni-cache/classpath"

if [[ ! -f "$CP_FILE" ]]; then
  cd "$BASE_DIR"
  mvn -q -DskipTests compile
  mvn -q dependency:build-classpath -Dmdep.outputFile="$CP_FILE"
fi

CLASSPATH="$BASE_DIR/target/classes:$(cat "$CP_FILE")"

exec java \
  -XX:TieredStopAtLevel=1 \
  --enable-native-access=ALL-UNNAMED \
  -cp "$CLASSPATH" ru.prohor.universe.uni.cli.MainKt "$@"
