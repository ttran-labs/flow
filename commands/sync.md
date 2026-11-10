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
   output. It curls the public ttran-labs/neurons release for this
   platform (aarch64-apple-darwin or x86_64-unknown-linux-gnu), checks the
   tarball's sha256 against the release's tools.lock, installs neuron and
   neuron-mcp, and registers the MCP server if absent - idempotent: at the
   released version it downloads no tarball. A non-zero exit is a failed
   neurons install (an unsupported platform, a sha256 mismatch, no
   network, no claude on PATH): report its output as an error and still
   carry on with the steps below.
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
9. Keep Claude Code on the pinned version, 2.1.287. ~/.claude/settings.json
   must contain `"env": {"DISABLE_AUTOUPDATER": "1"}`, so background updates
   don't move Claude Code off the pin. If it is missing or different, apply
   it when permitted: edit the file in place, add the key to the `env`
   already there, keep every other key, and leave no copy of the file
   behind. Otherwise show the user `"env": {"DISABLE_AUTOUPDATER": "1"}` and
   say to add the key inside an `env` that already exists, never replace it;
   never print the values already in `env`. Then run `claude --version`.
   When it reports another version, tell the user to run
   `curl -fsSL https://claude.ai/install.sh | bash -s <pinned version>`, with
   the pinned version filled in, and then restart Claude Code. Never run the
   install yourself. When the user has both to do, the setting comes first:
   restarting without it lets the startup update check move Claude Code past
   the pin. Report: Claude Code pinned, or what the user still needs to do,
   in that order.
