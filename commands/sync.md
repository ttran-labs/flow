---
description: Install the flow setup (CLAUDE.md + rules + bundled skills) into ~/.claude, overriding local copies
---

Sync the flow setup from this plugin into the live ~/.claude directory.
The plugin payload is the source of truth: install everything, overriding
existing local copies.

1. Install with override: copy "${CLAUDE_PLUGIN_ROOT}/CLAUDE.md" to
   ~/.claude/CLAUDE.md, "${CLAUDE_PLUGIN_ROOT}/rules/"*.md into
   ~/.claude/rules/, and each skill directory under
   "${CLAUDE_PLUGIN_ROOT}/bundled-skills/" into ~/.claude/skills/ (create
   directories if missing). Only .md files from rules/. Copy only the
   paths this step names.
2. Report per file: overwritten (changed), already current, or new.
3. Check that every skill the Flow graph in CLAUDE.md names is installed,
   in ~/.claude/skills/ or as a plugin skill — advisory, not a gate: the
   files above are already installed either way. If any are missing, tell
   the user to run /setup-matt-pocock-skills for the baseline and list
   what else is missing.
4. Install or update the neurons layer: run
   bash "${CLAUDE_PLUGIN_ROOT}/scripts/install-neurons.sh" and report its
   output. It pulls the latest neurons release binary (platform-aware),
   installs it, and registers the MCP server if absent - idempotent.
   neurons installs from a private repo until its public release, so when
   gh is missing, not logged in, or cannot read that repo, the script
   prints "neurons skipped:" with the reason and exits 0. Pass that line
   on as it is and carry on: a skipped neurons does not fail the sync. Any
   other non-zero exit is a failed neurons install: report its output as
   an error and still carry on with the steps below.
5. Check the tool versions: run
   python3 "${CLAUDE_PLUGIN_ROOT}/scripts/sync-tools.py" "${CLAUDE_PLUGIN_ROOT}"
   and pass its report on as it is. It keeps ~/.claude/flow/tools.yml,
   which says which version of each tool flow installed runs: a newly
   installed tool gets an entry at flow's default version, and a tool flow
   no longer lists loses its entry. A listed tool that is not installed
   keeps its entry, and the report says it is not installed. No output
   means every tool already runs flow's default. For each line
   "<tool>: tools.yml runs <version>, flow's default is <default>", ask the
   user with the question tool whether to keep <version> or switch to
   <default>. Keep: nothing to do; sync asks again while the two differ.
   Switch: run the same command with `--switch <tool>` added and report its
   line. A non-zero exit is a failed tools step: report its output as an
   error and still carry on with the steps below.
6. After the files are installed and the neurons and tools steps have
   finished, tell the user:
   "Run /reload-plugins now to pick up the changes in this session
   (or restart Claude Code). New sessions auto-load on start."
7. Inspect ~/.claude/settings.json for a legacy manual UserPromptSubmit hook
   that cats a flow-reminder file. If present, warn: the plugin hook now
   injects the reminder, so every prompt is being double-injected. Recommend
   removing the manual hook, but do not edit settings.json without the
   user's explicit confirmation.
8. Keep commit and PR attribution off. ~/.claude/settings.json must contain
   `"attribution": {"commit": "", "pr": "", "sessionUrl": false}` and
   `"includeCoAuthoredBy": false` (the older key, still read by older
   Claude Code versions). If either is missing or different, apply it when
   permitted; Claude Code blocks Claude from editing its own settings in auto
   mode, so otherwise show the user the exact JSON to add. Report: attribution
   already off, or the change the user still needs to make.
