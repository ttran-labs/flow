---
name: clean-code
description: The quality standard — thresholds, SOLID signals, code taste, test discipline. Use when reviewing or refactoring code, judging a diff or PR, deciding if code is complete, enforcing coverage gates, or when code-review, pr-review, improve-codebase-architecture, or to-spec need their standards.
---

# Clean Code

The single authority for code quality. Review skills (`code-review`, `pr-review`),
the refactor branch (`improve-codebase-architecture`, `to-spec`),
and the testing gate all judge against this file. Nothing elsewhere restates a
number from here.

## Thresholds

Hints, not limits. A review may name one as a minor note, never as critical or
major, and never asks for a split because of one. The coverage floor is the
exception: it still blocks. Never applied inside the red → green loop.

| Unit | Hint | What to look at |
|---|---|---|
| Function length | ≤40 lines | Separate steps or separate ideas? Keep it whole if it reads top to bottom |
| Function params | ≤3 | Do some params always travel together? |
| Cognitive complexity | ≤15 | Would inverted conditions or early returns read flatter? |
| Cyclomatic complexity | ≤10 | Is a chain of branches switching on a type code? |
| Nesting depth | ≤4 | Would guard clauses read flatter? |
| Class | ≤200 lines, ≤20 methods | Does it change for more than one reason? |
| File | ≤500 lines (target 200–300) | Does it hold more than one concept? |
| Package | <20 classes | Do its classes share one purpose? |
| Line | 80–120 chars | Wrap |
| Duplication | ≤3% (>6 lines or >50 tokens) | Does one fact live in two places? |
| Coverage | ≥90% overall, ≥95% critical paths | Add tests before merge |
| Unit test | <100ms | Move it to the integration suite |

## Design principles — the signal matters, not the definition

- **SRP** — one reason to change. Signal: class over the method hint, or high LCOM.
- **OCP** — signal: if/else or switch chain on a type code → polymorphism.
- **LSP** — signal: `NotImplementedException` in a subclass, or a base-class test
  failing on a derived type.
- **ISP** — signal: interface >10 methods, or implementers stubbing members out.
- **DIP** — signal: `new` on a collaborator inside business logic → inject via constructor.
- **DRY** — one authoritative representation per fact.
- **KISS / YAGNI** — simplest thing that works, only what is required now. Dead code
  and unused params are violations.
- **Law of Demeter** — immediate friends only. Signal: `a.getB().getC()`, or a method
  using more of another object than its own.
- **Coupling** — prefer passing data over flags or structures. Never share global
  state or reach into another module's internals.

**Clean Architecture** — dependencies point inward only: Entities → Use Cases →
Interface Adapters → Frameworks & Drivers. No inner-to-outer imports; enforce with
architecture tests, invert with DI. Circular dependencies are never acceptable.

**Function shape** — do one thing. Prefer pure. Command/query separation: do
something *or* return something, not both.

**Error handling**
- Exceptions for exceptional cases; error codes / Result for expected conditions.
- Fail fast at system boundaries; validate all input there.
- Never swallow an exception. Messages state what failed and what to do about it.
- Release resources in `finally` / RAII / `defer`.
- No null returns — Optional / Maybe / Result, or Null Object.

**Self-documenting** — keep a function whole while it reads top to bottom; extract a
piece only when it has two or more callers, the owner asks for it, or its name states
the intention more clearly than the lines do. Name intermediates to explain them
(`isEligible = age > 65 && income < 50000`); function names carry intent
(`ensureUserHasPermission()`); domain language, used consistently. Method order:
public → private, abstract → concrete, high → low.

**Smells worth naming**: divergent change (one class, many reasons), shotgun surgery
(one change, many classes), refused bequest (subclass ignores its inheritance),
middle man (>50% pure delegation), data clumps (same 3+ variables travelling
together), feature envy.

## Test Discipline

Green is a checkpoint, never a stopping point. The ladder:
1. **Unit tests** — on the logic extracted once there are types to test against.
2. **Integration tests** — against real infrastructure (testcontainers, real
   binaries, real ports). A test that mocks the thing it verifies is decoration —
   delete it.
3. **E2E tests** — the whole path, driven the way a caller or user drives it.

Seams decide where tests live; coverage only tells you whether the seams reached
far enough. Coverage short of the floor means a behavior has no seam — add the
seam, never reach into internals to raise a number.

Assert everything the behavior touches: return value, mutated state, persisted
rows, emitted messages, and the error path. One behavior per test.

FIRST: fast, independent, repeatable, self-validating, timely. Arrange-act-assert.
Written before the implementation. Tests ship in the same commit — not the next
commit, not the follow-up PR.

## Domain Model — DDD vocabulary

`domain-modeling` drives the elaboration.

- **Entity** — identity and lifecycle matter.
- **Value Object** — no identity, immutable, compared by value.
- **Aggregate** — the transaction and consistency boundary. One root, one lock.
- **Domain Event** — something that happened; named in past tense.
- **Domain Service** — behavior belonging to no single noun.
- **Repository** — the illusion of an in-memory collection of aggregates.
- **Specification** — a business rule made an object.

Behavior lives on the nouns. An entity of getters and setters with a service
holding all the rules is the anemic model this exists to eliminate. Ubiquitous
language: the names in the code are the names the business uses.

## Code Taste — judgment calls

- **Comments are a confession.** Fix the code, not the comment. Exceptions:
  non-obvious external constraints, measured performance reasons, spec/RFC
  references, TODOs with ticket numbers.
- **Speculative abstractions are debt.** No fields, params, interfaces, or
  extension points for imagined futures. If removing it breaks no current caller,
  remove it.
- **Pre-existing dead code is not yours to delete.** Clean up only the orphans
  your own change created; mention the rest.
- **Duplication is a design bug.** The test: can you change behavior in one place
  and have everything that should change actually change?
- **Building things nobody asked for is the worst code.** Who asked? What breaks
  without it? "In the future" → flag it, don't build it.

**Before declaring code complete:** no unnecessary comments, every field has a
consumer, no duplication, tests in same commit, names self-documenting, nothing
built that wasn't asked for.
