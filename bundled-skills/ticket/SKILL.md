---
name: ticket
description: Ticket body shape - Intent, Gate, Where, Serves, Blocked by, diagram, Done when. Use when cutting tickets, filing a defect ticket, or writing or editing a ticket body; works with mattpocock's to-tickets, which slices and publishes, while this skill sets each ticket's body.
---

# ticket

## 1. Cut the set with to-tickets

For a spec or plan, run mattpocock's to-tickets skill: it slices the work
into tracer-bullet tickets, sets their blocking edges, has the owner
approve the breakdown, and publishes them. When it reaches its own issue
template, write each body with step 2 instead. A single defect ticket
skips this step.

Done when: the owner approved the breakdown and every ticket names the
tickets that block it.

## 2. Write the body in this shape

```
Intent:     <features | fixes | refactors | hotfixes | ...>
Gate:       <full | qa | tests>
Where:      <container> > <component> [new | changed]
Serves:     <the spec it serves, and one line of what the owner gets>
Blocked by: <ticket ids, or none>
Does:       <optional: what the ticket adds beyond its title>

<one mermaid diagram, the changed parts marked *new* or *changed*>

Done when:
- [ ] <checkable item>
- [ ] tests pass
```

- Intent: read by the git-convention skill. The intents are examples, not a
  closed list.
- Gate: the checks before the PR, as ~/.claude/rules/gates.md defines them.
- Where: one line, as ~/.claude/rules/tickets-prs.md scopes a ticket.

Done when: every field is filled, the diagram marks what is new or changed,
and every Done-when item can be checked true or false.

## Example

The gate-check ticket:

~~~
Intent:     features
Gate:       full
Where:      neurons > cli > gate-check [new]
Serves:     #<n> a commit hook asks the graph whether the task was thought through
Blocked by: none

graph LR
  H[pre-commit hook] --> C[neuron gate-check *new*]
  C --> D[(project neurons.db)]

Done when:
- [ ] `neuron gate-check` exits 0 when the current task has a graph write this session, 1 otherwise, naming the task
- [ ] a fixture with no write makes it exit 1
- [ ] tests pass
~~~
