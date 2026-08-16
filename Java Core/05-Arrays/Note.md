# Arrays (the Java language feature)

## What this covers

The **mechanics** of Java arrays — declaration, memory, multi-dimensional forms, and the `Arrays`
utility class.

> For the *algorithmic* treatment — Kadane's, prefix sums, two pointers, sliding window — see
> `Data Structures with Java/Arrays/`. This folder is about the language; that one is about technique.

## What an array is in Java

A fixed-size, contiguous block of same-typed elements, allocated on the **heap**. An array variable
holds a **reference** to that block, not the block itself.

That single fact explains several behaviours:

```java
int[] a = {1, 2, 3};
int[] b = a;        // copies the REFERENCE - both point at the same array
b[0] = 99;          // a[0] is now 99 too
int[] c = a.clone();  // an actual copy
```

Arrays are objects: they have a `length` field, they can be `null`, and `==` compares references.

## Declaration and the three creation forms

```java
int arr[]  = new int[5];       // C-style - brackets on the variable
int[] arr1 = new int[5];       // preferred - "int[]" reads as the type
int[] arr2 = {10, 20, 30};     // literal - allocates and fills, size inferred
```

**Prefer `int[] name`.** It groups the type together, which matters when declaring several variables:
`int[] a, b;` makes both arrays, but `int a[], b;` makes an array and a plain int — a genuine trap.

## Default values are guaranteed

Java zero-fills on allocation: `0` for numeric types, `false` for `boolean`, `null` for references.
Unlike C, there is **no uninitialized garbage**. That is why `new int[5]` is immediately usable and
why frequency-count arrays need no explicit reset loop.

## Fixed size — the defining constraint

An array's length is set at creation and **can never change**. To "grow" one you allocate a bigger
array and copy — which is exactly what `ArrayList` automates (doubling capacity, giving amortized
O(1) appends).

**Use an array when** the size is known and stable, you want minimal memory overhead, or you are
storing primitives in bulk.
**Use `ArrayList` when** the size changes, or you want the Collections API.

**The primitive argument matters:** `List<Integer>` boxes every element into an object — roughly 16
bytes plus a pointer instead of 4. For a million numbers that is a large difference in memory *and*
speed. For numeric-heavy work, `int[]` beats `List<Integer>` decisively.

## 2-D arrays are arrays of arrays

Java has no true multi-dimensional array. `int[][]` is an array whose elements are themselves array
references. Consequences:

```java
int[][] grid = new int[3][4];        // rectangular: 3 rows, each of length 4
int[][] jagged = new int[3][];       // rows unallocated
jagged[0] = new int[1];              // rows may differ in length
jagged[1] = new int[5];
```

**Jagged arrays** are possible precisely because rows are independent objects. It also means rows are
scattered in memory, so 2-D array traversal is less cache-friendly than 1-D — and why **iterating
row-by-row is faster than column-by-column** (row elements are contiguous; column elements are not).

`grid.length` is the row count; `grid[0].length` is that row's length.

## The `Arrays` utility class

| Method | Does | Cost |
|---|---|---|
| `sort(a)` | in-place ascending | O(n log n) |
| `binarySearch(a, x)` | index of x — **array must be sorted** | O(log n) |
| `fill(a, v)` | set every element | O(n) |
| `copyOf(a, n)` | copy, resized | O(n) |
| `copyOfRange(a, i, j)` | copy of `[i, j)` | O(n) |
| `equals(a, b)` | element-wise comparison | O(n) |
| `toString(a)` | printable form | O(n) |
| `deepToString(a)` | printable form for **nested** arrays | O(n) |
| `stream(a)` | bridge to the Streams API | — |

**Two traps:**
- `System.out.println(arr)` prints something like `[I@1b6d3586` — the type and hash, not the contents.
  Use `Arrays.toString(arr)`, and `Arrays.deepToString` for 2-D.
- `arr1.equals(arr2)` compares **references**. Use `Arrays.equals` for contents.

`Arrays.sort` uses dual-pivot quicksort for primitives (stability is meaningless for identical ints)
and **TimSort** for objects (stable, adaptive) — see `Sorting/Note.md`.

## `length` vs `length()` vs `size()`

| Type | Spelling |
|---|---|
| Array | `arr.length` — a **field** |
| String | `str.length()` — a **method** |
| Collection | `list.size()` |

Three spellings of the same idea. A frequent small annoyance; worth memorizing once.

## When NOT to use a plain array

- **Size changes** → `ArrayList`.
- **Frequent insert/delete in the middle** → O(n) shifting; use a `LinkedList`/`ArrayDeque`.
- **Lookup by key** → `HashMap`.
- **You want the Collections API** (streams, `contains`, sorting comparators) → a `List`.
- **Generics** — you cannot create `new T[n]`; type erasure forbids it. Use `List<T>`.

## File in this folder

`ArrayBasics.java` — 1-D, 2-D and jagged arrays, default values, iteration, and the `Arrays` utility
methods.

## Pitfalls

- `ArrayIndexOutOfBoundsException` — valid indices are `0 .. length-1`. Java bounds-checks every
  access (a safety guarantee C does not give you).
- `NullPointerException` on an unallocated jagged row.
- Assuming `binarySearch` works on unsorted data — it returns nonsense, not an error.
- Overflow when summing a large `int[]` — accumulate into a `long`.
- Copying with `=` instead of `clone()`/`Arrays.copyOf`, then being surprised both change.

## Where this leads

Arrays back almost everything else: `ArrayList`, `String`, heaps (`Heap/MinHeap`), hash tables,
circular queues (`Queue/ArrayQueue`), DP tables (`DynamicProgramming/`), and adjacency matrices
(`Graphs/`).
