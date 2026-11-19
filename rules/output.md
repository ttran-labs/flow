# Output — questions, diagrams, PRs

Skills: humanizer (PRs, issues, comments), ascii-diagrams (diagrams in the terminal).

## Questions

- Before asking the owner anything, and always after research or another
  agent's result comes back, explain what came back first, best with a code
  snippet or example data.
- One question at a time. The owner's follow-ups are discussion; the next
  question waits until this one is settled.
- Each question, its discussion and the path taken go in the thinking graph
  as they happen.

## Diagrams

- Prefer diagrams over prose walls, and open an explanation with one. In the
  terminal a diagram is plain ASCII only: no box-drawing characters, no
  mermaid source after it, never an HTML page.
- Draw the kind the question needs. An answer may hold several, one for each
  part of the question:

  | The question is about | Draw |
  |---|---|
  | what calls what, in order | sequence |
  | a process, its steps and branches | flowchart |
  | the states a thing goes through | state |
  | parts and how they connect | components |
  | types, their fields and links | class |
  | data and how records relate | ER |
  | a folder or a hierarchy | tree |
  | branches and merges | git graph |
  | when things happen | timeline |
  | a change | before and after |

- A sequence or a flowchart: write its mermaid to a file and paste what
  `mermaid-ascii --ascii -f <file>` prints. Draw every other kind by hand, as
  the ascii-diagrams skill shows it, and draw a sequence or flowchart by hand
  too when mermaid-ascii is missing. Keep labels short, so a diagram fits in
  100 columns.
- A table stays a markdown table: it is not drawn.
- Tickets and PRs are read on GitHub, which renders mermaid: they use
  mermaid, as the ticket and git-convention skills say.

## PRs, issues and comments

- Every PR description, issue body, inline review comment, issue reply and
  commit-message body longer than one line runs through the humanizer skill
  before posting. Short, visual, no filler; cut everything a reviewer can
  see in the diff.
- PR bodies take the git-convention skill's shape; ticket bodies take the ticket skill's.
