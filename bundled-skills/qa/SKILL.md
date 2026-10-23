---
name: qa
description: Black-box QA of a ticket's domain and business logic on a throwaway copy, filing every bug in the change as a qa-found ticket. Use when a ticket's gate calls for QA, when asked to QA a PR, branch or change, or when re-checking the fix for a blocking qa-found ticket.
---

# qa

QA proves the ticket's domain and business logic hold, from the outside, as
a real user or a client of the system would meet them. Test like a QA who
annoys developers: the change is broken until it shows otherwise.

QA is black-box. Source code, tests and the diff stay closed: knowing how
the change was built is the developer's bias QA exists to remove. Every
answer comes from what a user can see, or from running the system.

## 1. Read what a user can see

- the ticket, its Done-when list and its business rules;
- the spec the ticket serves;
- the domain glossary (`CONTEXT.md`, `UBIQUITOUS_LANGUAGE.md`) and decision
  records (`docs/adr/`), when the project has them;
- user-facing docs: README, `--help`, API docs, skill and command text;
- the PR title and body.

Done when: a list holds every Done-when item and every business rule to
test, each with the line it came from.

## 2. Make a throwaway copy

Run everything on a copy that can be wrecked: a scratch clone of the PR
branch, a second scratch clone of the base branch, a separate home
(`HOME=<scratch>/home`) or Claude config (`CLAUDE_CONFIG_DIR=<scratch>/claude`),
and test data. Any tool, framework or library is fair game.

Keep the scripts and the report in one folder outside the repo, such as
`<scratchpad>/qa-<ticket id>/`, or the path the brief names.

When a behaviour only shows on the owner's real setup, stop and report
`QA blocked: needs the owner's setup for <behaviour>`.

Done when: every command QA runs points at the copy, and the scripts folder
exists outside every clone.

## 3. Check every Done-when item

For each item and business rule, write a repro script in the scripts folder
that acts as the user would, run it on the PR copy, and record what it
showed.

Done when: every item from step 1 has a script, a result (pass or fail) and
the steps a reader can repeat.

## 4. Attack from all 8 angles

Every ticket gets all 8 angles, whatever its scope:

| Angle | Try |
|---|---|
| bad input | wrong types, empty, huge, unicode, injection-shaped text |
| edges | zero, one, the limit, one past the limit, the oldest supported setup |
| order and repetition | steps out of order, the same step twice, run, undo, run again |
| interruption | kill it halfway, then run it again |
| missing setup | absent file, tool, env var, permission or network |
| two at once | two runs, sessions or worktrees on the same data |
| docs vs reality | follow the docs word for word; every claim holds |
| error messages | each failure names what went wrong and what to do next |

Done when: each of the 8 angles has at least one script, a result and its
steps.

## 5. Sort every bug

For each failure, run its repro script on the base copy too:

- in-scope: fails on the PR copy and passes on the base copy;
- pre-existing: fails on both.

Then its severity:

- critical: data loss, a security hole, or the feature broken;
- major: a Done-when item or business rule fails;
- minor: anything else, such as wording or a rough edge with a workaround.

A bug blocks the PR when it is in-scope and critical or major. Minor
in-scope bugs never block. Pre-existing bugs are outside the change: they
get one line in the report and no ticket.

Done when: every bug has its scope, its severity and blocks or does not
block.

## 6. File every in-scope bug

File each in-scope bug as a defect ticket with the ticket skill, in the
tracker the brief names, or else the ticket's own tracker:

- Intent `fixes`, label `qa-found` (create the label if the tracker lacks
  it);
- the repro script inline in a fenced block, with its expected and actual
  output;
- a `Found in: <PR link>` line, plus `Blocks <PR link>` when it blocks.

Done when: every in-scope bug from step 5 has a ticket number, and listing
the tracker's `qa-found` tickets shows each one.

## 7. Report

```
QA fail

Done when
- pass: <item>. Steps: <steps>
- fail: <item>. Steps: <steps>. #<ticket>, blocks

Angles
- bad input, pass: <what was tried>. Steps: <steps>
- edges, fail: <what was tried>. Steps: <steps>. #<ticket>, does not block
...

Outside the change
- <one line per pre-existing bug, no ticket>

Tickets: #<n> <title> (blocks), #<m> <title> (does not block)
Scripts: <folder>
```

Write the report to `report.md` in the scripts folder and hand its path
back.

Done when: the first line is `QA fail` when any ticket blocks and `QA pass`
otherwise; every item from step 1 and all 8 angles appear with their steps;
every in-scope failure carries its ticket number and blocks or does not
block; every pre-existing bug has one line under Outside the change; the
scripts folder sits outside the repo.

## Re-check after a fix

When a fix lands on the branch, re-check only that fix, on a fresh copy of
the branch:

- a fix for a blocking QA ticket: re-run that ticket's repro script;
- a fix for a review finding: re-run the Done-when items and angles the
  worker's prompt names as touched by the fix.

Each re-check is one of at most 3 turns.

Done when: the report's first line is `QA pass` with each re-run check shown
passing, or `QA fail` naming the check that still fails.
