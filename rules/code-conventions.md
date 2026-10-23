# Code conventions

Skills: clean-code (thresholds and taste), plus the project language's best-practice skill (for Rust: rust-best-practices, rust-design-idioms).

Code the owner reads without decoding. Boring code that reads fast beats
short or clever code. On conflict: fewest concepts first, then fewest lines.

1. Idioms, not machinery. Use the language's own constructs: iterator or
   stream chains, pattern matching, early-exit forms. Iterator chains are
   the preferred style for transforming a collection, not clever code.
   Machinery is a macro or metaprogramming where a function works, or an
   interface with one implementation.

2. Comments explain why, never ticket provenance. Code that needs
   explaining gets rewritten until it doesn't. A comment states only what
   the code cannot show, such as a constraint or a deliberate ceiling. Which
   ticket or review asked for a change belongs in the commit, not the code.

3. Explicit names, with boundary context. Names come from the domain; a
   reader who knows the problem reads the line without looking anything up.
   No abbreviations. Types are domain nouns. A function that does work is
   verb + domain noun (insert node, find position); a bare noun only for a
   cheap accessor (length, edges). Booleans read as yes/no questions (is
   stale, has address). Numbers carry their unit (timeout in milliseconds,
   age in seconds). Loop variables are named (node, not n). Free
   functions, entry points, services and facades carry the context their
   call site lacks. Published wire and tool names are frozen; the code
   behind them follows this rule.

4. One type per concept. No mirror types: no second type that only copies
   another one's fields for a different layer (raw, input or transfer-object
   twins).

5. Closed sets are enums; branch on them with match/switch, not if/else
   chains.

6. Let the library do it declaratively (annotations, generated code, config) before
   writing glue around it.

7. Keep functions whole. A helper earns its place only when it has two
   callers, the owner asks for it, or its name states a rule or intention
   the inline code would hide. A helper that only bridges two shapes of the
   same thing is deleted. Trace the logic in one place.

8. Early return. Guard clauses instead of if/else chains, so the main path
   reads straight down.

9. Blank lines between steps: after each if/match block, and between
   groups of statements doing different steps, so the steps show at a
   glance.

10. No emoji. Each language's standard formatter and linter at their
    defaults: rustfmt and clippy with warnings as errors for Rust, gofmt for
    Go, Google Java Style for Java, strict mode for TypeScript.

11. A proven library before hand-rolled code, as a high priority. Nothing is
    written from scratch before a search for a battle-tested, mature library
    that already solves the problem; a hand-rolled solution is the last
    resort, never the first instinct.
    - Proven means maintained, widely used, with a real test suite and real
      users reporting real bugs against it. Open security advisories or no
      recent release rule a library out; popularity alone is not proof.
    - If no library fits, say so and why before writing custom code. The
      default answer is "someone already built this."
    - This covers algorithms, parsers, serializers, HTTP clients, crypto,
      date handling, validation: everything where a wrong implementation is
      worse than a dependency.
