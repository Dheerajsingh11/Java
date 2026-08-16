# Greedy Algorithms

## What it is

A **greedy** algorithm builds a solution one step at a time, always taking the choice that looks best
**right now**, and never reconsidering.

## Why greedy is attractive — and dangerous

Greedy is usually the simplest and fastest approach: no recursion, no table, often just a sort plus
one linear pass. When it works it is unbeatable for clarity and speed.

The danger is that **greedy is intuitive but frequently wrong**, and it fails *silently* — producing
a plausible, suboptimal answer rather than an error. So the real skill here is not writing greedy
code; it is **knowing when greedy is valid**.

## The two conditions greedy needs

1. **Greedy-choice property** — a globally optimal solution can be reached by making locally optimal
   choices. The locally best choice is never something you must undo later.
2. **Optimal substructure** — after making that choice, the remaining problem is a smaller instance
   whose optimal solution combines with it.

Condition 1 is the hard one, and it is what usually fails.

## The clearest illustration: knapsack

| Variant | Can items be split? | Correct approach |
|---|---|---|
| **Fractional** knapsack | yes | **Greedy** — take the highest value/weight ratio first |
| **0/1** knapsack | no | **DP** — greedy gives wrong answers |

**Why the difference?** With fractions, every unit of capacity can always be filled with the densest
material still available, so a locally optimal choice is never regretted. With whole items, taking
the densest item can *block* a better combination — e.g. capacity 10 with items (6kg, $60) and two
(5kg, $50): greedy takes the $60 item and wastes 4kg, missing the $100 optimum.

One word — "divisible" — flips the correct technique. That is the lesson: **greedy must be justified,
never assumed.**

## Why sorting is usually step one

Every file here begins by sorting, and the *sort key is the entire algorithm*:

| Problem | Sort by | Why that key |
|---|---|---|
| Activity selection | **finish time** | finishing earliest leaves the most room for everything after |
| Fractional knapsack | **value/weight ratio** | each unit of capacity should hold the most value |
| Job sequencing | **profit (desc)** | take the most valuable jobs first, place each as late as allowed |

**Choosing the wrong key breaks the algorithm.** For activity selection, sorting by *start* time or by
*duration* both give wrong answers — only finish time works, because only it guarantees maximum
remaining room.

## How to prove a greedy choice is safe

The standard tool is an **exchange argument**: take any optimal solution, show you can swap in the
greedy choice without making it worse, and conclude a greedy solution is also optimal.

For activity selection: given an optimal schedule whose first activity ends later than the greedy
choice, swap in the greedy one. It still doesn't conflict with the rest (it ends earlier), so the
count is unchanged — greedy is at least as good.

If you cannot construct such an argument, **assume greedy is wrong** and reach for DP.

## Greedy vs DP — a decision guide

| | Greedy | DP |
|---|---|---|
| Explores | one path | all relevant states |
| Reconsiders choices | never | effectively yes, via the table |
| Speed | usually O(n log n) | often O(n·k) or worse |
| Memory | O(1) typical | O(states) |
| Correct when | greedy-choice property holds | optimal substructure holds |

**Practical heuristic:** try to construct a small counterexample to your greedy idea. Find one → use
DP. Fail to find one *and* you can sketch an exchange argument → greedy.

## When greedy is provably correct — the famous cases

- **Activity selection / interval scheduling** (earliest finish time)
- **Fractional knapsack** (highest density)
- **Huffman coding** (repeatedly merge the two least frequent symbols)
- **Dijkstra's shortest path** — greedy: the nearest unsettled vertex is final (only with
  non-negative weights, which is exactly why negatives break it)
- **Kruskal's and Prim's MST** — greedy, justified by the cut property
- **Coin change with canonical denominations** (like standard currency) — but **not** arbitrary ones:
  with coins {1, 3, 4} and target 6, greedy takes 4+1+1 = 3 coins while the optimum is 3+3 = 2. That
  is precisely why `DynamicProgramming/coinChange.java` uses DP.

## When NOT to use greedy

- Locally optimal choices can be regretted later (0/1 knapsack, general coin change).
- The problem asks for *all* solutions or a count — greedy produces one.
- You can construct a counterexample.
- Constraints interact — choosing one item changes the feasibility of others.

## Files in this folder

| File | Problem | Greedy key | Time |
|---|---|---|---|
| `activitySelection` | maximum non-overlapping activities | earliest **finish** time | O(n log n) |
| `fractionalKnapsack` | maximize value, items divisible | highest **value/weight** | O(n log n) |
| `jobSequencing` | maximize profit within deadlines | highest **profit**, latest free slot | O(n²) |

**Why job sequencing places jobs as late as possible:** scheduling a job at the latest slot before its
deadline keeps the earlier slots free for jobs with tighter deadlines. (With a Union-Find "next free
slot" structure this drops to near O(n log n) — see `DisjointSet/`.)

## Where greedy algorithms are used

Scheduling (CPU, meetings, jobs), **Huffman compression** (ZIP, JPEG, MP3), network routing
(Dijkstra), network design (MST), cache eviction heuristics, change-making in point-of-sale systems,
and as **approximation algorithms** for NP-hard problems where an exact answer is out of reach but a
provably-close greedy answer is acceptable.
