#!/usr/bin/env bash
# One-time, per-machine setup for the game7-bot identity.
#
# The GitHub App is account-scoped, so the SAME key works on any machine. What
# must exist on each machine is the private key + the gh-bot-token helper + a
# git credential helper that authenticates pushes as the bot. This script
# installs all three and verifies (idempotent).
#
# Usage:
#   scripts/setup-bot.sh [path-to-game7-bot.pem]
#   (defaults to ~/game7-bot.pem; run inside the devcontainer too, so the
#    container's home volume gets the key + helper as well)
#
# Key provisioning convention: retrieve the key from the password manager and
# save it to ~/game7-bot.pem (chmod 600). It is a secret and is never committed.
set -euo pipefail

KEY="${1:-$HOME/game7-bot.pem}"
DEST="$HOME/game7-bot.pem"
[ -f "$KEY" ] || { echo "error: private key not found at $KEY (pass its path as the first argument)" >&2; exit 1; }

# gh may be installed but not on PATH in non-login shells.
if ! command -v gh >/dev/null 2>&1; then
  [ -x "$HOME/.local/bin/gh" ] && export PATH="$HOME/.local/bin:$PATH"
fi
command -v gh >/dev/null 2>&1 || { echo "error: gh CLI is required (install it first)" >&2; exit 1; }

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
BOT_HELPER="$HOME/.local/bin/gh-bot-token"

# Install the key (idempotent if it's already at the destination) and the helper.
mkdir -p "$HOME/.local/bin"
if [ "$KEY" != "$DEST" ]; then
  install -m 600 "$KEY" "$DEST"
else
  chmod 600 "$DEST"
fi
install -m 700 "$SCRIPT_DIR/gh-bot-token" "$BOT_HELPER"

# Make plain `git push` authenticate as the bot on github.com. gh auth login may
# have installed its own helper here; replace it so the bot is the only helper
# (git would otherwise merge helpers and prefer the personal token).
git config --global --unset-all credential.https://github.com.helper || true
git config --global "credential.https://github.com.helper" \
  '!f(){ echo "username=x-access-token"; echo "password=$($HOME/.local/bin/gh-bot-token)"; }; f'

echo "Verifying bot identity..."
GH_TOKEN="$($BOT_HELPER)" gh api /installation/repositories \
  --jq '.repositories[].full_name' | sed 's/^/  /'

echo "OK: game7-bot ready on this machine (key: $DEST, helper: $BOT_HELPER)"
echo "Plain 'git push' now authenticates as the bot; commit authors are still"
echo "explicit per-commit (see AGENTS.md)."