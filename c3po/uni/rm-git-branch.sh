#!/bin/bash

MAGENTA=$(tput setaf 5)
YELLOW=$(tput setaf 3)
RESET=$(tput sgr0)

branches=$(git branch --merged main | sed 's/^ *//' | grep -Ev '^(main|\* main)$')

for branch in $branches; do
  read -p "${YELLOW}Delete branch ${MAGENTA}$branch?${YELLOW} (y to delete / Enter to skip):${RESET} " answer
  if [[ "$answer" == "y" || "$answer" == "Y" ]]; then
    git branch -d "$branch"
  fi
done
