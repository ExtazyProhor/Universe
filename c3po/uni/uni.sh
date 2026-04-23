#!/usr/bin/env bash
set -euo pipefail

# dev | prod
MODE=dev

if [[ -t 1 ]] && command -v tput >/dev/null 2>&1; then
  RED=$(tput setaf 1)
  GREEN=$(tput setaf 2)
  YELLOW=$(tput setaf 3)
  RESET=$(tput sgr0)
else
  RED=""
  GREEN=""
  YELLOW=""
  RESET=""
fi

info() { echo "${YELLOW}$*${RESET}"; }
ok()   { echo "${GREEN}$*${RESET}"; }
err()  { echo "${RED}$*${RESET}" >&2; }

BASE_DIR="${UNIVERSE_HOME}/c3po/uni"
CACHE_DIR="$BASE_DIR/.uni-cache"
VERSION_FILE="$CACHE_DIR/version"
CP_FILE="$CACHE_DIR/classpath"

mkdir -p "$CACHE_DIR"

if [[ ! -d "$BASE_DIR" ]]; then
  err "Base dir not found: $BASE_DIR"
  exit 1
fi

if ! command -v mvn >/dev/null 2>&1; then
  err "mvn not found in PATH"
  exit 2
fi

NEED_BUILD=0
CURRENT_VERSION=$(grep -m1 "<version>" "$BASE_DIR/pom.xml" | sed -E 's/.*<version>(.*)<\/version>.*/\1/')
CACHED_VERSION=""
[[ -f "$VERSION_FILE" ]] && CACHED_VERSION=$(cat "$VERSION_FILE")

if [[ "$CURRENT_VERSION" != "$CACHED_VERSION" ]]; then
  NEED_BUILD=1
fi

if [[ "$MODE" == "dev" ]]; then
  SRC_HASH=$(find "$BASE_DIR/src/main/kotlin" -type f -name "*.kt" \
      -exec sh -c 'stat -c "%Y %n" "$1" 2>/dev/null || stat -f "%m %N" "$1"' _ {} \; | \
      sort | \
      shasum | \
      awk '{print $1}')
  HASH_FILE="$CACHE_DIR/hash"
  CACHED_HASH=""
  [[ -f "$HASH_FILE" ]] && CACHED_HASH=$(cat "$HASH_FILE")
  [[ "$SRC_HASH" != "$CACHED_HASH" ]] && NEED_BUILD=1
fi

if [[ "$NEED_BUILD" == "1" ]]; then
  info "Building (version $CURRENT_VERSION)..."
  (
    cd "$BASE_DIR"
    mvn -q -DskipTests compile
    mvn -q dependency:build-classpath -Dmdep.outputFile="$CP_FILE"
  ) || {
    err "Build failed"
    exit 3
  }

  if [[ "$MODE" == "dev" ]]; then
    echo "$SRC_HASH" > "$HASH_FILE"
  fi
  echo "$CURRENT_VERSION" > "$VERSION_FILE"
  ok "Build complete"
fi

if [[ ! -f "$CP_FILE" ]]; then
  err "Classpath file not found: $CP_FILE"
  exit 4
fi

CLASSPATH="$BASE_DIR/target/classes:$(cat "$CP_FILE")"

exec java \
  -XX:TieredStopAtLevel=1 \
  --enable-native-access=ALL-UNNAMED \
  -cp "$CLASSPATH" ru.prohor.universe.uni.cli.MainKt "$@"
