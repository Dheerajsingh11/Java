# Heap / Priority Queue

## What it is

A **binary heap** is a complete binary tree stored in a plain array, maintaining the **heap
property**: in a min-heap every parent ≤ its children, so the minimum sits at the root. A max-heap is
the mirror. It implements the **priority queue** abstraction: *"always give me the most important
element next."*

## Why heaps exist — the gap they fill

Suppose you repeatedly need the smallest element from a changing collection.

| Approach | Get min | Insert | Problem |
|---|---|---|---|
| Unsorted array | O(n) scan | O(1) | slow retrieval |
| Sorted array | O(1) | **O(n)** insert (shift) | slow insertion |
| Balanced BST | O(log n) | O(log n) | works, but heavier and fully ordered |
| **Heap** | **O(1) peek** | **O(log n)** | — |

**The insight: a heap is only *partially* ordered.** It guarantees parent ≤ children and nothing
else — siblings are unrelated. That is exactly enough to expose the extreme element instantly, and
maintaining it is far cheaper than maintaining full order. Doing less work is the whole point.

Consequence: **iterating a heap does not give sorted output.** Only repeated `poll()` does.

## Why the array layout works

A heap is a *complete* tree — every level full except possibly the last, filled left to right. That
completeness means it packs into an array with no gaps, so parent/child links are pure arithmetic:

```
parent(i) = (i - 1) / 2      left(i) = 2i + 1      right(i) = 2i + 2
```

No node objects, no pointers, perfect cache locality. This is why heaps are fast in practice, not
just in theory.

## The two operations that maintain the invariant

- **Sift up** (after inserting at the end): swap with the parent while smaller. O(log n) — the height.
- **Sift down** (after moving the last element to the root): swap with the *smaller* child until
  settled. O(log n).

Both touch only one root-to-leaf path, which is why they are logarithmic.

## Why building a heap is O(n), not O(n log n)

This surprises people. Inserting n elements one at a time is n × O(log n) = O(n log n). But
**bottom-up heapify** (`buildHeap.java`) is O(n).

Why: sift-down cost depends on **distance to the bottom**, and a heap is mostly bottom. Half the
nodes are leaves needing zero work; a quarter sift down at most 1 level; an eighth at most 2. The sum
`n/2·0 + n/4·1 + n/8·2 + …` converges to **n**. Only the few nodes near the root do real work.

Practical rule: **have all the data up front → bottom-up build (O(n)). Data arrives one at a time →
repeated insertion (O(n log n)).**

## When to use a heap

- You repeatedly need the **min or max** of a changing set.
- **Top-k** problems — and this is worth internalizing:
  > To find the k **largest** elements, use a size-k **MIN**-heap.
  The root is the smallest of the best-k-so-far, so it is exactly the element to evict when something
  better arrives. Cost: **O(n log k)** and O(k) memory — better than sorting (O(n log n)) when k ≪ n,
  and it works on a **stream** that never fits in memory. (`kLargestElements.java`)
- **Scheduling by priority** rather than arrival order.
- **Two heaps** for a running median (`medianOfStream.java`): a max-heap for the lower half and a
  min-heap for the upper half, kept balanced. The median is always at the tops — O(log n) insert,
  O(1) query. Sorting after every element would be O(n log n) *per element*.

## When NOT to use a heap

- You need **full sorted order** → just sort. A heap gives you order only one extraction at a time.
- You need **search by value** → O(n) in a heap; use a hash table or BST.
- You need **sorted iteration or range queries** → heaps offer no ordering between siblings; use a
  `TreeMap`.
- You need **arbitrary deletion** → not supported efficiently without extra index bookkeeping.

## Heap sort — the guarantee nobody else offers

`Sorting/heapSort.java`: build a max-heap (O(n)), then repeatedly swap the root to the end and
re-heapify. It is the **only common sort that is both O(n log n) worst case and O(1) space** — merge
sort needs O(n) memory, quicksort risks O(n²). Its downside is cache-hostile index jumping, which is
why quicksort usually wins on wall-clock time despite the worse worst case.

## Complexity

| Operation | Cost |
|---|---|
| peek min/max | **O(1)** |
| insert (sift up) | O(log n) |
| extract (sift down) | O(log n) |
| build from an array | **O(n)** bottom-up |
| search arbitrary value | O(n) |
| space | O(n), in place |

## Files in this folder

| File | Covers |
|---|---|
| `MinHeap` | full array-backed implementation: insert, peek, extract, sift up/down |
| `buildHeap` | naive O(n log n) insertion vs bottom-up O(n) heapify |
| `kLargestElements` | 3 tiers — sort O(n log n), size-k heap O(n log k), quickselect O(n) avg |
| `medianOfStream` | two balanced heaps for a running median |

## Java note

`java.util.PriorityQueue` is a min-heap by default. For a max-heap pass
`Collections.reverseOrder()`, or a custom `Comparator` for domain priorities. Note that its
`toString()`/iteration shows **heap order, not sorted order** — a common source of confusion.

## Where heaps are used

**Dijkstra** and **Prim** (repeatedly pick the cheapest next edge/vertex — see `Graphs/`);
**Huffman coding** (repeatedly merge the two least frequent symbols — see `Greedy/`); OS task and
bandwidth schedulers; event-driven simulation (next event by timestamp); k-nearest-neighbour search;
merging k sorted streams; and load balancing.
