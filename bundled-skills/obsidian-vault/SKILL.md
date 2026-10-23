---
name: obsidian-vault
description: Where docs live and how notes are written in the project's Obsidian vault. Use when saving research, explainers, design writeups or reports, when finding or linking vault notes, or when deciding where a doc goes.
---

# obsidian-vault

Two homes for docs, never the remote:

- In the repo, local only: `CONTEXT.md`, `docs/adr/`, `docs/agents/`, the
  inputs the engineering skills read on every run. They sit at those paths
  and are gitignored.
- In the vault: all prose (research, explainers, design writeups, reports)
  at `~/obsidian/Vaults/<project>/`, one vault per project, outside the
  repo. A vault folder inside the repo is a stale stub. A skill's own report
  keeps its format but lands here.

## 1. Find the vault

Confirm the project's vault from its CLAUDE.md or memory, then look at its
layout before writing:

```
ls ~/obsidian/Vaults/<project>/
```

Read the vault's `CONVENTIONS.md` or `README.md` when present.

Done when: the vault path exists and its folders are known.

## 2. Write the note

- Put it in the folder for its kind: `research/`, `diagrams/`, `adr/`,
  `plans/`, `notes/`, `runbooks/`, `sessions/`, `reference/`.
- Title Case file name.
- Links to related notes go at the bottom as `[[Note Title]]`.
- A code symbol with a generated code-graph note is linked by name:
  `[[SymbolName]]`, methods as `[[dot-MethodName()]]`. The vault's
  `CONVENTIONS.md` has the full naming table.
- An HTML file gets a one-line markdown stub that links it, so markdown
  search still finds it.

Done when: the note is in its kind's folder, links its related notes, and
any HTML file has its stub.

## 3. Link it from an index

Index notes (`* Index.md`) are lists of `[[wikilinks]]`. Add the new note to
the index for its topic.

Done when: the note appears in an index note.

## Developing notes

A note still being worked out sits in `notes/` with a `status:` frontmatter
field. Promote it by moving it to `research/` or `adr/` and flipping the
status.

## Code-graph notes

graphify generates the code-graph notes. The export into the vault is
manual; the project's `docs/agents/second-brain.md` names the command.

## Search

```
find ~/obsidian/Vaults/<project>/ -name "*.md" | grep -i "<word>"   # by name
grep -rl "<word>" ~/obsidian/Vaults/<project>/ --include="*.md"     # by content
grep -rl "\[\[<Note Title>\]\]" ~/obsidian/Vaults/<project>/        # backlinks
```
