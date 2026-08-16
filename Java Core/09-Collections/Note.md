# Java Collections Framework

## What it is

A set of ready-made data structures for storing and manipulating groups of objects — the practical,
library-backed counterpart to the hand-written structures in `Data Structures with Java/`.

## Why it exists

Before it, every project reinvented lists and maps with incompatible APIs. The framework provides one
consistent design: **interfaces describe *what*, implementations decide *how*.**

That separation is the single most useful thing about it:

```java
List<String> names = new ArrayList<>();   // program to the INTERFACE
```

Swap `ArrayList` for `LinkedList` and nothing else changes. Your code depends on "an ordered
sequence", not on how it is stored — so you can change your mind about performance later.

## The shape of the framework

```
Collection
├── List   — ordered, indexed, duplicates allowed
├── Set    — no duplicates
└── Queue / Deque — ends-oriented (FIFO / both ends)

Map — key → value  (NOT a Collection; it holds pairs, not elements)
```

## Choosing an implementation — the decisions that matter

### List: ArrayList vs LinkedList

| | `ArrayList` | `LinkedList` |
|---|---|---|
| Backed by | growable array | doubly linked nodes |
| `get(i)` | **O(1)** | O(n) |
| add/remove at ends | O(1) amortized / O(n) front | **O(1)** both |
| Memory & cache | compact, **cache-friendly** | node overhead, scattered |

**Use `ArrayList` by default.** It wins on the operations programs actually perform most (iteration
and indexed access), and its cache locality often makes it faster even where big-O says otherwise.
Reach for `LinkedList` only when you genuinely add/remove at the ends constantly — and even then,
**`ArrayDeque` is usually the better choice**.

> Trap: `list.get(i)` inside a loop over a `LinkedList` is **O(n²)**. Use an iterator or for-each.

**Amortized O(1):** `ArrayList` doubles its array when full — that one resize is O(n), but spread
across n appends it averages out to constant time per add.

### Map & Set: Hash vs Tree vs Linked

| | `HashMap`/`HashSet` | `LinkedHashMap`/`Set` | `TreeMap`/`TreeSet` |
|---|---|---|---|
| Lookup | **O(1)** avg | O(1) avg | O(log n) |
| Order | **none** | insertion order | **sorted** |
| Extra powers | — | predictable iteration | `floor`, `ceiling`, `headMap`, `subMap` |

**Decide by what you need beyond lookup:**
- Just key→value, order irrelevant → **HashMap** (fastest).
- Need iteration order to be stable/predictable → **LinkedHashMap** (also the basis of an LRU cache).
- Need **sorted iteration, nearest-value, or range queries** → **TreeMap**. This is not a small
  difference: a hash structure *cannot answer these at all*. "Largest key ≤ x" requires ordering.

### Queue / Stack: use ArrayDeque

`ArrayDeque` serves as **both** stack and queue with O(1) operations at each end.

**Avoid the legacy `java.util.Stack`:** it is synchronized (slower) and extends `Vector`, which
exposes `get(i)`, `insertElementAt`, etc. — operations that violate stack semantics and let callers
corrupt the abstraction.

### PriorityQueue
A binary heap: `peek` is O(1), `offer`/`poll` are O(log n). Use it when you repeatedly need the
smallest/largest element. **Its iteration order is heap order, not sorted order** — only repeated
`poll()` yields sorted output. (See `Data Structures with Java/Heap/`.)

## Complexity cheat-sheet

| Operation | ArrayList | LinkedList | HashMap/Set | TreeMap/Set | ArrayDeque | PriorityQueue |
|---|---|---|---|---|---|---|
| add (end) | O(1)* | O(1) | O(1)* | O(log n) | O(1)* | O(log n) |
| get / lookup | O(1) | O(n) | O(1)* | O(log n) | — | O(1) peek |
| remove | O(n) | O(1) at ends | O(1)* | O(log n) | O(1) ends | O(log n) |
| ordered? | insertion | insertion | **no** | **sorted** | insertion | head only |

`*` amortized / average.

## Comparable vs Comparator — one natural order, many custom ones

| | `Comparable` | `Comparator` |
|---|---|---|
| Lives | **inside** the class (`compareTo`) | **outside**, as a separate object |
| How many | one per class | as many as you like |
| Use for | the single obvious order | alternative / multi-key orders |

Use `Comparable` for the one order everyone would expect; `Comparator` for everything else — it keeps
sorting policy out of the domain class. Modern form:

```java
Comparator.comparingInt(Player::getScore).reversed().thenComparing(Player::getName)
```

**Always use `Integer.compare(a, b)`, never `a - b`** — subtraction overflows for extreme values and
silently produces the wrong ordering.

## Two things that bite everyone

**1. `remove(int)` vs `remove(Object)`**
On a `List<Integer>` these overloads collide: `list.remove(2)` removes the element at **index 2**,
not the value 2. Use `list.remove(Integer.valueOf(2))` for the value.

**2. `ConcurrentModificationException`**
Modifying a collection while iterating it throws. Collections keep a `modCount`; the iterator caches
it and checks on every `next()`. A direct `remove` bumps the count without the iterator knowing, so it
**fails fast** rather than silently skipping elements. Fix with `iterator.remove()` or `removeIf`.

## Keys must have correct `hashCode`/`equals`

`HashMap` and `HashSet` locate entries by hash. If two equal objects produce different hash codes,
lookups fail silently — the entry is "lost" in another bucket. Rules:

- Override `hashCode` **whenever** you override `equals`.
- Equal objects **must** have equal hash codes.
- **Never mutate a key** after inserting it — its hash changes and it becomes unreachable.

Prefer immutable keys (`String`, `Integer`, `record`). `TreeMap`/`TreeSet` instead need `Comparable`
or a `Comparator`, and an inconsistent comparator can make them silently lose elements.

## When NOT to use collections

- **Primitives in tight loops** — `List<Integer>` boxes every value (an object per element). Use
  `int[]` for large numeric data.
- **Fixed-size data** — a plain array is smaller and faster.
- **Concurrency** — these classes are *not* thread-safe. Use `ConcurrentHashMap`,
  `CopyOnWriteArrayList`, or `BlockingQueue`; the legacy synchronized wrappers lock the whole
  structure and scale poorly.

## Files in this folder

`ArrayListDemo` · `LinkedListDemo` · `HashMapDemo` (incl. the `merge`/`getOrDefault` frequency idiom)
· `HashSetDemo` (dedup, set algebra) · `TreeMapTreeSetDemo` (navigation & range queries) ·
`StackQueueDeque` · `PriorityQueueDemo` (incl. top-k with a size-k heap) · `IteratorDemo` (safe
removal, why CME exists) · `ComparableComparatorDemo`

## Where the framework is used

Everywhere in real Java: request/response handling, caching (`LinkedHashMap` for LRU), graph
adjacency lists (`Map<V, List<V>>`), BFS queues, Dijkstra's priority queue, frequency counting,
deduplication, leaderboards and range queries (`TreeMap`), and configuration lookup.
