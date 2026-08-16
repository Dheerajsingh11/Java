# Linked List

A **linked list** stores elements in nodes, each pointing to the next (and, in a doubly linked list,
the previous). Unlike an array, elements are NOT contiguous — you trade O(1) index access for O(1)
insertion/deletion at a known position.

## Array vs linked list

| | Array / ArrayList | Linked list |
|--|-------------------|-------------|
| access by index | O(1) | O(n) |
| insert/delete at ends | O(n) front / O(1) end* | O(1) |
| insert/delete in middle | O(n) shift | O(1) once positioned |
| memory | contiguous, cache-friendly | scattered, extra pointers |

`*` amortized for ArrayList append.

## Variants

| Type | Each node has | File |
|------|---------------|------|
| Singly | value, next | existing `linkedList.java`, `traverseLinkedList.java`, `searchList.java`, … |
| Doubly | value, prev, next | `DoublyLinkedList.java` |
| Circular | last.next → head | `CircularLinkedList.java` |

## Classic problems (added here)

| File | Technique |
|------|-----------|
| `reverseLinkedList.java` | iterative pointer flip / recursion |
| `detectCycle.java` | Floyd's tortoise & hare + cycle start |
| `middleElement.java` | slow/fast pointers |
| `mergeTwoSorted.java` | dummy head + splice |
| `removeNthFromEnd.java` | two pointers n apart, one pass |
| `palindromeLinkedList.java` | reverse half, compare |

## Patterns to internalize

1. **Dummy head node** — removes special-casing for operations that may change the head.
2. **Two pointers** — slow/fast for middle & cycle detection; fixed-gap for nth-from-end.
3. **Pointer reversal** — carry a `prev` pointer to flip links in place.
4. Always guard against **null** (`node`, `node.next`) before dereferencing.

## Applications

- Implementing stacks/queues/deques, adjacency lists for graphs, LRU caches (doubly linked list +
  hash map), music/photo carousels (circular), and undo histories.
