# Output — messages, diagrams, PRs, explainers

Skills: humanizer (PRs, issues, comments), explainer and teach (explainers).

## Messages

- Say where things stand first; end on the reader's next decision.
- Numbers over adjectives: "coverage 88.3% -> 94.1%", not "much better".
- Every question opens with its path on its own line: [ goal ] > [ area ] >
  [ scope ], with a space inside each bracket. Then the question, then the
  answers to choose from, recommended one first. A decision with 2 to 4
  options goes through Claude Code's question tool in the same order; an
  open question stays as text. When more than 3 questions are open, show a
  map of them first.
- Plain, concrete words, in prose and in code strings (flags, errors, docs).
  No grand abstract vocabulary for ordinary things; use the project's own
  words.
- Pushback that comes with a confused comment ("still not clear") means the
  proposal was not understood, not "no". Keep the proposal and explain it
  again with new before/after examples on different data. Only a bare no is
  final; never file tickets or change plans on an unclear reject.

## Diagrams

- Prefer graphs and diagrams over prose walls. A flowchart or an ASCII
  before/after says more than three paragraphs.
- In the terminal, draw a diagram as ASCII first (mermaid-ascii) and put the
  mermaid source after it.

## PRs, issues and comments

- Every PR description, issue body, inline review comment, issue reply and
  commit-message body longer than one line runs through the humanizer skill
  before posting. Short, visual, no filler; cut everything a reviewer can
  see in the diff.
- PR bodies take the git-convention skill's shape; ticket bodies take the ticket skill's.

## Explainers

- Explainers teach with diagrams and interactive examples, never a flat page
  of text: use the explainer skill, also when the teach skill fires.
