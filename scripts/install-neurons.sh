#!/usr/bin/env bash
set -euo pipefail

# One source of truth: run the neurons repo's own installer. It handles
# gh auth, both binaries (neuron-mcp AND the neuron CLI that owns
# `seed --update`), and MCP registration. This wrapper only fetches it -
# the repo is private, so everything goes through gh. A machine that cannot
# read it skips neurons, so /flow:sync still finishes.

REPO="trungth1406/neurons"

skip_neurons() {
  echo "neurons skipped: $1. neurons installs from $REPO, which stays private until its public release."
  exit 0
}

command -v gh >/dev/null || skip_neurons "gh is not installed (https://cli.github.com)"
# gh's own first error line says why: not logged in, no access (404), or offline.
gh_error=$(gh api "repos/$REPO" --silent 2>&1) || skip_neurons "gh cannot read $REPO: ${gh_error%%$'\n'*}"

gh api "repos/$REPO/contents/install.sh" -H "Accept: application/vnd.github.raw" | bash
