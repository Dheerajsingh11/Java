# Sorting

## What it is

Sorting arranges elements into order. It is studied more than any other algorithm family because
sorting is rarely the goal — it is the **enabling step** that makes other problems easy.

## Why sorting matters more than it looks

Sorting converts hard problems into trivial ones. Pay O(n log n) once, then:

| Problem | Unsorted | After sorting |
|---|---|---|
| Search for a value | O(n) | O(log n) — binary search |
| Find duplicates | O(n²) | O(n) — duplicates become adjacent |
| Closest pair of values | O(n²) | O(n) — must be adjacent (`minDiffEfficient`) |
| Merge two datasets | O(n²) | O(n) — two-pointer merge |
| k-th smallest | O(n²) | O(1) — index directly |
| Merge overlapping intervals | O(n²) | O(n) sweep (`mergeOverlapInt`) |

**"Sort first, then the problem is easy" is one of the highest-leverage instincts in algorithm
design.** Several files in `Questions/` are exactly this pattern.

## Why O(n log n) is a wall (and how to break it)

Any sort that works by **comparing** elements needs at least ~n log n comparisons. The reason: n
elements have n! possible orderings, each comparison yields one bit, and log₂(n!) ≈ n log n bits are
needed to identify the right one. No comparison sort can beat this.

**Counting, radix, and bucket sort break the wall** by *not comparing* — they use the values
themselves as array indices. That is why they achieve O(n + k) and why they only work on
integer-like keys with a bounded range.

## The properties that decide which sort to use

**Stability** — do equal elements keep their original relative order?
Why you care: it lets you sort by multiple keys. Sort by name, then *stably* by department, and
within each department names remain alphabetical. Without stability the first sort is destroyed.
Stability is also what makes **radix sort work at all** — each digit pass must preserve the previous
passes' ordering.

**In-place** — O(1) extra memory? Matters for huge datasets and embedded systems.

**Adaptive** — faster on nearly-sorted input? Real-world data is often nearly sorted, so this is
worth more in practice than worst-case bounds suggest.

## Comparison table

| Algorithm | Best | Average | Worst | Space | Stable | Adaptive |
|---|---|---|---|---|---|---|
| Bubble (optimized) | O(n) | O(n²) | O(n²) | O(1) | yes | yes |
| Selection | O(n²) | O(n²) | O(n²) | O(1) | no | no |
| Insertion | O(n) | O(n²) | O(n²) | O(1) | yes | **yes** |
| Merge | O(n log n) | O(n log n) | O(n log n) | O(n) | **yes** | no |
| Quick | O(n log n) | O(n log n) | **O(n²)** | O(log n) | no | no |
| Heap | O(n log n) | O(n log n) | O(n log n) | **O(1)** | no | no |
| Shell | O(n log n) | ~O(n^1.25) | O(n²) | O(1) | no | yes |
| Counting | O(n+k) | O(n+k) | O(n+k) | O(k) | yes | no |
| Radix | O(d(n+k)) | O(d(n+k)) | O(d(n+k)) | O(n+k) | yes | no |
| Bucket | O(n+k) | O(n+k) | **O(n²)** | O(n) | yes | no |
| Cycle | O(n²) | O(n²) | O(n²) | O(1) | no | no |

## When to use which — a decision guide

- **Just use the library.** `Arrays.sort` uses dual-pivot quicksort for primitives (no stability
  needed — two equal `int`s are indistinguishable) and **TimSort** for objects (stable, adaptive).
  Hand-rolled sorts are for learning and for special cases.
- **Need guaranteed O(n log n)** (real-time, adversarial input) → **merge** or **heap**. Quicksort's
  O(n²) worst case is unacceptable when an attacker picks the input.
- **Need stability** → **merge sort** (or TimSort).
- **Memory-constrained, need a guarantee** → **heap sort** — the only common sort that is both
  O(n log n) *and* O(1) space.
- **General speed on random data** → **quicksort**: best constants and cache behaviour in practice.
- **Small (n < ~50) or nearly sorted** → **insertion sort**. Real libraries switch to it for small
  subarrays precisely because its low overhead beats asymptotically better algorithms there.
- **Small integer range** → **counting sort**. Fixed-width keys (IDs, dates) → **radix sort**.
- **Writes are expensive** (flash/EEPROM wear) → **cycle sort** — provably minimal writes.
- **Data exceeds RAM** → external **merge sort** — it streams sequentially and merges runs.

## When NOT to sort

Sorting is O(n log n) — sometimes that is more than the problem needs:

- **You only need the k-th element or the top k** → **quickselect** is O(n) average
  (`Questions/kthSmallestElementEfficient`), or a size-k heap is O(n log k) (`Heap/kLargestElements`).
  Sorting everything to read one position is wasteful.
- **You only need the max/min** → one O(n) pass.
- **You only need membership or duplicate detection** → a `HashSet` is O(n) and does not need order.
- **The data is already sorted or nearly so** → detect it; TimSort and insertion sort exploit
  existing runs. Do not re-sort defensively.
- **You must preserve the original order** → sorting mutates the array. Sort a copy, or sort an index
  array instead.
- **The data does not fit in memory** → an ordinary sort will thrash; use external merge sort.

## Where sorting is used

Databases (`ORDER BY`, index construction, sort-merge joins); deduplication; preparing data for
binary search; leaderboards and rankings; scheduling by priority or deadline; merging log streams by
timestamp; compression (BWT); computational geometry (sweep-line algorithms); and as the setup step
for the interval and two-pointer problems in `Questions/`.

## Why quicksort is usually fastest despite a worse worst case

Big-O hides constants. Quicksort partitions **in place** with a tight inner loop and sequential
memory access, so it exploits the CPU cache; merge sort allocates and copies, and heap sort jumps
between parent and child indices scattered across memory. Real implementations defuse the O(n²) risk
with randomized or median-of-three pivots.

## Partitioning: the shared building block

`partArray` (naive, O(n) space), `lamutoPartition`, and `hoarePartition` implement "rearrange around
a pivot", the engine behind quicksort and quickselect.

- **Lomuto** — simpler; returns the pivot's *final* index, so recursion excludes it: `(l, p-1)` and
  `(p+1, h)`. More swaps; degrades badly on many duplicates.
- **Hoare** — ~3× fewer swaps and handles duplicates well, but returns a **boundary**, not the
  pivot's position, so the left recursion must be `(l, p)`. Using Lomuto's `p-1` here is a classic
  bug — and one that was actually present in this repo.

## Files in this folder

**Simple O(n²):** `bubbleSortUnopt`, `bubbleSortOpt`, `selectionSort{Naive,Efficient}`, `insertionSort`
**Efficient O(n log n):** `mergeSorting`, `quickSortLamuto`, `quickSortHoare`, `heapSort`, `shellSort`
**Non-comparison:** `countingSort{Naive,Efficient}`, `radixSort`, `bucketSort`
**Specialized:** `cycleSort` (minimum writes)
**Partitioning:** `partArray`, `lamutoPartition`, `hoarePartition`
**Merging:** `mergeArr{Naive,Efficient}`
**`Questions/`** — applying sorting: union, intersection, inversions, min difference, segregate by
sign, sort 0/1/2, k-th smallest (quickselect), merge intervals, max concurrent guests.

## Pitfalls

- A single demo input proves nothing. This repo contained three sorts that **did not sort** yet
  produced correct output for their own hard-coded example. Test reversed, all-equal, single-element,
  and empty inputs — and ideally compare against `Arrays.sort` on random data.
- `o1.start - o2.start` in a comparator can **overflow**; use `Integer.compare`.
- An inconsistent comparator can make `TreeMap`/`TreeSet` silently lose elements.
