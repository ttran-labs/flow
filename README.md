# flow

My Claude Code setup: the working flow in `CLAUDE.md`, the rules in `rules/`,
and the skills they use. It leaves out settings, permissions and anything
tied to one machine.

## What's here

- `CLAUDE.md`: the Flow graph. Every task goes from questions to design,
  tickets, build, review and QA.
- `rules/`: short rules loaded into every session. Each one names the skill
  that explains how.
- `bundled-skills/`: skills written for this setup that no plugin provides.
  `/flow:sync` copies them into `~/.claude/skills/`.
- `commands/`: the `/flow:sync` and `/flow:setup-hooks` commands.
- `mise/flow/`: flow's mise setup. It says which files `/flow:sync` copies
  into `~/.claude` and which tools flow installs, each at the newest release
  inside a major. It also holds flow's tasks, the commit guards among them,
  and the lefthook job file for each language.
- Next to `mise/flow/`, a folder for each machine tool but gitleaks, with
  its tool line and its `flow:sync:<tool>` module. `/flow:sync` links each
  folder into `~/.config/mise/conf.d/`, where mise loads it as its own
  config. gitleaks' tool line sits in `mise/flow/` itself.
- `hooks/`: one hook adds `flow-reminder.md` to every prompt. The others
  start the guards, `mise -C ~ run flow:guard-commit` and
  `flow:guard-neurons`, on each Bash command Claude runs. They check each
  `git commit`: no AI attribution in the message, no ticket work on `main`,
  and a thinking graph written in the last two hours. They start from the
  home folder, so mise never trusts the project Claude works in. Without
  mise or flow's tasks they block nothing and print one line: run
  `/flow:sync`.
- `flow-reminder.md`: the short reminder that hook adds.

## Install

flow needs plugins from three other marketplaces, so add those first. In
Claude Code:

```
/plugin marketplace add anthropics/claude-plugins-official
/plugin marketplace add blader/humanizer
/plugin marketplace add DietrichGebert/ponytail
/plugin marketplace add ttran-labs/flow
/plugin install flow@flow
```

This also installs mattpocock-skills, context7, humanizer and ponytail.

Two skills don't come with any plugin. Install them yourself:

```sh
npx ctx7 setup --claude --cli        # find-docs
uv tool install graphifyy            # graphify; the package name has two y's
graphify install --platform claude
```

Sync needs [mise](https://mise.jdx.dev). Without it, `/flow:sync` stops and
prints the install command, pinned to the mise release flow is tested on:

```sh
curl -fsSL https://mise.run | MISE_VERSION=v2026.11.5 sh
```

Then run `/flow:sync` once. It links flow's mise folder as
`~/.config/mise/conf.d/flow`, and mise copies `CLAUDE.md`, `rules/` and the
bundled skills into `~/.claude`, removing a rule or skill flow dropped and
keeping your own. Don't skip it: plugin hooks only reach the main session,
and subagents only see the rules through those copied files. Your own
`~/.config/mise/config.toml` is never edited. If you pick dotfile groups with
mise's `[bootstrap] dotfile_groups`, list `flow-rules`, `flow-skills` and
`flow-tools` there too, or the next sync removes flow's rules and skills from
`~/.claude` and its tool folders from `~/.config/mise/conf.d`.

mise also installs the tools flow uses: lefthook, gitleaks, and neurons, the
MCP server behind the thinking graph the rules ask for, from its public
release. Releases ship for Apple Silicon macOS and x86_64 Linux. Each tool
names a major in flow's mise folders; the exact versions are in mise's global
lock, which `mise run flow:sync` writes. Each tool but gitleaks has a module
that reruns only when its own files changed, and then moves that tool to the
newest release inside its major; a sync with nothing changed skips them all.
gitleaks has no module yet and moves on every sync. `mise run --force
flow:sync` moves every tool now. Every session runs the neurons mise
installed: sync registers its MCP server at mise's global link, switches a
user-scope registration that runs another build, removes the local-scope ones
that do, and deletes the `neuron` and `neuron-mcp` that flow's old installer
put in `~/.cargo/bin`. The lint tools load only
in flow's own hook tasks, so your shell and your builds keep their own
versions.

Sync keeps every machine on the Claude Code version flow pins. When yours
differs, it prints the install command for that version; it never runs the
installer itself. It also turns off Claude Code's background updates, or shows
the setting to add.

To use graphify in a project, add it to the project's `.mcp.json` and build
the graph once with `/graphify`:

```json
{"mcpServers": {"graphify": {"command": "graphify-mcp", "args": ["graphify-out/graph.json"]}}}
```

## Checks in a repo

Run `/flow:setup-hooks` once at the root of each repo, after `/flow:sync`.
It finds the repo's languages from its build files (Java, Python, Rust,
TypeScript), writes `.config/lefthook-local.yml` extending flow's job files
for only those languages, and installs the git hooks. Every commit then
runs those checks, also from a GUI git client like IntelliJ. It adds the
files it writes to git's global ignore file, so `git status` stays clean.

A new worktree needs it too: the local config is ignored, so `git worktree
add` leaves it out, and a commit there runs no checks until you run
`mise run flow:setup-hooks` in the worktree. The git-convention skill does
that for every ticket worktree.

## Update

```sh
claude plugin marketplace update flow
claude plugin update flow@flow
```

Then restart Claude Code and run `/flow:sync`.

## Without the plugin

Clone this repo and copy the files by hand:

```sh
mkdir -p ~/.claude/rules
cp CLAUDE.md flow-reminder.md ~/.claude/
cp rules/*.md ~/.claude/rules/
```

You then wire the reminder yourself: in `~/.claude/settings.json`, add a
`UserPromptSubmit` hook that runs
`cat ~/.claude/flow-reminder.md 2>/dev/null || true`.

## License

MIT, see `LICENSE`.
