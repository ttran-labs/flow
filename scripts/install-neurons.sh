#!/usr/bin/env bash
set -euo pipefail

# Installs neuron and neuron-mcp from the public ttran-labs/neurons release
# and registers the neurons MCP server. tool-release writes tools.lock there:
# one "<version> <target> <url> <sha256>" line per platform.

LOCK_URL="https://raw.githubusercontent.com/ttran-labs/neurons/main/tools.lock"
BIN_DIR="${NEURON_BIN_DIR:-$HOME/.cargo/bin}"

fail() {
  echo "neurons install failed: $1" >&2
  exit 1
}

case "$(uname -s) $(uname -m)" in
  "Darwin arm64") target=aarch64-apple-darwin; sha256_command=(shasum -a 256) ;;
  "Linux x86_64") target=x86_64-unknown-linux-gnu; sha256_command=(sha256sum) ;;
  *) fail "$(uname -s) $(uname -m) is not supported (only aarch64-apple-darwin and x86_64-unknown-linux-gnu)" ;;
esac

lock=$(curl -fsSL "$LOCK_URL") || fail "cannot read $LOCK_URL"
read -r released_version _ url expected_sha256 < <(awk -v target="$target" '$2 == target' <<<"$lock") ||
  fail "$LOCK_URL lists no $target release"

installed_version=$("$BIN_DIR/neuron-mcp" --version 2>/dev/null | awk '{print $2}') || true
if [ "$installed_version" = "$released_version" ] && [ -x "$BIN_DIR/neuron" ]; then
  echo "neurons $released_version already installed in $BIN_DIR"
else
  mkdir -p "$BIN_DIR"
  # Staged inside the bin dir so mv is a rename: a running neuron-mcp keeps
  # its old file instead of being overwritten in place.
  staging_dir=$(mktemp -d "$BIN_DIR/.neurons-install.XXXXXX")
  trap 'rm -rf "$staging_dir"' EXIT
  asset=${url##*/}
  curl -fsSL -o "$staging_dir/$asset" "$url" || fail "cannot download $url"

  actual_sha256=$("${sha256_command[@]}" "$staging_dir/$asset" | awk '{print $1}')
  [ "$actual_sha256" = "$expected_sha256" ] || fail "sha256 of $asset does not match tools.lock"

  tar -xzf "$staging_dir/$asset" -C "$staging_dir" neuron neuron-mcp
  mv -f "$staging_dir/neuron" "$staging_dir/neuron-mcp" "$BIN_DIR/"
  echo "neurons $released_version installed in $BIN_DIR"
fi

command -v claude >/dev/null ||
  fail "claude is not on PATH; register the MCP server with: claude mcp add neurons -- $BIN_DIR/neuron-mcp"

# Read whole before grep: grep -q can exit before claude finishes writing,
# and under pipefail that failed write would read as not registered.
mcp_servers=$(claude mcp list)
grep -q '^neurons:' <<<"$mcp_servers" || claude mcp add neurons -- "$BIN_DIR/neuron-mcp"
