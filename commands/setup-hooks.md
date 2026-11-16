---
description: Give this repo flow's checks for the languages it builds, through lefthook
---

Set up flow's checks in the repo the user is working in. The user may name
project folders after the command: $ARGUMENTS

1. Run `mise run flow:setup-hooks $ARGUMENTS` from the folder this session
   is in, inside that repo. A repo's project folders are its root, the
   folder the task starts in and each folder named after it, read from that
   folder; a later run remembers them. It finds each project folder's
   languages from the build files in it (Java, Python, Rust, TypeScript) and
   writes `.config/lefthook-local.yml` at the repo root, which extends flow's
   job file for only those languages. A project folder below the root gets
   jobs that run inside it, for the staged files under it, so a repo with no
   build file at its root still gets its projects' checks. It adds the local
   config and the hidden `.config/flow/` folders to git's global ignore
   file, so nothing it writes shows in `git status`, writes no mise config
   into the repo, and runs lefthook install. The repo's own lefthook config
   stays as it is. When the repo already has a lefthook-local config of its
   own, it changes nothing in it and prints the lines to add, in that file's
   format.
2. Pass its output on as it is. Do not edit an existing lefthook config
   yourself: show the user the printed lines and let them add them.
3. When it exits non-zero, report the line above mise's
   `[flow:setup-hooks] ERROR task failed` as the error: it names what is
   missing and how to fix it.
4. Tell the user that a new worktree needs nothing typed: the local config is
   ignored, so `git worktree add` leaves it out, and the worktree's first
   commit copies it in from the repo's `.git/flow/` folder, where the task
   keeps a copy of it and the rc the hooks load first. Running the task
   again in a repo gives the same file and installs any hooks a newer flow
   added.
