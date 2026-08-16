# Searching

## What it is

Searching answers *"is this value present, and where?"* Every algorithm here is a different bargain
between **what you assume about the data** and **how fast you can answer**.

## Why there is more than one search algorithm

A search is a process of **eliminating candidates**. How many you can eliminate per comparison is
everything:

| Assumption about data | Eliminated per comparison | Cost |
|---|---|---|
| None (unsorted) | 1 element | O(n) |
| Sorted | half the remaining range | O(log n) |
| Sorted + uniformly distributed | most of the range (estimates position) | O(log log n) |

**This is the core lesson: sortedness is not a detail, it is the thing you are paying for.** Sorting
costs O(n log n) once, then every later lookup drops from O(n) to O(log n). One search doesn't repay
that; a thousand searches repay it enormously.

## Why binary search is so fast — concretely

Each comparison halves the range: `n → n/2 → n/4 → … → 1`, which takes log₂(n) steps.

| n | linear search | binary search |
|---|---|---|
| 1,000 | 1,000 | ~10 |
| 1,000,000 | 1,000,000 | ~20 |
| 1,000,000,000 | 1,000,000,000 | ~30 |

Going from a thousand to a billion elements adds only **20 comparisons**. That is why binary search
appears everywhere.

## When to use which

| Situation | Use | Why |
|---|---|---|
| Unsorted data, one-off lookup | **linear** (`basicSearch`) | nothing to exploit; sorting first would cost more |
| Sorted data | **binary** (`binarySearchIterative`) | the default; optimal for comparison-based search |
| Repeated lookups, unsorted | **HashSet/HashMap** | O(1) average — beats sorting when order isn't needed |
| Target likely near the front, or size unknown/unbounded | **exponential** | brackets the target in O(log i) where i is its position |
| Jumping backwards is expensive (tape, sequential storage) | **jump** | only steps forward, then scans one block |
| Rotated sorted array | **`searchRotatedArray`** | one half is always sorted; test membership against it |
| Duplicates, need count or range | **`firstLastOccurrence`** | biased binary search finds the boundaries |
| Find a peak / optimize a unimodal function | **`peakElement`**, ternary | binary search on the *slope*, not the value |

## When NOT to use binary search

- Data is **unsorted** — the answer will be silently wrong, not an error.
- Data is a **linked list** — reaching the middle is already O(n), so the halving buys nothing.
- Only **one lookup** on unsorted data — sorting to enable it costs more than a linear scan.

## The two classic binary-search bugs

**1. Overflow.** `(low + high) / 2` overflows `int` when both are large, producing a negative index.
Always write `low + (high - low) / 2`. (This bug existed in Java's own library for nine years.)

**2. Inconsistent interval convention.** Pick one and stay with it:
- *Closed* `[lo, hi]` → `while (lo <= hi)`, update `hi = mid - 1` / `lo = mid + 1`
- *Half-open* `[lo, hi)` → `while (lo < hi)`, update `hi = mid`
Mixing them gives an infinite loop or an off-by-one. **Every branch must shrink the range**, or the
loop never terminates.

## Beyond arrays: "binary search on the answer"

The most valuable generalization. If a problem has a **monotonic** yes/no property — false, false,
…, true, true — you can binary-search the *answer space* rather than an array.

> "What is the smallest ship capacity that ships all packages in D days?"
> Capacity 10 → too slow; 20 → too slow; 30 → works; 40 → works. Monotonic ⇒ binary search the
> capacity, testing each candidate in O(n).

Recognizing this turns many optimization problems into O(n log(range)).

## Files in this folder

| File | Technique | Time | Requires |
|---|---|---|---|
| `basicSearch` | linear scan | O(n) | nothing |
| `binarySearchIterative` | halving, O(1) space | O(log n) | sorted |
| `binarySearchRecursive` | halving, O(log n) stack | O(log n) | sorted |
| `jumpSearch` | √n blocks + local scan | O(√n) | sorted |
| `ternarySearch` | thirds | O(log₃ n) | sorted |
| `exponentialSearch` | double, then binary | O(log i) | sorted (size may be unknown) |
| `searchRotatedArray` | binary on a rotated array | O(log n) | rotated-sorted |
| `firstLastOccurrence` | biased binary search | O(log n) | sorted |
| `peakElement` | binary search on the slope | O(log n) | any array |

**Note on ternary search:** it makes *fewer iterations* than binary search but *more comparisons per
iteration*, so it is *slower* for plain sorted lookup. Its real use is optimizing **unimodal
functions** (finding a maximum/minimum), not membership testing.

## Where searching shows up

Database indexes (B-trees generalize binary search), `Arrays.binarySearch` /
`Collections.binarySearch`, `git bisect` (binary search over commits), autocomplete (tries — see
`Trees/Trie.java`), and rate/threshold tuning via binary search on the answer.
