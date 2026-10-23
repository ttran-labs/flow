# Tickets and PRs — where in the system, and why

Skills: ticket (ticket shape), git-convention (PR shape).

- Every ticket takes the ticket skill's shape, which overrides the
  to-tickets template.
- One Where line, one ticket. A ticket changes the job of one component. A
  second component whose behaviour changes is a second ticket; wiring edits
  to neighbours (an import, a registration, a call site) are fine.
- Every PR takes the git-convention skill's shape.
