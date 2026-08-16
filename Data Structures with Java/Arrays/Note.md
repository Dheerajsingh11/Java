# Arrays

An **array** is a fixed-size, contiguous block of elements with O(1) index access. It is the most
fundamental data structure and the substrate for many others (heaps, hash tables, matrices).

## Key properties

| Operation | Cost | Why |
|-----------|------|-----|
| access by index | O(1) | address = base + i × elementSize |
| update by index | O(1) | direct write |
| search (unsorted) | O(n) | must scan |
| insert/delete (middle) | O(n) | shift elements |
| resize | O(n) | allocate + copy (Java arrays are fixed; use ArrayList to grow) |

## Fundamentals (existing files)

Create/insert/delete, reverse, largest/second-largest, isSorted, remove duplicates, move zeroes,
and the four left-rotation variants (`leftRotateone`, `leftRotateD1/D2/D3`).

## Patterns & problems added here

| File(s) | Pattern | Idea |
|---------|---------|------|
| `kadaneMaxSubarray{Naive,Medium,Efficient}` | running best | max contiguous sum in O(n) |
| `slidingWindowMaxSum` | sliding window | fixed-size window sum in O(n) |
| `prefixSumRangeQuery` | prefix sums | O(1) range-sum after O(n) precompute |
| `dutchNationalFlag` | three pointers | sort 0/1/2 in one pass |
| `mooreVotingMajority` | vote cancellation | majority element in O(1) space |
| `trappingRainWater{Naive,Medium,Efficient}` | two pointers | water between bars |

## The core array patterns (learn these, reuse everywhere)

1. **Two pointers** — opposite ends moving inward (rain water, pair sums, reversing).
2. **Sliding window** — fixed or variable contiguous ranges (subarray sums, longest-substring).
3. **Prefix sums** — turn repeated range aggregates into O(1) differences.
4. **In-place partitioning** — rearrange with O(1) space (Dutch flag, move zeroes, quicksort).
5. **Running state** — carry the best/needed value in one pass (Kadane, Moore's voting).

## Pitfalls

- Off-by-one on bounds; `arr.length` is a field, not a method.
- `ArrayIndexOutOfBoundsException` on empty arrays — check `length` first.
- Integer overflow when summing large arrays — use `long` if values are big.

## Applications

- Buffers, matrices/images, lookup tables, dynamic-programming tables, and as the backing store for
  ArrayList, heaps, hash tables, and ring buffers.
