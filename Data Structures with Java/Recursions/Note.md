# Recursion

## What it is

**Recursion** is a function solving a problem by calling itself on a smaller instance of the *same*
problem, until it reaches a case small enough to answer directly.

Two parts are mandatory:
1. **Base case** — the smallest input, answered without recursing. It stops the recursion.
2. **Recursive case** — reduces the problem and must make *measurable progress* toward the base case.

Miss either — or fail to progress — and you get infinite recursion → `StackOverflowError`.

## Why recursion exists

Recursion is not a performance tool. Iteration is usually faster and uses less memory. Recursion
exists because some structures and problems are **defined recursively**, and code that mirrors the
definition is dramatically easier to write and to trust:

- A **tree** is a node plus two subtrees → traversal is 3 lines recursively, ~15 with an explicit stack.
- A **linked list** is a node plus another list.
- **Divide and conquer** (merge sort, quicksort, binary search) is inherently self-similar.
- **Backtracking** (N-Queens, Sudoku) needs the call stack to remember and undo choices.

> Rule of thumb: if the data structure or the problem is self-similar, recursion will be clearer.
> If it is a flat sequence, a loop will be clearer *and* faster.

## How it actually works — the call stack

Each call gets a **stack frame** holding its parameters and locals. Frames pile up until the base
case, then unwind, each returning into its parent.

For `fun1(3)`: frames for 3, 2, 1, 0 exist *simultaneously* at the deepest point — that is the
**O(depth) space cost** that iteration avoids. Java's default stack overflows at roughly 10,000–
50,000 frames, so recursion depth proportional to `n` is risky for large n; depth proportional to
`log n` (binary search, balanced trees) is always safe.

## Where the work happens: before vs after the recursive call

This single choice changes the output completely, and it is the clearest idea in this folder:

```java
print(n); recurse(n-1);   // work BEFORE  -> pre-order  -> prints N..1  (printNto1.java)
recurse(n-1); print(n);   // work AFTER   -> post-order -> prints 1..N  (print1toN.java)
```

Nothing else differs. Work done *before* recursing happens on the way **down**; work done *after*
happens as the stack **unwinds**, in reverse order. The same distinction produces pre-order vs
post-order tree traversal, and it is why `decimalToBinary` prints bits in the correct order for free.

## Tail recursion

A call is **tail-recursive** when nothing remains to do after it returns. Such calls *could* be
turned into a loop with no stack growth — but **the JVM does not perform tail-call optimization**, so
in Java tail recursion still costs O(depth). The distinction is conceptual here (and real in
Scala/Kotlin/Scheme).

## Recursion + memoization = Dynamic Programming

This is the most important connection in the folder.

`fibonacci.java` is O(2ⁿ) because the recursion tree **re-solves the same subproblems** — `fib(30)`
computes `fib(10)` thousands of times. The structure is right; the repetition is the problem.

Cache each result and the exponential tree collapses to a linear chain:

| Version | Time | Where |
|---|---|---|
| Plain recursion | O(2ⁿ) | `Recursions/fibonacci.java` |
| + memoization (top-down) | O(n) | `DynamicProgramming/fibonacciMedium.java` |
| Tabulation, space-optimized | O(n) time, O(1) space | `DynamicProgramming/fibonacciEfficient.java` |

**Overlapping subproblems ⇒ memoize.** That single observation is what DP is.

## When to use recursion

- Trees, graphs, and other self-similar structures.
- Divide and conquer.
- Backtracking / exhaustive search with undo.
- When a recursive formulation is obviously clearer and depth is bounded (ideally O(log n)).

## When NOT to use recursion

- **Deep linear recursion** (depth ~ n on large n) → stack overflow risk; convert to a loop.
- **Simple iteration** — a loop is clearer and faster.
- **Repeated subproblems without caching** → exponential blowup; memoize or go iterative.
- Performance-critical inner loops — call overhead is real.

## Converting recursion to iteration

Always possible. Linear recursion becomes a plain loop; tree recursion needs an **explicit stack**
that mimics what the call stack was doing (see `Trees/TreeTraversalsIterative.java` for the
side-by-side comparison).

## Files in this folder

| File | Teaches |
|---|---|
| `recursion` | the mechanics: base case, recursive case, stack behaviour |
| `printNto1` / `print1toN` | work-before vs work-after — same code, reversed output |
| `sumOfN` | mapping a recurrence to code (plus the O(1) closed form that beats it) |
| `sumOfDigits` | `/10` and `%10` to peel digits; recursive vs iterative space |
| `fibonacci` | tree recursion and why it explodes — the motivation for DP |
| `palindrome` | two pointers converging via recursion; `&&` short-circuiting |
| `decimalToBinary` | unwinding to reverse output order for free |
| `ropeCutting` | multi-branch recursion, optimal substructure, `-1` sentinel propagation |
| `generateSubsets` | include/exclude branching → 2ⁿ subsets |
| `towerOfHanoi` | classic divide & conquer; provably minimal 2ⁿ−1 moves |
| `equalOnes` | (iterative) longest balanced 0/1 subarray — compare the O(n) hashing version |

## Pitfalls

- **Forgetting the base case**, or writing one the recursion can jump *past* (e.g. `fib` needs both
  `n==0` and `n==1`, or the `n-2` branch runs into negatives forever).
- **Not making progress** toward the base case.
- **Corrupting a sentinel**: adding 1 to a `-1` "not found" marker turns it into a valid-looking
  answer. `recursiveSearch` and `ropeCutting` both guard against this explicitly.
- Assuming Java optimizes tail calls. It does not.

## Where recursion is used

Compilers and parsers (recursive descent), file-system traversal, JSON/XML processing, tree and graph
algorithms, divide-and-conquer sorts, backtracking solvers, and fractal/procedural generation.
