# Heap / Priority Queue

A **binary heap** is a complete binary tree stored in an array, maintaining the **heap property**:
in a *min-heap* every parent ≤ its children (so the minimum is at the root); a *max-heap* is the
mirror. It backs the **priority queue** abstraction: "always give me the smallest/largest next".

## Array layout (no node objects needed)

For a node at index `i`:
- parent = `(i - 1) / 2`
- left child = `2i + 1`, right child = `2i + 2`

A complete tree has no gaps, so this arithmetic is exact.

## Core operations

| Operation | How | Time |
|-----------|-----|------|
| peek min/max | read root (index 0) | O(1) |
| insert | append at end, **sift up** | O(log n) |
| extract | move last to root, **sift down** | O(log n) |
| build from array | **bottom-up heapify** | **O(n)** |

## Files

| File | Covers |
|------|--------|
| `MinHeap.java` | full array min-heap: insert / peek / extractMin (sift up & down) |
| `buildHeap.java` | build a heap: naive O(n log n) vs bottom-up O(n) |
| `kLargestElements.java` | k largest: sort (naive) / size-k heap (medium) / quickselect (efficient) |
| `medianOfStream.java` | running median with two balanced heaps |

## Why bottom-up build is O(n)
Most nodes are near the leaves, where sift-down does almost no work. Summing work per level gives a
convergent series → O(n), better than n separate O(log n) insertions.

## Heap vs sorting

- Need the **whole thing sorted** → sort, O(n log n).
- Need the **top element repeatedly** (or top k, or a stream) → heap, avoids sorting everything.
- Heap iteration is **not** sorted; only `extract` yields sorted order (that is heap-sort).

## Applications

- **Dijkstra** / **Prim** (pick the next cheapest edge/vertex), Huffman coding, task scheduling by
  priority, k-largest / k-closest, median of a data stream, and merging k sorted lists.
- Library: `java.util.PriorityQueue` (min-heap by default; pass `Collections.reverseOrder()` or a
  comparator for max-heap / custom priority).
