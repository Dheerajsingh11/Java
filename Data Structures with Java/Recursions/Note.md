# Recursion

**Recursion** is a method that solves a problem by calling itself on smaller inputs until it reaches
a **base case**. It expresses naturally recursive definitions (factorials, trees, subsets) far more
cleanly than loops.

## The two required parts

1. **Base case** — the smallest input, solved directly, that stops the recursion.
2. **Recursive case** — reduce the problem toward the base case and combine the sub-results.

Miss the base case (or fail to make progress toward it) → infinite recursion → `StackOverflowError`.

## Files in this folder

Print 1..N / N..1, sum of N, sum of digits, factorial-style patterns, Fibonacci, palindrome check,
decimal→binary, rope cutting, generate subsets, equal 0s/1s, and Tower of Hanoi.

## How it works (the call stack)

Each call gets its own stack frame (parameters + locals). Calls stack up until the base case, then
unwind, combining results on the way back. Depth = O(number of pending calls), so deep recursion
can overflow — convert to iteration or increase stack size when needed.

## Recursion vs iteration

| | Recursion | Iteration |
|--|-----------|-----------|
| Reads like the definition | yes (trees, divide & conquer) | sometimes clunky |
| Memory | O(depth) stack | O(1) |
| Risk | stack overflow | none |

Any recursion can be rewritten as iteration (sometimes needing an explicit stack).

## Recursion + memoization = Dynamic Programming

When recursive calls **repeat** subproblems (e.g. naive Fibonacci), caching results turns
exponential work into polynomial — that is exactly the DP progression in
`DynamicProgramming/` (naive recursion → memoization → tabulation).

## Applications

- Tree/graph traversal, divide-and-conquer (merge/quick sort, binary search), backtracking
  (N-Queens, subsets, permutations), and parsing.
