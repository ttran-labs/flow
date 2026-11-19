---
name: ascii-diagrams
description: One plain-ASCII example of each diagram kind the output rule names - sequence, flowchart, state, components, class, ER, tree, git graph, timeline, before and after. Use when drawing any diagram in the terminal.
---

# ascii-diagrams

Plain ASCII only: `- | + < > v ^ * / \ .` and letters. A sequence and a
flowchart come from `mermaid-ascii --ascii -f <file>`; the drawings of those
two below are what it prints, for when it is missing. Draw the rest by hand,
in the shape shown. Keep labels short, so a diagram fits in 100 columns.

## sequence

```
+-----+     +----------+     +----------+
| git |     | flow-cli |     | lefthook |
+--+--+     +-----+----+     +-----+----+
   | pre-commit   |                |
   +------------->|                |
   |              | run pre-commit |
   |              +--------------->|
   | exit code    |                |
   |<..............................+
```

Solid arrows are calls, dotted arrows are answers.

## flowchart

```
+-------------+     +------+
| setup-hooks |---->| Repo |
+------+------+     +------+
       |               ^
       v               |
+-------------+        |
|  Lefthook   |--------+
+-------------+
```

A choice is a `< question >` box with a labelled arrow for each answer.

## state

```
           setup writes            owner edits
[Missing] -------------> [Generated] -----------> [HandWritten]
    ^                                                  |
    +------------------- file deleted -----------------+
```

## components

```
+------------------- flow-cli ---------------------+
|  +-------------+      +------+      +---------+  |
|  | setup_hooks |----->| Repo |<-----| git_hook|  |
|  +-------------+      +--+---+      +---------+  |
+-------------------------|-------------------------+
                          v
                       [ git ]
```

## class

```
+---------------------+        +----------------------+
| Repo                |  1   * | SubModule            |
|---------------------|------->|----------------------|
| root: PathBuf       |        | path_from_root       |
| shared_folder       |        | languages: [Language]|
|---------------------|        +----------------------+
| find(), lock_setup()|
+---------------------+
```

## ER

```
+-----------+        +-------------+        +----------+
| customer  |1      *| order       |*      *| product  |
|-----------|--------|-------------|--------|----------|
| id  PK    |        | id  PK      |        | id  PK   |
| email     |        | customer FK |        | name     |
+-----------+        +-------------+        +----------+
```

## tree

```
shop/
+-- src/
|   +-- main.rs
|   +-- order.rs
|   +-- billing/
|       +-- invoice.rs
+-- Cargo.toml
```

## git graph

```
* 903d61b  Merge the feature branch
|\
| * ed1b217  Merge main into it
| * 382638e  refactor: the git hook
|/
* 017abe2  release 0.17.0
```

## timeline

```
2026-11-17          2026-11-19            2026-11-20
----+-------------------+---------------------+------->
    |                   |                     |
  port split        three stacks          ASCII rule
  into a stack      become single PRs
```

## before and after

```
 before                          | after
 --------------------------------+--------------------------------
 rc holds sh logic as strings    | rc holds 3 export lines
 lefthook runs first             | flow-cli runs, then lefthook
```
