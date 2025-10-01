#!/bin/bash

BASE_DIR=$(dirname "$0")
RED=$(tput setaf 1)
RESET=$(tput sgr0)

if [ -z "$1" ]; then
  echo "${RED}At least 1 argument required. Use 'uni help' for a list of commands${RESET}"
  exit 1
fi

SCRIPT_NAME="$BASE_DIR/$1.sh"

if [ -f "$SCRIPT_NAME" ]; then
  if [ ! -x "$SCRIPT_NAME" ]; then
    chmod +x "$SCRIPT_NAME"
  fi
  "$SCRIPT_NAME"
else
  echo "${RED}$1.sh not found${RESET}"
fi
