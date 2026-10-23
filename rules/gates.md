# Gates — what blocks work, a commit and a PR

Skills: ticket and to-tickets (tickets first), implement, tdd, diagnosing-bugs, code-review, qa.

Routing lives in the Flow graph in ~/.claude/CLAUDE.md. Thresholds, coverage
floors and taste rules live in the clean-code skill; if any file disagrees
with it, clean-code wins.

## Tickets before work

- No implementation work starts until /to-tickets has created its tickets,
  however small the task.
- Tickets come out of the chain before them: grill-me for clarity, then the
  design skills (codebase-design, domain-modeling, the language
  best-practice skill), then to-spec. Never a shortcut around it.
- /implement fires only on an existing ticket, and the ticket's gate sets
  the checks before its PR.

## The gate — the checks before the PR

Every ticket names its gate when it is cut: full, qa or tests.

- full: tests green, then /code-review and /qa, each in its own session. For
  changes that run as code: guards, test suites, CI and release workflows,
  anything that moves the owner's files outside the repo.
- qa: tests green, then /qa tests the change black-box on a throwaway copy.
  For skill text that drives behaviour.
- tests: tests green and every "Done when" item checked by the worker. For
  rule text and mechanical changes (renames, manifests, word sweeps). The
  owner reads every PR before merging.

Review and QA look only at what the ticket changed. When they find a
blocking problem, the worker fixes it, and review and QA check only that
fix, not everything again: that is one turn. At most 3 turns; if a blocking
problem is still open after the third, the worker stops and asks the
owner. Anything noticed outside the change gets one line in the report,
no ticket.

When in doubt, choose full. One-way doors (anything published, anything that
deletes files git cannot restore) are always full. No self-certification on
full or qa: the implementer's own green suite never counts as the review or
the QA.

## Tests

- Never commit or push with a failing suite. Tests decide whether an
  implementation succeeded; a red suite is a stop, not a warning.
- Code without tests is never committed or pushed. New or changed logic
  ships with its tests in the same commit.
- Coverage never drops below 80%: an absolute stop, no commit crosses it.
  The working floors stay clean-code's (>=90% overall, >=95% critical
  paths); 80% is the line never crossed, not the target.
- A new test or validator counts only after it has been seen to fail on a
  broken copy of what it guards, and that broken-copy check ships with it. A
  surprising result, even a quiet one, gets a confirmed cause before the
  step is called done.
- A test that pins a bug asserts the fixed behaviour, and its fixtures do
  not depend on the bug they document.
- A failing test is never made to pass by editing the test. Debug first
  (diagnosing-bugs) to prove the failure is not a logic bug. A logic bug
  gets a defect ticket via /to-tickets and a code fix under it; the test's
  inputs and assertions stay. Only a test proven wrong by diagnosis may
  change, with the proof stated.
- Test representations before data. Data entries are cheap to fix late;
  representations and public shapes (identifiers, encodings, interface
  types downstream code bakes in) are not. Adversarial,
  tested-before-implementation scrutiny goes to what structures mean first,
  data contents second.
- No hook checks the suite, coverage or the gate: the agent runs them itself
  before every commit and PR, and the owner reads every PR.
