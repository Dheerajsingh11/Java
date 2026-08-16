# Repository Conventions

This document defines the structure, naming, and commenting standards every file in this
repository follows. It exists so that the code reads like one consistent teaching resource
rather than a pile of one-off scripts. Read this before adding or editing any file.

---

## 1. Three optimization tiers

Most algorithm problems are implemented **three times**, each in its own file, so the reader can
see *how* a solution improves step by step:

| Tier | Suffix | Meaning |
|------|--------|---------|
| Non-optimized | `Naive` | The most direct, brute-force idea. Easiest to understand, worst complexity. |
| Medium-optimized | `Medium` | A meaningful improvement (better data structure, early exit, one pass instead of many). |
| Highly-optimized | `Efficient` | The best practical approach — optimal or near-optimal time/space. |

Example for "second largest element in an array":

```
secondLargestNaive.java      // sort or double loop
secondLargestMedium.java     // find max, then max excluding it (two passes)
secondLargestEfficient.java  // single pass tracking largest + second largest
```

Not every problem has three genuinely distinct tiers. When only two make sense, ship two
(`Naive` + `Efficient`). When a topic is a single utility (e.g. `createArray`), no suffix is used.
**Never invent a fake middle tier just to hit three** — the tiers must be genuinely different ideas.

For **Dynamic Programming**, the tiers map to the standard progression:
`Naive` = brute-force recursion, `Medium` = memoization (top-down),
`Efficient` = tabulation and/or space-optimized (bottom-up).

---

## 2. File header block

Every `.java` file starts with this comment block:

```java
// Problem  : <one-line statement of what this solves>
// Approach : <Naive|Medium|Efficient> - <short description of the idea>
// Intuition: <why this works / the key insight>
// Time     : O(...)   Space: O(...)
// Trade-off: <why this tier, and why one might stop here or move to the next>
```

Keep it to these five lines where possible. The `Trade-off` line is what ties the three tiers
together — it should point forward ("the sort here costs O(n log n); the Efficient version avoids
it") or backward ("this removes the extra array the Medium version allocated").

---

## 3. Inline comments

- Explain the **why**, not just the **what**. `i++ // increment i` is noise; `i++ // skip the
  duplicate we just matched` is useful.
- Keep the friendly, line-by-line teaching style already used in the stronger existing files
  (see `Data Structures with Java/Arrays/getSecLargEff.java` as the reference for tone).
- Comment the tricky lines heavily; leave the obvious ones clean.
- **Favour depth.** This is a teaching repo, so err on the side of MORE explanation:
  - For algorithms, **derive the complexity** (why O(n), why the loop runs k times, why O(1) space)
    rather than just stating it.
  - Call out **edge cases** explicitly in comments: empty input, single element, all-equal values,
    negatives, overflow, nulls, already-sorted, etc. — and what the code does about each.
  - Where a subtle line exists, add a short `// Why:` or `// Edge:` note.
  - A closing block comment ("dry run" / walkthrough on the sample input) is encouraged for the
    trickier algorithms.

---

## 4. Runnability

- Every file has a `public static void main(String[] args)` with a **small demo** and a comment
  showing the **expected output**, so the file can be run standalone:

```java
public static void main(String[] args) {
    int[] arr = { 1, 2, 3, 4, 5, 6 };
    System.out.println(secondLargest(arr)); // expected: 5
}
```

- Prefer hard-coded sample inputs over reading from stdin, so files run without interaction.

---

## 5. Naming

- **Class name = file name** (Java requires this for `public` classes).
- **Problem/algorithm files:** `camelCase` (e.g. `binarySearchIterative`, `kadaneMaxSubarray`).
- **Data-structure types:** `PascalCase` (e.g. `BinarySearchTree`, `MinHeap`, `DoublyLinkedList`).
- Tier suffix comes last: `sortArr012Naive`, `sortArr012Efficient`.
- No lowercase-suffix drift (`...naive` is wrong; use `...Naive`).

---

## 6. Per-topic theory note

Every topic folder contains a `Note.md` covering:
- What the topic/data structure is and its core idea
- Complexity table for the main operations
- When to use it / when **not** to use it
- Real-world applications

Use the existing `Data Structures with Java/Hashing/Note.md` as the style reference.

---

## 7. Housekeeping

- **No build artifacts in git.** `*.class` files are ignored via `.gitignore` and never committed.
- Compile from a topic folder with `javac *.java` and run with `java <ClassName>`.
- One concept per file; avoid dumping unrelated demos together.

---

## 8. Folder layout

```
Java Core/
  01-Basics/ 02-Operators/ 03-ControlFlow/ 04-Strings/ 05-Arrays/
  06-Methods/ 07-OOP/ 08-Exceptions/ 09-Collections/ 10-Generics/
  11-Functional/ 12-Advanced/          (each with a Note.md)

Data Structures with Java/
  Arrays/ Hashing/ Linked List/ Recursions/ Searching/ Sorting/
  Stack/ Queue/ Trees/ Heap/ Graphs/ DisjointSet/
  Greedy/ DynamicProgramming/ Backtracking/ BitManipulation/ Math/ Strings/
                                         (each with a Note.md)
```
