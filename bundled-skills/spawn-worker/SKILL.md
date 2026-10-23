---
name: spawn-worker
description: Hand ticket work to a worker, a headless claude -p session in tmux. Use when spawning a worker for a ticket, writing a worker's brief, running a Gate full review or QA session, resuming a worker stopped by a usage limit, or cleaning up after a worker's PR.
---

# spawn-worker

A worker is a separate `claude -p` session in tmux that does one ticket and
reports back. The spawner writes its brief, starts it, waits, and relays the
report to the owner.

## 1. Start from a ticket

A worker only starts on an open, unblocked ticket.

Done when: `gh issue view <n> -R <owner>/<repo>` shows the ticket OPEN and
every ticket on its Blocked by line is closed.

## 2. Write the brief

Write the brief to a file in the scratchpad. It carries:

- the ticket reference;
- only the decisions the ticket does not spell out;
- the ticket's gate;
- the worktree and branch, named by the git-convention skill;
- "neurons tools may be locked by the main session: skip them and say so";
- the stop-and-report conditions: a step would delete files outside the
  worktree, or needs a decision the ticket and the brief do not cover;
- the report file path;
- "SendMessage a one-line summary to <spawner session name>". The spawner
  finds its own name with ListAgents.

Done when: the brief file holds every item in the list above.

## 3. Start the worker

```
tmux new-session -d -s <name> -c <dir> "claude -p \"\$(cat <brief>)\" > <log> 2>&1; echo EXIT=\$? >> <log>"
```

`<dir>` is where the worker starts, usually the main checkout. Then wait in
the background until the log has an EXIT line, for example a background
Bash `until grep -q '^EXIT=' <log>; do sleep 30; done`.

The command relies on `"defaultMode": "auto"` in Claude Code settings; without
it, add `--permission-mode auto` after `-p`. ListAgents shows the worker as
`<cwd folder>-<short suffix>`, not as its tmux name.

Done when: `tmux ls` lists `<name>` and the background wait is running.

## 4. Gate full and Gate qa: review and QA turns

On a Gate full ticket the worker runs a short review and a QA; on a Gate
qa ticket, only the QA. Each is its own `claude -p` session started and
waited on as in step 3. Both look
only at what the ticket changed. The review gets a bounded prompt that
names the diff to read and asks for a report of 30 lines or fewer. The QA
session uses the qa skill, whose own report shape sets its length.

When either finds a blocking problem, the worker fixes it and runs the
gate's sessions again on that fix only, each prompt naming what the fix
touches: one turn. After the third turn with a
blocking problem still open, the worker stops, opens no PR, and reports
the open problems to its spawner, who asks the owner.

Done when: the gate's reports on the last turn show no
blocking problem within 3 turns, or the worker has stopped and reported
after the third.

## 5. Resume after a usage-limit stop

When the log shows the worker stopped on a usage limit, resume it instead of
starting fresh. Its session file is `~/.claude/projects/<folder>/<id>.jsonl`,
where `<folder>` is the resolved path of `<dir>` with every non-alphanumeric
character as `-` (macOS `/tmp` resolves to `/private/tmp`).
`grep -l "<report path>" ~/.claude/projects/<folder>/*.jsonl` finds it, and
`<id>` is the file name without `.jsonl`. When several files match, take the
one whose first user message is the brief. Then, from that same directory:

```
tmux new-session -d -s <name> -c <dir> "claude -p --resume <id> \"<continue note>\" > <log2> 2>&1; echo EXIT=\$? >> <log2>"
```

and wait on `<log2>` as in step 3.

Done when: `<log2>` ends with `EXIT=0` and the work the continue note asked
for is done.

## 6. Read the report and clean up

Read the report and relay it to the owner. Remove the worktree and branch
only after the PR is merged.

```
[ "$(gh pr view <pr> -R <owner>/<repo> --json state -q .state)" = MERGED ] &&
  git worktree remove <worktree> && git branch -d <branch>
```

After a squash merge `git branch -d` refuses; the MERGED check already shows
the work landed, so use `git branch -D <branch>` then.

Done when: the owner has the report's summary, and the worktree and branch
are gone only if the PR state is MERGED.

## Lessons

- A worker cannot edit Claude Code settings in auto mode. Settings changes
  stay with the main session or the owner.
- The brief must carry the decisions, because the worker may not reach the
  thinking graph.
- "Refactors #N" does not close a ticket, so the worker's PR ends with
  "Closes #N" for every ticket it finishes (git-convention skill).
