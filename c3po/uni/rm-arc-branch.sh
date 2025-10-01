#!/bin/bash

MAGENTA=$(tput setaf 5)
YELLOW=$(tput setaf 3)
RESET=$(tput sgr0)

branches=$(arc branch --merged trunk | sed 's/^ *//' | grep -Ev '^(trunk|\* trunk)$')

for branch in $branches; do
  read -p "${YELLOW}Delete branch ${MAGENTA}$branch?${YELLOW} (y to delete / Enter to skip):${RESET} " answer
  if [[ "$answer" == "y" || "$answer" == "Y" ]]; then
    arc branch -d "$branch"
  fi
done
