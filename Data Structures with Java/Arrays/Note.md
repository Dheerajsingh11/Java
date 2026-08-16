# Arrays

## What it is

An **array** is a fixed-size, contiguous block of memory holding elements of one type. Element `i`
lives at `base_address + i × element_size`.

## Why arrays exist — the one idea that matters

That address formula is the entire reason arrays are worth learning. Because the location of any
element is *computed arithmetically* rather than *searched for*, access is **O(1)** — reaching
element 5 costs exactly as much as reaching element 5,000,000.

Everything good and bad about arrays follows from contiguity:

| Contiguity gives you | Contiguity costs you |
|---|---|
| O(1) index access (address arithmetic) | Fixed size — growing means reallocate + copy |
| **Cache locality** — neighbours load together | O(n) insert/delete in the middle (shifting) |
| Zero per-element memory overhead | Needs one unbroken block of free memory |

**Cache locality is underrated.** CPUs fetch memory in ~64-byte lines, so scanning an array pulls in
several elements per fetch. A linked list of the same data can be 5–10× slower to traverse despite
identical O(n) complexity. Big-O hides this; benchmarks don't.

## When to use an array

- You need **fast access by position** — this is the decisive reason.
- The size is known, or changes rarely.
- You will **iterate a lot** (cache locality wins).
- You want minimal memory overhead.

## When NOT to use an array

- **Frequent insert/delete in the middle or front** → O(n) shifting per operation. Use a
  `LinkedList` / `ArrayDeque`.
- **Size changes constantly and unpredictably** → use `ArrayList` (which is a growable array and
  amortizes the copying).
- **Lookup by key rather than position** → use a `HashMap`.
- **Always needing the smallest/largest** → use a heap (`PriorityQueue`).

## Where arrays are used in practice

Backing store for `ArrayList`, `String`, heaps, hash tables, and ring buffers; matrices and image
pixel buffers; DP tables; lookup tables; I/O buffers. Essentially every other data structure is
built on one.

---

## The five array patterns worth memorizing

These solve a large fraction of all array problems. Learn the *pattern*, not the individual problem.

### 1. Two pointers
Two indices moving toward each other or in the same direction.
**Why it works:** sortedness or symmetry means one pointer's movement rules out a whole region, so
each element is visited once instead of compared against all others — O(n²) → O(n).
**Use when:** reversing, palindromes, pair-sums in sorted data, partitioning, trapping rain water.
**Files:** `reverseArray`, `trappingRainWaterEfficient`, `dutchNationalFlag`, `remDupEfficient`.

### 2. Sliding window
A contiguous range whose ends advance, maintaining a running aggregate.
**Why it works:** consecutive windows overlap in k−1 elements. Recomputing that overlap is waste;
adding the entering element and subtracting the leaving one is O(1) per step.
**Use when:** "best/longest/shortest contiguous subarray" questions.
**Caution:** variable-size windows need the aggregate to be **monotonic** — which is why sliding
window works for all-positive sums but *fails* with negatives (use prefix sums + hashing instead).
**File:** `slidingWindowMaxSum`.

### 3. Prefix sums
Precompute cumulative totals so any range sum is a subtraction.
**Why it works:** `sum(i..j) = P(j+1) − P(i)`; the shared prefix cancels.
**Use when:** many range queries over **static** data. If the data changes, use a Fenwick or segment
tree instead (`Trees/`).
**File:** `prefixSumRangeQuery`.

### 4. In-place partitioning
Rearrange around a predicate using a write pointer.
**Why it works:** the write pointer never overtakes the read pointer, so overwriting is always safe —
giving O(1) space where the obvious solution allocates a second array.
**Use when:** move zeroes, segregate by sign, sort 0/1/2, quicksort's partition step.
**Files:** `zeroesToEndEfficient`, `dutchNationalFlag`.

### 5. Running state (one-pass accumulation)
Carry the answer-so-far instead of re-deriving it.
**Why it works:** a single remembered value can summarize everything to the left, collapsing a nested
scan into one sweep.
**Use when:** max/min, Kadane's maximum subarray, majority element.
**Files:** `largestElementEfficient`, `kadaneMaxSubarrayEfficient`, `mooreVotingMajority`.

---

## Complexity reference

| Operation | Cost | Why |
|---|---|---|
| Access / update by index | O(1) | address arithmetic |
| Search (unsorted) | O(n) | must inspect each |
| Search (sorted) | O(log n) | binary search |
| Insert / delete at end | O(1) | nothing shifts |
| Insert / delete at front or middle | O(n) | everything after shifts |

## Files in this folder

**Fundamentals:** `createArray`, `insertArray`, `delete`, `reverseArray`, `basicSearch`
**Tiered problems:** `largestElement{Naive,Efficient}`, `getSecLarg{Naive,Efficient}`,
`isSorted{Naive,Efficient}`, `remDup{Naive,Efficient}`, `zeroesToEnd{Naive,Efficient}`
**Rotation ladder (3 tiers):** `leftRotateone` (building block) → `leftRotateD1` O(n·d) →
`leftRotateD2` O(n) time/O(d) space → `leftRotateD3` O(n) time/O(1) space (reversal trick)
**Patterns:** `kadaneMaxSubarray{Naive,Medium,Efficient}`, `trappingRainWater{Naive,Medium,Efficient}`,
`slidingWindowMaxSum`, `prefixSumRangeQuery`, `dutchNationalFlag`, `mooreVotingMajority`

## Pitfalls

- `arr.length` is a **field** (no parentheses); `String.length()` is a method. Easy to confuse.
- Indices are `0 .. length-1`; Java bounds-checks and throws `ArrayIndexOutOfBoundsException`.
- Summing a large `int[]` can **overflow** — accumulate into a `long`.
- Arrays are objects: `arr2 = arr1` copies the *reference*, not the data. Use `Arrays.copyOf`.
- `Arrays.equals` compares contents; `==` compares references.

## Also in this folder

`matrixOperations` — spiral traversal, in-place 90° rotation (transpose + reverse), and set-zeroes using the first row/column as markers.
