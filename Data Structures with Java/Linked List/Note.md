# Linked List

## What it is

A **linked list** stores elements in separate **nodes**, each holding a value and a reference to the
next node. The nodes can sit anywhere in memory; the `next` references are what make them a sequence.

## Why linked lists exist

An array's power comes from contiguity — and so do its limits. A linked list makes the opposite
trade: **give up O(1) index access to gain O(1) structural change.**

| | Array | Linked list |
|---|---|---|
| Access element k | **O(1)** — address arithmetic | O(k) — must walk the chain |
| Insert/delete at front | O(n) — shift everything | **O(1)** — relink one pointer |
| Insert/delete mid (position known) | O(n) — shift | **O(1)** — relink |
| Grow | reallocate + copy | **O(1)** — just allocate a node |
| Memory per element | value only | value + reference(s) + object header |
| Cache behaviour | **excellent** (contiguous) | poor (scattered) |

**Be honest about the cache cost.** A linked list traversal and an array traversal are both O(n), but
the array is often 5–10× faster in wall-clock time because each cache line fetch brings in several
elements. This is why modern practice favours `ArrayList`/`ArrayDeque` for most work, and why
"linked list for fast insertion" is weaker advice than textbooks imply — you still pay O(n) to *find*
the insertion point.

## When to use a linked list

- Constant-time insertion/removal at the **ends** → stacks, queues, deques.
- You already hold a **reference to the node** to remove (e.g. an LRU cache's hash map points
  straight at nodes, making eviction O(1)).
- Size varies wildly and you want no reallocation/copy pauses.
- You need to **splice** whole lists together in O(1).

## When NOT to use one

- **You index by position** → use an array/`ArrayList`. `list.get(i)` in a loop is an O(n²) trap.
- **You will iterate heavily** and care about speed → arrays win on cache locality.
- **You need binary search** → impossible in useful time; reaching the middle is already O(n).
- Memory is tight → the per-node overhead is significant for small values.

## Variants and why each exists

| Variant | Structure | Buys you | Costs |
|---|---|---|---|
| **Singly** | `value, next` | least memory | forward-only; deleting needs the *predecessor* |
| **Doubly** (`DoublyLinkedList`) | `value, prev, next` | backward traversal; **O(1) delete of a known node** | one extra reference per node |
| **Circular** (`CircularLinkedList`) | last → first | round-robin; one `tail` pointer gives O(1) access to *both* ends | traversal must stop on returning to start, not on `null` |

**Why doubly linked lists earn their extra pointer:** to delete a node in a singly linked list you
must know the node *before* it, which means an O(n) scan. With a `prev` pointer, deletion is O(1)
given only the node itself. That is exactly why an **LRU cache** uses a doubly linked list.

## The patterns worth internalizing

### 1. Dummy head node
Allocate a throwaway node before the real head, then return `dummy.next`.
**Why:** operations that might change the head (delete the first element, merge, insert at front)
otherwise need a special case. The dummy makes every position uniform.
**Files:** `mergeTwoSorted`, `removeNthFromEnd`.

### 2. Two pointers at different speeds (tortoise & hare)
`slow` moves 1 step, `fast` moves 2.
**Why it works:** when `fast` reaches the end, `slow` is exactly halfway — the middle found in **one
pass without knowing the length**. On a cyclic list the fast pointer inevitably laps the slow one, so
they meet — proving a cycle exists.
**Files:** `middleElement`, `detectCycle`, `palindromeLinkedList`.

### 3. Two pointers at a fixed gap
Advance `fast` n steps first, then move both together.
**Why:** when `fast` hits the end, `slow` sits exactly n from the end — the n-th-from-last element in
one pass instead of two (count, then walk).
**File:** `removeNthFromEnd`.

### 4. Pointer reversal
Carry a `prev` pointer and flip each `next` as you go.
**Why:** reverses in O(1) space — no new list, no stack.
**File:** `reverseLinkedList`.

## Floyd's cycle detection — why the second phase works

After `slow` and `fast` meet inside the loop, moving one pointer back to the head and advancing both
one step at a time makes them meet **exactly at the cycle's entry**. The reason: the distance from
the head to the entry equals the distance from the meeting point to the entry (mod cycle length). It
looks like magic; it is arithmetic. `detectCycle.java` implements both phases.

## Complexity summary

| Operation | Singly | Doubly |
|---|---|---|
| Insert/delete at head | O(1) | O(1) |
| Insert/delete at tail | O(n) (O(1) with a tail pointer) | O(1) |
| Delete a **given** node | O(n) (need predecessor) | **O(1)** |
| Search | O(n) | O(n) |
| Access by index | O(n) | O(n) |

## Files in this folder

**Basics:** `linkedList` (node model & memory), `traverseLinkedList`, `insertAtBegin`, `searchList`,
`recursivePrint`, `recursiveSearch`
**Variants:** `DoublyLinkedList`, `CircularLinkedList`
**Classic problems:** `reverseLinkedList`, `detectCycle`, `middleElement`, `mergeTwoSorted`,
`removeNthFromEnd`, `palindromeLinkedList`

> **Compile note:** several of these files each declare their own top-level `Node` class in the
> default package, so `javac *.java` on the whole folder collides. Compile and run them individually.

## Pitfalls

- **Null dereference** — always check `node` *and* `node.next` before following two links.
- **Losing the head** — traverse with a separate cursor; if you advance `head` itself, the list is gone.
- **Forgetting to reset `tail`** when a list becomes empty (see `LinkedQueue.dequeue`).
- Java is **pass-by-value**: reassigning a `head` parameter inside a method does not change the
  caller's variable — which is why these methods *return* the new head.

## Where linked lists are used

Stack/queue/deque implementations, adjacency lists for graphs, **LRU caches** (doubly linked list +
hash map), undo/redo histories, music and image carousels (circular), free-block lists in memory
allocators, and blockchain (each block references the previous).

## Also in this folder

`LRUCache` — HashMap + doubly linked list; the definitive reason a doubly linked list earns its extra pointer.
