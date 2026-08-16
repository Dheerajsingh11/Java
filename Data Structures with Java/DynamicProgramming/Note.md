# Dynamic Programming (DP)

## What it is

**Dynamic programming** solves a problem by breaking it into subproblems, solving each **once**, and
reusing the answers.

It applies exactly when both conditions hold:

1. **Optimal substructure** — an optimal solution is built from optimal solutions of subproblems.
2. **Overlapping subproblems** — the same subproblems recur many times.

If subproblems *don't* overlap, plain divide-and-conquer (merge sort, binary search) is the right
tool — there is nothing to cache.

## Why DP works — the whole idea in one example

Naive `fib(n)` is **O(2ⁿ)** because it recomputes the same values astronomically often: `fib(50)`
recomputes `fib(10)` millions of times. The *structure* is correct; the **repetition** is the problem.

There are only n distinct subproblems (`fib(0)` … `fib(n)`). Remember each one and the exponential
tree collapses to a linear chain:

| Version | Time | Why |
|---|---|---|
| Brute recursion | O(2ⁿ) | recomputes everything |
| + memoization | **O(n)** | each subproblem solved once |
| Tabulation, space-optimized | O(n) time, **O(1)** space | only the last two values are ever needed |

**DP is not a specific algorithm — it is the discipline of not repeating work.**

## The three tiers (how every DP file here progresses)

| Tier | Name | Shape | Cost |
|---|---|---|---|
| **Naive** | brute-force recursion | write the recurrence directly | usually exponential |
| **Medium** | **memoization** (top-down) | same recursion + a cache | polynomial |
| **Efficient** | **tabulation** (bottom-up) + space optimization | loops filling a table | polynomial, minimal memory |

`fibonacci{Naive,Medium,Efficient}` and `knapsack01{Naive,Medium,Efficient}` show all three end to end.

## Memoization vs tabulation — which to choose

| | Memoization (top-down) | Tabulation (bottom-up) |
|---|---|---|
| How you write it | recursion + cache | loops in dependency order |
| Computes | **only the states you need** | every state in range |
| Risk | **stack overflow** on deep recursion | none |
| Ease | closest to the recurrence — easier to derive | needs you to work out the fill order |
| Space optimization | hard | **easy** (drop old rows) |

**Practical advice:** derive with memoization (it mirrors your thinking), then convert to tabulation
if you need the space optimization or fear stack depth. If the state space is sparse — most states
never reached — memoization can be *faster*, since tabulation dutifully fills them all.

## A recipe for solving any DP problem

1. **Define the state.** What parameters identify a subproblem? (`(index, capacityLeft)`)
2. **Write the recurrence.** How does a state depend on smaller states?
3. **Identify base cases.** The smallest inputs, answered directly.
4. **Choose a direction.** Memoize the recursion, or fill a table bottom-up.
5. **Optimize space.** If a state only needs the previous row (or last two values), drop the rest.

Step 1 is where most difficulty lives. **Get the state right and the recurrence usually writes itself.**

## Reading the recurrences in this folder

| Problem | Recurrence in one line | Insight |
|---|---|---|
| Fibonacci | `F(n) = F(n-1) + F(n-2)` | the canonical overlap demo |
| 0/1 Knapsack | `max(skip item, take item + solve(remaining capacity))` | every item is a binary choice |
| LCS | match → `1 + diag`; else `max(drop from a, drop from b)` | compare the *last* characters |
| Edit distance | match → `diag`; else `1 + min(replace, delete, insert)` | three edit operations = three predecessors |
| Coin change | `dp[x] = 1 + min over coins of dp[x - c]` | unbounded reuse of coins |
| LIS | `dp[i] = 1 + max(dp[j])` for `j < i, a[j] < a[i]` | O(n²); patience method gives O(n log n) |

## The subtlety in 1-D knapsack: loop direction

In the space-optimized 0/1 knapsack, capacity is swept **high → low**:

```java
for (int c = W; c >= wt[i]; c--) dp[c] = max(dp[c], val[i] + dp[c - wt[i]]);
```

Going **low → high** instead would let the same item be used **multiple times** — because `dp[c - wt[i]]`
would already reflect the current item. That single direction change is the difference between the
0/1 knapsack and the *unbounded* knapsack. It is the most commonly missed detail in DP.

## "Pseudo-polynomial" — an important caveat

Knapsack's O(n·W) is polynomial in the *value* W but exponential in the number of **bits** used to
write W. Doubling W's digit count squares the work. This is why knapsack is still considered NP-hard
despite the DP.

## When to use DP

- Optimization: maximize/minimize over choices ("best", "fewest", "longest").
- Counting: "how many ways…".
- Feasibility: "is it possible to reach exactly X?".
- Sequence comparison: diffs, alignment, similarity.
- Whenever a brute-force recursion is obviously re-solving the same states.

## When NOT to use DP

- **No overlapping subproblems** → divide and conquer.
- **A greedy choice is provably safe** → greedy is simpler and faster. (Fractional knapsack: greedy.
  0/1 knapsack: DP. The single difference is whether items can be split — see `Greedy/Note.md`.)
- **State space too large** to store (exponential in the natural parameters).
- **A closed-form formula exists** — don't build a table to compute `n(n+1)/2`.

## Files in this folder

**Three-tier ladders:** `fibonacci{Naive,Medium,Efficient}`, `knapsack01{Naive,Medium,Efficient}`
**Classic problems:** `longestCommonSubsequence`, `longestIncreasingSubsequence` (O(n²) DP **and**
O(n log n) patience method), `coinChange`, `editDistance`

## Pitfalls

- **Wrong state definition** — the usual root cause when a DP "almost works".
- **Missing base cases**, or ones the recursion can jump past.
- **Sentinel corruption** — using `-1` for "impossible" and then adding to it.
- **Overflow** in counting problems — use `long`, or take a modulus.
- Forgetting that memo arrays need an "uncomputed" marker distinct from a legitimate 0 answer.

## Where DP is used

`diff` and version control; spell-checkers and autocorrect (edit distance); DNA/protein sequence
alignment (Levenshtein/Needleman-Wunsch); resource allocation and budgeting (knapsack); text
justification in typesetting; speech recognition (Viterbi); reinforcement learning (Bellman
equations); and shortest-path algorithms — **Bellman-Ford and Floyd-Warshall are dynamic programming**.

## Also in this folder

`matrixChainMultiplication` (interval DP — the state is a *range*) · `subsetSum` (three tiers; the backwards sweep that enforces 0/1).
