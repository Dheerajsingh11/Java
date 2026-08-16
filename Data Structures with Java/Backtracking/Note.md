# Backtracking

**Backtracking** builds a solution incrementally and abandons a partial candidate ("backtracks") as
soon as it cannot possibly lead to a valid complete solution. It is a refined brute force: it
explores the tree of choices but **prunes** dead branches early.

## The universal template

```
solve(state):
    if state is a complete solution: record it; return
    for each choice from state:
        if choice is valid:
            apply(choice)          # choose
            solve(next state)      # explore
            undo(choice)           # un-choose  <-- the "backtrack"
```

The **choose → explore → un-choose** trio is the heart of every file here.

## Files

| File | Problem | Pruning insight |
|------|---------|-----------------|
| `nQueens.java` | place N non-attacking queens | one queen per row; O(1) column/diagonal checks |
| `permutations.java` | all orderings | skip already-used elements |
| `subsets.java` | power set | include/exclude each element (+ bitmask view) |
| `ratInMaze.java` | path through a grid | skip walls / already-visited cells |
| `sudokuSolver.java` | fill a 9×9 grid | only try digits valid in row/col/box |

## When to use backtracking

- The problem asks for **all** solutions, or **any** valid arrangement, or to **count** them.
- Solutions are built from a sequence of choices with constraints (permutations, combinations,
  subsets, board placements, constraint satisfaction).

## Backtracking vs plain brute force vs DP

- **Brute force** generates every candidate, then tests it. Backtracking tests *while building* and
  prunes — usually far fewer candidates.
- **DP** applies when subproblems overlap and you want an optimum; backtracking applies when you must
  enumerate/search a space (often no overlap to exploit).

## Complexity

Output-sensitive and often exponential (n!, 2ⁿ) because the answer set itself is large. Good pruning
(constraint checks, ordering the most-constrained choice first) is what makes it practical.

## Applications

- Puzzles (Sudoku, crosswords, mazes), constraint satisfaction, combinatorial generation
  (permutations/combinations/subsets), parsing, and search in AI (with pruning like alpha-beta).
