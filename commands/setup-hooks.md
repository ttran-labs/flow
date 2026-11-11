---
description: Give this repo flow's checks for the languages it builds, through lefthook
---

Set up flow's checks in the repo the user is working in.

1. Run `mise run flow:setup-hooks` from inside that repo. It finds the
   repo's languages from the build files at its root (Java, Python, Rust,
   TypeScript) and writes `.config/lefthook-local.yml`, which extends flow's
   job file for only those languages. It adds that file and the hidden
   `.config/flow/` folder to git's global ignore file, so nothing it writes
   shows in `git status`, writes no mise config into the repo, and runs
   lefthook install. The repo's own lefthook config stays as it is. When
   the repo already has a lefthook-local config of its own, it changes
   nothing in it and prints the lines to add, in that file's format.
2. Pass its output on as it is. Do not edit an existing lefthook config
   yourself: show the user the printed lines and let them add them.
3. When it exits non-zero, report the line above mise's
   `[flow:setup-hooks] ERROR task failed` as the error: it names what is
   missing and how to fix it.
4. Tell the user that every worktree needs this too: the local config is
   ignored, so `git worktree add` leaves it out, and a commit in a worktree
   without it runs no checks. The git-convention skill runs it in every new
   ticket worktree. Running it again in a repo gives the same file and
   installs any hooks a newer flow added.
