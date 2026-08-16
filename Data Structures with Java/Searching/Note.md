# Searching

Searching finds whether (and where) a value exists in a collection. The single biggest lever is
whether the data is **sorted** — sorted data unlocks logarithmic search.

## Files

| File | Technique | Time | Needs sorted? |
|------|-----------|------|---------------|
| `basicSearch.java` | linear scan | O(n) | no |
| `binarySearchIterative.java` | halve each step | O(log n) | yes |
| `binarySearchRecursive.java` | halve each step (recursive) | O(log n) | yes |
| `jumpSearch.java` | √n blocks + local scan | O(√n) | yes |
| `ternarySearch.java` | thirds each step | O(log₃ n) | yes |
| `exponentialSearch.java` | double then binary | O(log i) | yes (also unbounded) |
| `searchRotatedArray.java` | binary on rotated array | O(log n) | rotated-sorted |
| `firstLastOccurrence.java` | biased binary search | O(log n) | yes |
| `peakElement.java` | binary on the slope | O(log n) | any array |

## How to choose

- **Unsorted** data → linear search (or hash the data for O(1) membership).
- **Sorted** data → binary search (the default) at O(log n).
- **Target near the front / unknown size** → exponential search.
- **Sequential-only storage (jumping back is costly)** → jump search.
- **Rotated sorted** → the rotated-array binary variant.
- **Duplicates, need the range/count** → first/last occurrence.
- **Unimodal data (find a peak/optimum)** → ternary search / peak-element binary search.

## Binary search: the two traps

1. Compute mid as `lo + (hi - lo) / 2`, **not** `(lo + hi) / 2`, to avoid integer overflow.
2. Be consistent with the loop bound: `while (lo <= hi)` with `hi = mid - 1` / `lo = mid + 1`
   (closed interval) vs `while (lo < hi)` with `hi = mid` (half-open). Mixing them causes off-by-one
   or infinite loops.

## Applications

- Dictionary/database lookups, `Arrays.binarySearch`, `Collections.binarySearch`, git bisect,
  finding boundaries (lower/upper bound), and "binary search on the answer" for optimization problems.
