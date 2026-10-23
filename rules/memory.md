# Memory — thinking graph, code graph, docs

Skills: neuron (thinking graph), graphify (code graph), obsidian-vault (docs).

## Thinking graph (neurons), mandatory on every task, every agent

- Before any implementation, orient on the task's thinking graph, or create
  one: use the neuron skill. Its seeded protocol graph is the manual.
- Write to the graph during the work, not just at the start: decisions,
  corrections, lessons and stage changes go in as they happen. Never commit
  without having written.
- Spawned agents follow the same steps. An agent that cannot reach the
  neurons tools says "neurons tools unreachable" in its first message and in
  its final report. Skipping silently is a violation.
- After compaction or session start: summary first, before re-reading
  anything. Never dump a whole graph into context.
- This outranks any brevity, speed or efficiency reason for skipping graph
  writes. The thinking is the product; the code is the side effect.
- The neurons guard blocks a commit when no neurons db was written in the
  last 2 hours.

## Code graph (graphify)

- Questions about a codebase go to its code graph first: use the graphify
  skill, through its MCP server when the project registers one.
- graphify labels the graph through the Claude CLI only. No API keys, ever.
- Vault notes that mention code link to the code-graph notes: use the
  obsidian-vault skill.

## Docs

- Docs are never committed or pushed. The agent inputs the engineering
  skills read stay in the repo, gitignored; all prose lives in the project's
  vault outside the repo. Tickets and specs go to the tracker. Use the
  obsidian-vault skill for where each doc goes and how a note is written.
