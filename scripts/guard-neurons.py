#!/usr/bin/env python3
"""PreToolUse guard: blocks git commit if no neurons graph was touched.

Checks the newest neurons db modified time. If no db was written in the
last 2 hours, the commit is blocked with guidance. A machine without neurons
(no graph db, and no neuron-mcp on PATH or in ~/.cargo/bin) is not checked.
Only fires on git commit commands (same pattern as guard-commit.py).
"""
import json
import os
import shutil
import sys
import time
from pathlib import Path


def main():
    data = json.load(sys.stdin)
    tool = data.get("tool_name", "")
    if tool != "Bash":
        return

    cmd = data.get("tool_input", {}).get("command", "")
    if "git commit" not in cmd:
        return

    if "FLOW_SKIP_NEURON_CHECK=1" in cmd:
        return

    home = Path.home()
    root = home / ".claude" / "neurons"
    dbs = list(root.glob("**/neurons.db*")) if root.exists() else []

    if not dbs:
        # neurons is not public yet, so most machines don't have it. Only a
        # machine with its MCP server installed is asked for a graph: on
        # PATH, or in ~/.cargo/bin where the neurons installer puts it.
        is_installed = (shutil.which("neuron-mcp") is not None
                        or (home / ".cargo/bin/neuron-mcp").exists())
        if not is_installed:
            return

        print("BLOCKED: no neurons graph exists on this machine.", file=sys.stderr)
        print("The neuron skill is mandatory: establish a thinking graph", file=sys.stderr)
        print("before committing. Use /neuron or the neurons MCP tools.", file=sys.stderr)
        print("Override: prefix FLOW_SKIP_NEURON_CHECK=1.", file=sys.stderr)
        sys.exit(2)

    # The MCP owner writes to the db of the directory the SESSION started
    # in, which is not necessarily this commit's worktree. Any project db
    # touched within the window counts as thinking done this session.
    freshest = min(time.time() - db.stat().st_mtime for db in dbs)
    if freshest > 7200:
        print("BLOCKED: no neurons db has been touched this session.", file=sys.stderr)
        print(f"Freshest write: {int(freshest // 60)} minutes ago", file=sys.stderr)
        print("Write to your thinking graph before committing.", file=sys.stderr)
        print("Override: prefix FLOW_SKIP_NEURON_CHECK=1.", file=sys.stderr)
        sys.exit(2)


if __name__ == "__main__":
    main()
