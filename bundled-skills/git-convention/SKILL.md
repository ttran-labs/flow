---
name: git-convention
description: Branch names, worktree folders, commit messages and PR bodies for ticket work. Use when ticket work starts, when creating a branch or a worktree, when writing a commit message, or when opening or editing a pull request.
---

# git-convention

Ticket work lives on its own branch, in its own worktree, next to the main
checkout.

## 1. Name the branch

`<intent>/<ticket id>/<description>`

- intent: the ticket's Intent line. The intents are examples, not a closed
  list: features, fixes, refactors, hotfixes.
- ticket id: written the way the tracker writes it.
- description: a few words of the ticket title, in kebab-case.

GitHub: `fixes/#<n>/superseded-node-address`
Jira: `features/PROJ-123/export-csv`

Done when: the name has all three parts and the id is spelled the way the
tracker spells it.

## 2. Make the worktree

Folder: `<project>:worktree/<project>-<ticket id>` next to the main checkout,
with `#` dropped from the folder name. On Windows use `<project>.worktree`,
because `:` is not allowed there.

From ~/Personal/flow, for ticket #<n>:

```
git worktree add ../flow:worktree/flow-<n> -b refactors/#<n>/pytest-suite
```

Done when: `git worktree list` shows the new folder on the new branch.

## 3. Commit

Conventional Commits, ticket at the end:

```
feat: refuse superseded nodes without an address (#<n>)
```

release-please reads the type to pick the release: `feat` = minor, `fix` =
patch, `!` after the type = major; `refactor`, `test`, `docs`, `chore` = no
release. Pick the type by the release the change deserves.

Done when: every commit message on the branch reads
`<type>: <summary> (<ticket id>)`.

## 4. Open the PR: the body, in this order

1. Typed link line: `Fixes`, `Refactors` or `Features #N`, then related
   tickets.
2. Why, in 1 to 3 lines.
3. Two diagrams: a high-level C4 view of where the change sits, and a
   low-level view of what changed.
4. Code-change view: the before/after or the table of cases that matters,
   not the diff.
5. Differences from the ticket, or "none".
6. Evidence: "QA pass" with the tests QA ran; for Gate tests, the suite
   result and each Done-when item checked. No gates table: CI shows test
   results.
7. Merge danger: what can break or needs doing after merge, or "none".
8. One `Closes #N` line per ticket the PR finishes. Refactors and Features
   are not GitHub closing keywords, so the typed link alone does not close
   the ticket.

Done when: all 8 parts are in the body, in this order, and every ticket the
PR finishes has its own `Closes` line.

## Example

The neurons #<pr> rewrite, shortened:

~~~
Fixes #<n>. Related: #<m>

A snapshot could load a Superseded node with no forwarding address, and
`supersede` then accepted it as a replacement. Every load path now refuses
a node whose status and address disagree.

graph LR
  S[from_snapshot, import, seed] --> L[long_term *changed*] --> G[(graph)]

graph TD
  C[check_consistency] --> W[forwarding-walk checks] --> A[status and address agree *new*]

                 superseded_by: None    superseded_by: Some(x)
Active           loads                  refused (new)
Parked           loads                  refused (new)
Superseded       refused (new)          loads

Differs from the ticket: adds the Parked-with-address case found in review.

QA pass: from_snapshot and import refuse it with nothing written; a row
broken through consolidate is refused by CLI and MCP while other graphs keep
working. Tests: 4 new, red on main; 298 passed.

Merge danger: a stored graph with the bad shape stops loading; the refusal
names the field to repair.

Closes #<n>
~~~
