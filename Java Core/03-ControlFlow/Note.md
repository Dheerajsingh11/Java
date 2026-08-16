# Control Flow

## What this covers

Control flow decides **which** statements run and **how many times**. Two families: **branching**
(pick a path) and **looping** (repeat).

## Branching: `if/else` vs `switch`

They are not interchangeable — each is built for a different shape of decision.

| | `if / else-if` | `switch` |
|---|---|---|
| Tests | **any boolean condition** | **equality** against constants |
| Handles ranges (`x < 10`) | yes | no |
| Many discrete cases | verbose | **clean** |
| Speed | sequential tests | can compile to a **jump table** |

**Why `switch` can be faster:** with dense integer cases the compiler emits a jump table — one
indexed jump regardless of how many cases exist. An `if/else` ladder tests conditions one by one, so
the last branch costs the most. With 50 cases that is a real difference.

**Rule:** comparing *one variable* against *many constants* → `switch`. Testing *ranges* or *complex
conditions* → `if/else`.

## Ordering an if/else ladder correctly

Conditions are evaluated top-down and the **first true branch wins**, so order matters:

```java
if (a < 10)       ...   // narrowest first
else if (a < 20)  ...   // reached only when a >= 10
else              ...   // catch-all
```

**Watch for an unreachable `else`.** Writing `if (a <= 10) ... else if (a > 10) ... else ...` makes
the final `else` dead code, because every value satisfies one of the first two. This exact bug was
present in this repo's original `ifElse.java`. If your `else` cannot be reached, the ladder is
mis-structured — either fix the conditions or delete the branch.

## `switch`: arrow vs colon, and the fall-through trap

```java
case 5 -> doSomething();     // arrow (Java 14+): only this branch runs, NO fall-through
case 5: doSomething(); break;  // colon: falls through to the next case without break
```

**Fall-through** is the classic C-family bug: forget `break` and execution continues into the next
case. The arrow form eliminates it entirely — **prefer it in new code**.

Fall-through is occasionally *intentional*, to group labels that share code:
```java
case 'A':
case 'B':                 // both fall through to the same block
    System.out.println("Great");
    break;
```

**`switch` as an expression** (Java 14+) yields a value directly, which removes a whole class of
"forgot to assign in one branch" bugs:
```java
String name = switch (day) { case 1 -> "Mon"; case 2 -> "Tue"; default -> "Other"; };
```

`switch` works on `int`, `char`, `String`, and `enum`. With enums the compiler can warn about
unhandled constants — a strong reason to prefer enums over magic strings.

## Choosing a loop

| Loop | Condition checked | Runs at least once? | Use when |
|---|---|---|---|
| `for` | before each pass | no | the **iteration count is known** |
| `while` | before each pass | no | loop **until an event**; count unknown |
| `do-while` | **after** each pass | **yes** | the body must run once (menus, prompts, retry) |
| for-each | — | no | **read-only** iteration over an array/collection |

**`do-while` exists for a specific reason:** "ask the user, then validate" must run before there is
anything to validate. Any loop where the first iteration is unconditional is a `do-while`.

## for-each: what it buys and what it costs

```java
for (int value : arr) { ... }
```

**Buys:** no index bookkeeping, no off-by-one errors, and — importantly — **O(n) iteration over a
`LinkedList`**, where an index-based loop would be O(n²).

**Costs:** no index available; you cannot modify the source collection; and for primitives you get a
**copy**, so assigning to the loop variable changes nothing. Use an indexed `for` when you need the
position or need to write back.

Modifying a collection inside a for-each throws `ConcurrentModificationException` — use
`Iterator.remove()` or `removeIf` instead (see `09-Collections`).

## `break`, `continue`, and labels

- `break` — leave the nearest loop or switch immediately.
- `continue` — skip the rest of this iteration, proceed to the next.
- **Labeled** `break`/`continue` — target an **outer** loop from inside a nested one.

A plain `break` inside nested loops only exits the inner one. A label is the clean way to escape both:

```java
outer:
for (...) for (...) if (found) break outer;
```

The alternative — a `boolean found` flag checked in both loop conditions — is more code and easier to
get wrong. Labels have a bad reputation from `goto`, but a labeled break is structured and safe.

## When NOT to use explicit loops

For transforming or filtering collections, the **Streams API** (`11-Functional/`) often reads better:

```java
list.stream().filter(n -> n % 2 == 0).map(n -> n * n).toList();
```

Use streams when the pipeline expresses intent more clearly; use plain loops for simple iteration,
early exit, or performance-critical hot paths.

## Files in this folder

| File | Covers |
|---|---|
| `ifElse` | ladders, ordering, the unreachable-`else` bug |
| `switchCase` | arrow vs colon syntax, intentional fall-through, switch expressions |
| `loops` | `for`, `while`, `do-while`, for-each, `break`, `continue`, labeled break |

## Pitfalls

- `=` (assignment) instead of `==` (comparison) in a condition.
- Forgetting `break` in a colon-style `switch`.
- An unreachable final `else`.
- Off-by-one in `for` bounds — `<` vs `<=` against `length`.
- Modifying a collection during for-each iteration.
- `for (int i = 0; i < list.size(); i++) list.get(i)` on a `LinkedList` → accidental O(n²).

## Where this leads

Every algorithm in this repo is built from these constructs: traversals and searches are loops,
recursion's base case is an `if`, state machines are `switch`, and BFS/DFS are `while` loops over a
queue or stack.
