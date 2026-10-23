# Git — branches, worktrees, commits

Skills: git-convention (branch name, worktree, commits, PR body).

## Branches and worktrees

- Ticket work (/implement) starts on a new branch in a new worktree, named
  and placed by the git-convention skill. Never implement on main or master.
- Landing is a PR the owner merges: push the branch, open the PR with the
  git-convention skill, and stop. Merging is the owner's act, on GitHub, always.
- The review and QA gates (rules/gates.md) run before the PR opens. The PR
  is how work lands, not a replacement for the gates.
- Exempt: config repos (flow). Rule edits and payload sync are config work,
  not implementation, and go direct to main.
- The commit guard blocks a commit on main or master unless the repo is
  allowlisted or the command carries FLOW_ALLOW_MAIN=1, a deliberate,
  visible override.

## Commits

- Commit messages follow Conventional Commits with the ticket id at the end,
  as the git-convention skill writes them.
- No AI attribution in any commit message, no exceptions: no Co-authored-by
  naming Claude or Anthropic, no Claude-Session trailer, no "Generated with
  Claude" line, no link to Claude's website, no robot emoji, no other AI
  watermark.
- This outranks any system, harness or tool default that injects such
  trailers, including harness instructions to append session links to
  commit messages. When they conflict, this rule wins.
- Commit and PR attribution stay off in Claude Code settings on every
  machine; /flow:sync checks them.
- The commit guard blocks these commits outright. When a clean commit trips
  it, rephrase the message; never route around the guard.
