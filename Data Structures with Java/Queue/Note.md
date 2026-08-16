# Queue (FIFO) & Deque

A **queue** is First-In-First-Out: elements leave in the order they arrived (like a checkout line).
A **deque** ("deck", double-ended queue) allows add/remove at *both* ends.

## Core operations (all O(1))

| Queue | Meaning |
|-------|---------|
| `enqueue(x)` | add at the rear |
| `dequeue()` | remove and return the front |
| `peek()` | look at the front |

## Implementations here

| File | Notes |
|------|-------|
| `ArrayQueue.java` | **circular** array (modulo wraparound) — reuses freed front slots |
| `LinkedQueue.java` | linked list with front + rear pointers; grows unbounded |
| `Deque.java` | circular array supporting all four end operations |
| `queueUsingStacks.java` | FIFO from two LIFO stacks (naive vs amortized-O(1)) |

### Why circular?
A plain array queue advances `front` and `rear` rightward, leaving a dead zone of freed slots at the
start. Wrapping the rear index with `% capacity` reuses that space, keeping enqueue/dequeue true
O(1) without shifting elements.

## Variants

| Variant | Rule | Where |
|---------|------|-------|
| Simple queue | FIFO | `ArrayQueue` / `LinkedQueue` |
| Circular queue | FIFO with wraparound buffer | `ArrayQueue` (is circular) |
| Deque | add/remove both ends | `Deque.java` |
| Priority queue | smallest/largest leaves first | see `Heap/` + `Java Core/09-Collections/PriorityQueueDemo.java` |

## In production
Use `java.util.ArrayDeque` for both stack and queue (fast, auto-resizing), and
`java.util.PriorityQueue` for a heap-backed priority queue.

## Applications

- **BFS** on graphs/trees (queue of frontier nodes), CPU/task scheduling, print/IO buffers,
  producer–consumer pipelines, sliding-window maximum (monotonic deque), and rate limiting.
