# Agent protocol — pick once per project, workers do the ticket work

Skills: spawn-worker (brief, start, gate sessions, resume, clean-up).

## Pick once per project

No agent is spawned in a project until the user has picked how agents
coordinate, from the mechanisms this environment actually has (agent teams,
workflows, message servers, tmux fleets). The choice is recorded in the
project CLAUDE.md or memory and reused; it is asked again only when the
toolset changes. This holds for user-to-agent and agent-to-agent work alike.

## Main session and workers

- The main session stays a discussion: lookups, thinking graph and memory
  writes, and small direct work the owner asks for. It makes a change
  itself only when the change is already known and takes about 10 tool
  calls or fewer. Anything that needs exploring goes to a worker.
- Ticket work (code, tests, commits, PRs) goes to workers. Workers follow
  the Flow and the rules like any session.
- A worker is a separate headless Claude Code session in tmux, never an
  in-process subagent: the owner can watch and stop it, its tokens are not
  re-sent on every call, and a reviewer gets its own session id. A review or
  QA worker is one bounded call with a fixed scope, never a resumed agent.
- A worker reports back to the session that spawned it.
