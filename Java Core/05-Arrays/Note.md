# Arrays (Java language feature)

A Java **array** is a fixed-size, contiguous container of a single type, with O(1) index access.
(For the *algorithmic* treatment — Kadane, prefix sums, two pointers — see
`Data Structures with Java/Arrays/`; this folder is about the Java language mechanics.)

## Files

| File | Covers |
|------|--------|
| `ArrayBasics.java` | 1-D, 2-D, jagged arrays; the `java.util.Arrays` helper class |

## Key points

- **Fixed size**: set at creation (`new int[5]`); cannot grow — use `ArrayList` to grow.
- **Default values**: numeric `0`, `boolean false`, references `null`.
- **`length` is a field**, not a method (`a.length`, no parentheses). Contrast: `String.length()`.
- **2-D = array of arrays**: `int[][]` rows are themselves arrays, so rows may differ in length
  (**jagged**).
- Out-of-range indexing throws `ArrayIndexOutOfBoundsException`.

## `java.util.Arrays` cheat-sheet

| Method | Does |
|--------|------|
| `sort(a)` | in-place ascending (O(n log n)) |
| `binarySearch(a, x)` | index of x (array must be sorted) |
| `fill(a, v)` | set every element to v |
| `copyOf(a, n)` | copy resized to length n |
| `copyOfRange(a, i, j)` | copy of `[i, j)` |
| `equals(a, b)` | element-wise equality |
| `toString(a)` / `deepToString(a)` | printable form (deep for nested) |

## Applications

- The backing store for many structures (ArrayList, heaps, hash tables, ring buffers), matrices/
  grids, lookup tables, and DP tables.
