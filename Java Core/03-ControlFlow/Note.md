# Control Flow

Control flow decides *which* statements run and *how many times*. Two families: **branching**
(pick a path) and **looping** (repeat).

## Files

| File | Covers |
|------|--------|
| `ifElse.java` | `if` / `else-if` / `else` ladder; ordering; unreachable-else pitfall |
| `switchCase.java` | `switch` (arrow + classic), fall-through, switch-as-expression |
| `loops.java` | `for`, `while`, `do-while`, for-each, `break`, `continue`, labeled break |

## Branching: if/else vs switch

| Use `if / else-if` when… | Use `switch` when… |
|--------------------------|--------------------|
| testing ranges (`x < 10`) | comparing one value to fixed constants |
| complex boolean conditions | many discrete cases (int, char, String, enum) |
| few branches | many equality branches (clearer, jump-table fast) |

## Looping: which loop?

| Loop | Condition checked | Runs at least once? | Best for |
|------|-------------------|---------------------|----------|
| `for` | before each pass | no | known iteration count |
| `while` | before each pass | no | loop until an event |
| `do-while` | after each pass | **yes** | body must run once (menus, prompts) |
| for-each | — | no | read-only iteration over array/collection |

## Jump statements

- `break` — leave the nearest loop/switch immediately.
- `continue` — skip to the next iteration.
- **labeled break/continue** — target an *outer* loop from inside a nested one.

## Pitfalls

- Use `==` to compare, not `=` (assignment).
- Order if/else branches specific → general; make sure the final `else` is actually reachable.
- Classic `case X:` **falls through** without `break`; the arrow form `case X ->` does not.
- for-each gives a **copy** of each element — you cannot modify the source array/list through it.

## Applications

- The backbone of every algorithm: traversals, searching, sorting, and DP all loop; menus and
  input validation use do-while; state machines use switch.
