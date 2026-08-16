# Sorting

Sorting arranges elements in order. It is worth knowing several algorithms because they trade off
simplicity, speed, memory, and **stability** differently.

## Comparison of algorithms in this folder

| Algorithm | Best | Average | Worst | Space | Stable? | Notes |
|-----------|------|---------|-------|-------|---------|-------|
| Bubble (`bubbleSort*`) | O(n) | O(n²) | O(n²) | O(1) | yes | optimized version stops early if sorted |
| Selection (`selectionSort*`) | O(n²) | O(n²) | O(n²) | O(1) | no | fewest swaps (n-1) |
| Insertion (`insertionSort`) | O(n) | O(n²) | O(n²) | O(1) | yes | great for small/nearly-sorted data |
| Merge (`mergeSorting`) | O(n log n) | O(n log n) | O(n log n) | O(n) | yes | divide & conquer; external-sort friendly |
| Quick (`quickSortHoare/Lamuto`) | O(n log n) | O(n log n) | O(n²) | O(log n) | no | fast in practice; pivot choice matters |
| Heap (`heapSort`) | O(n log n) | O(n log n) | O(n log n) | O(1) | no | in-place, guaranteed n log n |
| Counting (`countingSort*`) | O(n+k) | O(n+k) | O(n+k) | O(k) | yes | non-comparison; small integer range k |
| Radix (`radixSort`) | O(d·(n+k)) | — | — | O(n+k) | yes | digit by digit; fixed-width keys |
| Bucket (`bucketSort`) | O(n+k) | O(n+k) | O(n²) | O(n) | yes | uniform distribution assumption |

## Key concepts

- **Stability**: equal elements keep their original relative order. Matters when sorting by one key
  after another (e.g. sort by name, then stably by age).
- **In-place**: uses O(1)/O(log n) extra space (bubble, selection, insertion, heap, quicksort).
- **Comparison vs non-comparison**: comparison sorts are bounded below by O(n log n); counting/radix/
  bucket beat that by exploiting key structure (small integer range, digits).
- **Partitioning**: `lamutoPartition` / `hoarePartition` / `partArray` are the building blocks of
  quicksort and quickselect.

## Choosing

- General purpose, need stability → **merge sort** (or `Arrays.sort` on objects, which is TimSort).
- General purpose, in-place, average speed → **quicksort** (`Arrays.sort` on primitives).
- Guaranteed O(n log n), O(1) space → **heap sort**.
- Small integer range / digits → **counting / radix**.
- Tiny or nearly-sorted input → **insertion sort**.

## Questions subfolder
`Questions/` applies sorting to problems: union/intersection, inversions, min difference,
segregate +/-, sort 0/1/2, kth smallest, merge intervals, meeting max guests.

## Applications
Databases (ORDER BY), deduplication, binary-search prerequisite, priority scheduling, and any
"process items in order" task.
