# Dynamic Programming (DP)

**Dynamic programming** solves a problem by combining solutions to overlapping subproblems, each
solved once and reused. Two ingredients must be present:

1. **Optimal substructure** — an optimal solution is built from optimal solutions of subproblems.
2. **Overlapping subproblems** — the same subproblems recur (otherwise plain divide-and-conquer is
   enough).

## The three tiers (how every DP file here progresses)

| Tier | Name | Idea | Typical cost |
|------|------|------|--------------|
| Naive | brute-force recursion | write the recurrence directly | often exponential |
| Medium | **memoization** (top-down) | cache each subproblem result | polynomial |
| Efficient | **tabulation** (bottom-up) + space optimization | fill a table, then keep only what's needed | polynomial, minimal memory |

`fibonacci{Naive,Medium,Efficient}` and `knapsack01{Naive,Medium,Efficient}` show all three tiers
end to end.

## Files

| File(s) | Problem | Recurrence in one line |
|---------|---------|------------------------|
| `fibonacci*` | n-th Fibonacci | F(n) = F(n-1) + F(n-2) |
| `knapsack01*` | 0/1 knapsack | take vs skip each item |
| `longestCommonSubsequence` | LCS of two strings | match → 1+diag, else max(drop a, drop b) |
| `longestIncreasingSubsequence` | LIS | O(n²) DP and O(n log n) patience method |
| `coinChange` | fewest coins for an amount | dp[x] = 1 + min over coins dp[x-c] |
| `editDistance` | Levenshtein distance | match → diag, else 1 + min(replace,delete,insert) |

## A recipe for solving DP problems

1. Define the **state** (what parameters identify a subproblem, e.g. `(i, capacity)`).
2. Write the **recurrence** (how a state depends on smaller states).
3. Identify the **base cases**.
4. Choose **top-down** (memoize the recursion) or **bottom-up** (fill a table in dependency order).
5. **Optimize space** — if a state only depends on the previous row/two values, drop the full table.

## Memoization vs tabulation

| | Memoization (top-down) | Tabulation (bottom-up) |
|--|------------------------|------------------------|
| Shape | recursion + cache | loops filling a table |
| Computes | only needed states | all states in range |
| Risk | stack overflow (deep) | none |
| Ease | closest to the recurrence | needs an explicit order |

## Applications

- Sequence alignment (DNA, diff), spell-check, text justification, resource allocation (knapsack),
  shortest paths (Bellman-Ford, Floyd-Warshall are DP), and countless optimization problems.
