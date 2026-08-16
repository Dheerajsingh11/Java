# Greedy Algorithms

A **greedy** algorithm builds a solution step by step, always taking the choice that looks best
*right now* (a local optimum), never reconsidering. It is simple and fast — but only correct when the
problem has the right structure.

## When greedy is provably correct

A greedy choice yields the global optimum when the problem has:

1. **Greedy-choice property** — a globally optimal solution can be reached by locally optimal choices.
2. **Optimal substructure** — an optimal solution contains optimal solutions to subproblems.

If these do not hold, greedy gives a wrong (though often "good enough") answer, and you likely need
**DP** instead.

## Files

| File | Problem | Greedy key |
|------|---------|-----------|
| `activitySelection.java` | most non-overlapping activities | sort by **finish time** |
| `fractionalKnapsack.java` | max value, items divisible | sort by **value/weight ratio** |
| `jobSequencing.java` | max profit within deadlines | sort by **profit**, latest free slot |

## Greedy vs DP — a crucial contrast

- **Fractional** knapsack → greedy is optimal (you can take fractions).
- **0/1** knapsack → greedy FAILS; you need DP (see `DynamicProgramming/knapsack01*`).

The single change (can you split an item?) flips the correct technique — a classic lesson that
greedy must be *justified*, not assumed.

## Proving a greedy choice

The usual tool is an **exchange argument**: show that any optimal solution can be transformed into
one that makes the greedy choice, without losing optimality.

## Applications

- Scheduling (activities, jobs, intervals), Huffman coding (compression), Dijkstra/Prim (shortest
  path / MST are greedy), coin systems (for canonical denominations), and approximation algorithms.
