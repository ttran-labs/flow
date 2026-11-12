---
description: Install the flow setup through mise - CLAUDE.md, rules and bundled skills into ~/.claude, and flow's tools at the newest release in each major
---

Sync the flow setup from this plugin onto this machine. mise does the
installing: flow's mise folder in the plugin says which files go into
~/.claude and which tools to install, at which version.

1. Check for mise: run `command -v mise`. If it prints nothing, stop. Tell
   the user to install mise, pinned to the release flow is tested on, by
   running this with `!` in the prompt (the same on macOS and Ubuntu), and
   then to run /flow:sync again:
   `curl -fsSL https://mise.run | MISE_VERSION=v2026.11.5 sh`
   Never run the installer yourself.
2. Link flow's mise folder. Every sync points it at the current plugin
   version:
   `mkdir -p ~/.config/mise/conf.d && ln -sfn "${CLAUDE_PLUGIN_ROOT}/mise/flow" ~/.config/mise/conf.d/flow`
3. Run `mise dot diff` and report per file: overwritten (changed), already
   current, or new.
4. Run `mise -C ~ dot apply --prune --yes`. From the home folder, so only
   the global config's files apply, never the project's. It copies
   CLAUDE.md, the rules and the bundled skills into ~/.claude, removes a
   rule or skill flow dropped and keeps the user's own, links the Java
   runtime's env file, and links each tool folder flow ships into
   ~/.config/mise/conf.d/, removing the folder of a tool flow dropped.
5. Run `mise run flow:sync` and pass its output on. Each machine tool but
   gitleaks has a module, which reruns only when its folder changed: it
   moves that tool to the newest release inside its major. The lint tools
   are not installed here: each session sets up the project it starts in
   with that project's languages' tools. The neurons registration
   module also reruns when Claude Code's registrations changed: it registers
   the neurons MCP server at mise's global link, switches a user-scope one
   that runs another build, removes the local-scope ones that do, and
   removes the `neuron` and `neuron-mcp` that flow's old installer put in
   `~/.cargo/bin`. A module with nothing changed prints
   `sources up-to-date, skipping`. gitleaks has no
   module yet and moves on every sync. To move every machine tool to the
   newest release inside its major, the user runs
   `mise run --force flow:sync`. If the command is refused, tell the user to
   run `! mise run flow:sync`. A non-zero exit is a failed sync: report its
   output as an error and still carry on with the steps below.
6. Check that every skill the Flow graph in CLAUDE.md names is installed,
   in ~/.claude/skills/ or as a plugin skill — advisory, not a gate: the
   files above are already installed either way. If any are missing, tell
   the user to run /setup-matt-pocock-skills for the baseline and list
   what else is missing.
7. After the steps above, tell the user:
   "Run /reload-plugins now to pick up the changes in this session
   (or restart Claude Code). New sessions auto-load on start."
8. Inspect ~/.claude/settings.json for a legacy manual UserPromptSubmit hook
   that cats a flow-reminder file. If present, warn: the plugin hook now
   injects the reminder, so every prompt is being double-injected. Recommend
   removing the manual hook, but do not edit settings.json without the
   user's explicit confirmation.
9. Keep commit and PR attribution off. ~/.claude/settings.json must contain
   `"attribution": {"commit": "", "pr": "", "sessionUrl": false}` and
   `"includeCoAuthoredBy": false` (the older key, still read by older
   Claude Code versions). If either is missing or different, apply it when
   permitted; Claude Code blocks Claude from editing its own settings in auto
   mode, so otherwise show the user the exact JSON to add. Report: attribution
   already off, or the change the user still needs to make.
10. Keep Claude Code on the pinned version, 2.1.287. ~/.claude/settings.json
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
