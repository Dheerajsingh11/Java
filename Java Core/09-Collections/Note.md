# Java Collections Framework

The Collections Framework is a set of ready-made data structures (interfaces + implementations)
for storing and manipulating groups of objects. Learn the **interfaces** (what) separately from the
**implementations** (how), and program to the interface.

## The core interfaces

```
Collection
├── List   (ordered, indexed, duplicates allowed)
├── Set    (no duplicates)
└── Queue / Deque (ends-oriented: FIFO / both ends)

Map        (key -> value; not a Collection)
```

## Files & when to use each implementation

| File | Type | Best for | Key cost |
|------|------|----------|----------|
| `ArrayListDemo.java` | List | random access, iteration | O(n) mid insert/remove |
| `LinkedListDemo.java` | List + Deque | O(1) add/remove at ends | O(n) index access |
| `HashMapDemo.java` | Map | O(1) key lookup | unordered |
| `HashSetDemo.java` | Set | O(1) membership / dedup | unordered |
| `TreeMapTreeSetDemo.java` | Sorted Map/Set | order + range/nearest queries | O(log n) ops |
| `StackQueueDeque.java` | Deque | LIFO / FIFO (use ArrayDeque) | — |
| `PriorityQueueDemo.java` | Queue (heap) | repeatedly get min/max | not fully sorted |
| `IteratorDemo.java` | traversal | safe remove during iteration | — |
| `ComparableComparatorDemo.java` | ordering | custom / multi-key sorting | — |

## Complexity cheat-sheet

| Operation | ArrayList | LinkedList | HashMap/Set | TreeMap/Set | PriorityQueue |
|-----------|-----------|------------|-------------|-------------|---------------|
| add end | O(1)* | O(1) | O(1)* | O(log n) | O(log n) |
| get/lookup | O(1) | O(n) | O(1)* | O(log n) | O(1) peek |
| remove | O(n) | O(1) ends | O(1)* | O(log n) | O(log n) |
| ordered? | insertion | insertion | no | sorted | head only |

`*` = amortized / average.

## Choosing quickly

- Need an index and fast reads → **ArrayList**.
- Lots of add/remove at the ends (stack/queue) → **ArrayDeque** (or LinkedList).
- Key→value lookup, order irrelevant → **HashMap**; need sorted keys/ranges → **TreeMap**.
- Uniqueness / membership → **HashSet**; sorted → **TreeSet**.
- "Give me the smallest/largest next" repeatedly → **PriorityQueue**.

## Pitfalls

- `List<Integer>.remove(int)` removes by **index**; use `remove(Integer.valueOf(x))` for value.
- Modifying a collection inside a for-each throws `ConcurrentModificationException`; use
  `Iterator.remove()` or `removeIf`.
- HashMap/HashSet keys need correct `hashCode`/`equals`; TreeMap/TreeSet keys need `Comparable`
  or a `Comparator`.
- Prefer `ArrayDeque` over the legacy `Stack`/`Vector` classes.

## Applications

- Graphs (adjacency lists = `Map`/`List`), BFS (`Queue`), DFS (`Deque`), Dijkstra/A* (`PriorityQueue`),
  frequency counting and caching (`HashMap`), leaderboards and ranges (`TreeMap`).
