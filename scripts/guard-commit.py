#!/usr/bin/env python3
"""PreToolUse guard: block AI-watermarked commits and ticket work on main."""
import json
import os
import re
import subprocess
import sys

# Deliberate ceiling: scans the whole command, so -m and heredoc bodies are
# caught and only -F <file> messages escape. Deliberate strictness: a clean
# commit that mentions a pattern elsewhere on the command line also trips.
# The co-author pattern ignores case because tools spell the trailer
# differently (Claude Code writes Co-Authored-By); the other patterns stay
# case-sensitive, matching the case harness injections write.
PATTERNS = (
    r"(?i:Co-authored-by:[^\n]*(?:claude|anthropic))",
    r"Claude-Session",
    r"Generated with[^\n]*[Cc]laude",
    r"claude\.ai",
    r"\U0001F916",
)

# Config repos: rule edits are config sync, exempt per rules/git.md.
ALLOWLIST = {"flow"}
# Deliberate ceiling: main/master by name, no origin/HEAD lookup.
DEFAULT_BRANCHES = {"main", "master"}


def git_in(directory, *args):
    result = subprocess.run(["git", "-C", directory, *args],
                            capture_output=True, text=True)
    return result.stdout.strip() if result.returncode == 0 else None


def commit_dir(command):
    directory = os.getcwd()
    cd = re.search(r"(?:^|&&|;)\s*(?:\w+=\S*\s+)*cd\s+([^\s;&|]+)", command)
    if cd:
        directory = os.path.expanduser(cd.group(1))
    git_c = re.search(r"\bgit\s+-C\s+([^\s;&|]+)[^\n|;&]*\bcommit\b", command)
    if git_c:
        directory = os.path.join(directory, os.path.expanduser(git_c.group(1)))
    return directory


def branch_violation(command):
    if "FLOW_ALLOW_MAIN=1" in command:
        return None
    directory = commit_dir(command)
    top = git_in(directory, "rev-parse", "--show-toplevel")
    if top is None or os.path.basename(top) in ALLOWLIST:
        return None
    branch = git_in(directory, "symbolic-ref", "--short", "HEAD")
    if branch in DEFAULT_BRANCHES:
        return branch
    return None


def main():
    event = json.load(sys.stdin)
    if event.get("tool_name") != "Bash":
        sys.exit(0)
    command = event.get("tool_input", {}).get("command", "")
    if not re.search(r"\bgit\b[^\n|;&]*\bcommit\b", command):
        sys.exit(0)
    for pattern in PATTERNS:
        if re.search(pattern, command):
            print("BLOCKED: commit carries an AI watermark "
                  f"(matched {pattern!r}). rules/git.md forbids "
                  "this — rewrite the message without any AI attribution.",
                  file=sys.stderr)
            sys.exit(2)
    branch = branch_violation(command)
    if branch is not None:
        print(f"BLOCKED: commit on {branch!r}. Ticket work lands on a branch "
              "and reaches main via PR (rules/git.md): "
              "git worktree add ../<repo>:worktree/<repo>-<ticket id> "
              "-b <intent>/<ticket id>/<description>. "
              "Deliberate override: prefix FLOW_ALLOW_MAIN=1.",
              file=sys.stderr)
        sys.exit(2)
    sys.exit(0)


if __name__ == "__main__":
    main()
