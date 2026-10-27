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
- `commands/sync.md`: the `/flow:sync` command.
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

Then run `/flow:sync` once. It copies `CLAUDE.md`, `rules/` and the bundled
skills into `~/.claude`. Don't skip it: plugin hooks only reach the main
session, and subagents only see the rules through those copied files.

Sync checks the tools flow includes with Python, so the `python3` on your PATH
needs PyYAML: `python3 -m pip install pyyaml`.

Sync also tries to install neurons, the MCP server behind the thinking graph
the rules ask for. neurons stays private until its first public release, so
for now sync skips it and says why. Without neurons, the thinking graph
check stays off and commits go through.

To use graphify in a project, add it to the project's `.mcp.json` and build
the graph once with `/graphify`:

```json
{"mcpServers": {"graphify": {"command": "graphify-mcp", "args": ["graphify-out/graph.json"]}}}
```

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
