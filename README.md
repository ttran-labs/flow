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
  inside a major. It also holds flow's tasks and the lefthook job file for
  each language.
- `hooks/` and `scripts/`: one hook adds `flow-reminder.md` to every prompt.
  The others check each `git commit` Claude runs: no AI attribution in the
  message, no ticket work on `main`, and a thinking graph written in the
  last two hours.
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
mise's `[bootstrap] dotfile_groups`, list `flow-rules` and `flow-skills`
there too, or the next sync removes flow's rules and skills from `~/.claude`.

mise also installs the tools flow uses: lefthook, gitleaks, and neurons, the
MCP server behind the thinking graph the rules ask for, from its public
release. Releases ship for Apple Silicon macOS and x86_64 Linux. Each tool
names a major in flow's folder; the exact versions are in mise's global
lock, which `mise run flow:sync` writes and every sync moves to the newest
release inside that major. The lint tools load only in flow's own hook tasks, so your
shell and your builds keep their own versions.

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
