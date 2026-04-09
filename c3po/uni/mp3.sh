#!/usr/bin/env bash

set -euo pipefail

RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
RESET=$(tput sgr0)

die() {
  echo "${RED}$*${RESET}" >&2
  exit 1
}

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || die "$1 is required"
}

usage() {
  cat <<EOF
Usage: uni mp3 <command> <file>

Commands:
  probe <file>   Show ffprobe info (json)
  tags  <file>   Show ID3 tags
  clear <file>   Remove ID3 tags
  help           Show this help
EOF
}

cmd_probe() {
  local file="$1"
  need_cmd ffprobe
  ffprobe -v quiet -print_format json -show_format "$file"
}

cmd_tags() {
  local file="$1"
  need_cmd id3v2
  id3v2 -l "$file"
}

cmd_clear() {
  local file="$1"
  need_cmd id3v2
  id3v2 -D "$file"
  echo "${GREEN}Tags removed${RESET}"
}

cmd_help() {
  usage
}

main() {
  local cmd="${1:-help}"
  shift || true

  case "$cmd" in
    probe)  [[ $# -eq 1 ]] || die "probe requires file";  cmd_probe "$@" ;;
    tags)   [[ $# -eq 1 ]] || die "tags requires file";   cmd_tags "$@" ;;
    clear)  [[ $# -eq 1 ]] || die "clear requires file";  cmd_clear "$@" ;;
    help|-h|--help) cmd_help ;;
    *) die "Unknown command: $cmd" ;;
  esac
}

main "$@"
