# Backtracking

## What it is

**Backtracking** builds a solution incrementally and **abandons a partial candidate the moment it
cannot possibly lead to a valid complete solution**. It is brute force that gives up early.

## Why it beats plain brute force

Brute force generates every candidate, then tests each. Backtracking tests **while building**, so an
invalid prefix eliminates every candidate that would have extended it.

Concretely, for 8-Queens:

| Approach | Candidates examined |
|---|---|
| All placements of 8 queens on 64 squares | C(64,8) ≈ **4.4 billion** |
| One queen per row (structural constraint) | 8⁸ ≈ 16.7 million |
| **+ backtracking with pruning** | ~**15,000** nodes |

The algorithm is still exponential in the worst case — but **pruning is the difference between
"impossible" and "instant"**. That is the entire value proposition.

## The universal template

Every file in this folder is this shape:

```
solve(state):
    if state is a complete solution: record it; return
    for each candidate choice:
        if choice is valid:
            apply(choice)        # CHOOSE
            solve(next state)    # EXPLORE
            undo(choice)         # UN-CHOOSE  <- the "backtrack"
```

**The un-choose step is the whole trick.** It restores the state so the next branch starts clean,
which lets one mutable structure serve the entire search tree instead of copying state at every node.

> Note `Recursions/generateSubsets.java` needs *no* explicit undo — it builds new immutable strings
> instead. Explicit backtracking is what you do when you mutate shared state for efficiency.

## Pruning is where the performance lives

The template is easy; making it fast is about **rejecting bad branches as early and as cheaply as
possible**.

| File | Pruning insight |
|---|---|
| `nQueens` | one queen per row (structural), plus O(1) column/diagonal occupancy checks |
| `sudokuSolver` | only try digits currently legal in that row, column, and 3×3 box |
| `ratInMaze` | skip walls and cells already on the current path |
| `permutations` | skip elements already used on this branch |
| `subsets` | no pruning needed — every branch is valid |

**The O(1) constraint check matters as much as the pruning itself.** `nQueens` uses three boolean
arrays (columns, both diagonal directions) so "is this square attacked?" is three array reads instead
of scanning the board. Cheap checks let you prune aggressively without the checking becoming the
bottleneck.

## Diagonal indexing in N-Queens (a neat trick worth knowing)

For a square at `(row, col)`:
- `row + col` is constant along one diagonal direction
- `row - col` is constant along the other (offset by `n-1` to keep the index non-negative)

That converts a geometric test into an array lookup.

## When to use backtracking

- **Enumerate all** solutions (all subsets, all permutations, all valid boards).
- Find **any one** valid configuration (Sudoku, maze path).
- **Count** solutions (N-Queens count).
- **Constraint satisfaction**: choices interact and must all hold simultaneously.
- The solution is naturally a **sequence of decisions** with rules about which are legal.

## When NOT to use backtracking

- **Overlapping subproblems + you want an optimum** → **dynamic programming**. Backtracking explores
  the tree; DP collapses it. If you find yourself re-solving identical states, switch.
- **A greedy choice is provably safe** → greedy is vastly faster.
- **The search space is astronomically large with weak constraints** → pruning won't save you;
  consider heuristics, branch-and-bound, or approximation.
- You only need **one** solution and a direct construction exists.

## Backtracking vs DP vs Greedy

| | Backtracking | DP | Greedy |
|---|---|---|---|
| Explores | the whole tree, pruned | all distinct states, memoized | one path |
| Answers | all / any / count | the optimum | the optimum (when valid) |
| Typical cost | exponential | polynomial | O(n log n) |
| Needs | validity constraints | overlapping subproblems | greedy-choice property |

## Complexity — and why it is unavoidable

Backtracking complexity is **output-sensitive**: enumerating all 2ⁿ subsets or n! permutations cannot
be faster than the size of the answer. When the *output itself* is exponential, so is the algorithm.
Pruning reduces the constant and the explored tree, not the asymptotic lower bound on enumeration.

## Files in this folder

| File | Problem | Search space |
|---|---|---|
| `subsets` | power set (backtracking **and** bitmask views) | 2ⁿ |
| `permutations` | all orderings | n! |
| `nQueens` | place N non-attacking queens | pruned heavily |
| `ratInMaze` | find a path through a grid | 4^(R·C) worst, heavily pruned |
| `sudokuSolver` | fill a 9×9 grid | exponential, but real puzzles solve fast |

**The bitmask view of subsets** (in `subsets.java`) is worth noting: iterate `0 .. 2ⁿ-1` and let bit
*j* decide whether element *j* is included. It replaces recursion with a loop and is the foundation
of **bitmask DP**.

## Pitfalls

- **Forgetting to un-choose** — state leaks between branches and results are silently wrong.
- **Recording a reference instead of a copy** — `out.add(current)` stores the live list, which keeps
  mutating. Must be `out.add(new ArrayList<>(current))`.
- **Marking visited but never unmarking** (maze/grid problems) — blocks legitimate alternative paths.
- **Pruning too late** — checking validity only at complete solutions makes it plain brute force.

## Where backtracking is used

Puzzle solvers (Sudoku, crosswords, mazes, N-Queens); constraint satisfaction and scheduling;
regular-expression backtracking engines; parser exploration of ambiguous grammars; combinatorial
generation for testing; game AI move search (with alpha-beta pruning, which is backtracking plus
bounds); and circuit/layout design.

## Also in this folder

`wordSearch` — grid path search; demonstrates why the un-choose step is mandatory.
