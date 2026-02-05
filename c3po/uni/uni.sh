#!/bin/bash

BASE_DIR=$(dirname "$0")
RED=$(tput setaf 1)
RESET=$(tput sgr0)

if [ -z "$1" ]; then
  echo "${RED}At least 1 argument required. Use 'uni help' for a list of commands${RESET}"
  exit 1
fi

CMD="$1"
shift

SCRIPT_NAME="$BASE_DIR/$CMD.sh"

if [ -f "$SCRIPT_NAME" ]; then
  if [ ! -x "$SCRIPT_NAME" ]; then
    chmod +x "$SCRIPT_NAME"
  fi
  "$SCRIPT_NAME" "$@"
else
  echo "${RED}$CMD.sh not found${RESET}"
fi
