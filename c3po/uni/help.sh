#!/bin/bash

BASE_DIR=$(dirname "$0")
RED=$(tput setaf 1)
RESET=$(tput sgr0)

set -euo pipefail

if ! command -v jq >/dev/null 2>&1; then
  echo "${RED}jq is required for execution${RESET}" >&2
  exit 2
fi

if [[ ! -f "${BASE_DIR}/desc.json" ]]; then
  echo "${RED}desc.json with command descriptions was not found${RESET}" >&2
  exit 3
fi

shopt -s nullglob

declare -a files
for f in ${BASE_DIR}/*.sh; do
  [[ "${f##*/}" = "uni.sh" ]] && continue
  files+=("$f")
done

if [[ ${#files[@]} -eq 0 ]]; then
  exit 0
fi

declare -a cmds
maxlen=0
for f in "${files[@]}"; do
  base="${f##*/}"
  cmd="${base%.sh}"
  cmds+=("$cmd")
  prefix="uni $cmd"
  len=${#prefix}
  (( len > maxlen )) && maxlen=$len
done

min_gap=3

for cmd in "${cmds[@]}"; do
  prefix="uni $cmd"
  padding=$(( maxlen - ${#prefix} + min_gap ))
  desc=$(jq -r --arg k "$cmd" '.[$k] // ""' ${BASE_DIR}/desc.json)
  if [[ -z "$desc" ]]; then
    printf '%s\n' "$prefix"
  else
    printf '%s%*s%s\n' "$prefix" "$padding" "" "$desc"
  fi
done
