#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="${UNIVERSE_HOME}/jawa"
JAR="$BASE_DIR/uni/target/uni.jar"

if [[ ! -f "$JAR" ]]; then
  cd "$BASE_DIR"
  mvn -q -pl uni -am -DskipTests package
fi

exec java \
  -XX:TieredStopAtLevel=1 \
  --enable-native-access=ALL-UNNAMED \
  -jar "$JAR" "$@"
