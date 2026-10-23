---
name: neuron
description: The thinking graph. Use on every task, every agent, every session. This is only the bootstrap - load the tools, read the seeded neuron-protocol graph, and follow it. The protocol graph is the manual; a session without graph writes is incomplete. After compaction or session start, summary first.
---

# neuron

The manual is not in this file. It is the seeded `neuron-protocol`
graph in the engine, and it evolves there. This file only gets you
to it.

## 1. Load the tools

```
ToolSearch "select:mcp__neurons__summary,mcp__neurons__add_node,
mcp__neurons__add_nodes,mcp__neurons__new_graph,mcp__neurons__link,
mcp__neurons__set_stage,mcp__neurons__supersede,mcp__neurons__reinforce,
mcp__neurons__search,mcp__neurons__settle,mcp__neurons__park,
mcp__neurons__unpark,mcp__neurons__consolidate,mcp__neurons__export,
mcp__neurons__position,mcp__neurons__next_moves,mcp__neurons__advance,
mcp__neurons__bridge,mcp__neurons__propose,mcp__neurons__radius,
mcp__neurons__show,mcp__neurons__path,mcp__neurons__list"
```

If unavailable: state "neurons tools unreachable" in the first message
and in the final report.

## 2. Read the protocol

```
export graph="neuron-protocol" format="md"
```

It is nine nodes. Each node's content is the exact calls for that
moment: session-start branches to establish or activate, route asks
the flow what is next, work runs the position's runbook top to bottom,
capture writes each decision as it lands, recall re-orients after
compaction (summary FIRST, never dump a graph), close settles.

## 3. Obey it

FIRST ACT, before any work on the task: stand it in the flow.

```
add_node graph=<task> id="_flow_state" kind="pointer" title="where this task stands"
bridge from=<task>:_flow_state to=flow:<node> label="at"
```

A graph without a pointer routes nothing - position, next_moves, and
the runbooks all start from that bridge. Then let `position` hand you
the ordered runbook - explicit invocations, run top to bottom.
`advance` only when the condition honestly holds, with the why.

LAST ACT, when the task is done: `settle` the graph. Work that ends
with the root still active is unfinished by definition.

After compaction: `summary` first, always, before reading anything
else.
