# Queue (FIFO) & Deque

## What it is

A **queue** is First-In-First-Out: elements leave in the order they arrived, like a checkout line.
A **deque** ("deck", double-ended queue) allows adding and removing at **both** ends.

## Why FIFO matters

Stack vs queue is not a style choice — the ordering changes what the structure *means*:

| | Stack (LIFO) | Queue (FIFO) |
|---|---|---|
| Serves | most recent first | oldest first |
| Models | nesting, backtracking | **fairness, arrival order, waiting** |
| Graph traversal | DFS — go deep | **BFS — expand in rings** |

**The most important consequence:** because BFS explores in order of distance, the *first* time it
reaches a vertex is necessarily via the **shortest path** (in edge count). That property — free
shortest paths on unweighted graphs — comes directly from FIFO, and it is the single biggest reason
queues matter in algorithms.

## Core operations — all O(1)

| Operation | Meaning |
|---|---|
| `enqueue(x)` / `offer` | add at the rear |
| `dequeue()` / `poll` | remove and return the front |
| `peek()` | inspect the front |

## Why "circular" — the key implementation insight

A naive array queue advances `front` and `rear` rightward. The slots freed at the front become
**permanently unusable dead space**, so the queue "fills up" while half-empty. Two fixes:

- Shift everything left on each dequeue → **O(n)** per operation. Unacceptable.
- **Wrap the indices with modulo**: `rear = (front + size) % capacity`. Freed slots at the front get
  reused, and both operations stay **O(1)**.

That wraparound is the whole idea behind a **circular buffer** — and it is why `ArrayQueue.java`
here is circular by construction. The same structure underpins ring buffers in audio, networking,
and logging.

## Implementation trade-offs

| | `ArrayQueue` (circular) | `LinkedQueue` |
|---|---|---|
| Capacity | fixed — can overflow | unbounded |
| Memory | compact, contiguous | node overhead per element |
| Cache locality | **excellent** | poor |
| Gotcha | must track `size` (or waste a slot) to tell full from empty | must reset `rear` to null when it empties |

`LinkedQueue` keeps **both** `front` and `rear` pointers — without a tail pointer, every enqueue
would walk the whole list, making it O(n).

## Building a queue from two stacks

`queueUsingStacks.java` shows a classic idea: reversing a LIFO into another LIFO produces FIFO. The
interesting part is **where you pay**:

- **Costly enqueue** (naive): rebuild on every insert → O(n) enqueue, O(1) dequeue.
- **Costly dequeue** (efficient): only refill the output stack when it empties → **amortized O(1)**.

The amortized argument: each element moves from `in` to `out` **exactly once** in its lifetime. A
single dequeue may cost O(n), but n operations cost O(n) total. This is the same reasoning that makes
`ArrayList` appends O(1) amortized despite occasional O(n) resizes — a genuinely important concept.

## When to use which

| Need | Use |
|---|---|
| Fair, arrival-order processing | **queue** |
| BFS / shortest path on unweighted graphs | **queue** |
| Most-recent-first, backtracking | **stack** |
| Add/remove at both ends | **deque** |
| Sliding-window maximum | **monotonic deque** |
| Serve by priority, not arrival | **PriorityQueue** (heap — see `Heap/`) |

## When NOT to use a queue

- You need indexing or search → use a list.
- You need priority ordering → a plain queue is strictly arrival-ordered; use a heap.
- You need the newest item → that is a stack.

## Deque: one structure, three roles

A deque is a strict superset of both stack and queue — push/pop one end for LIFO, push one end and
pop the other for FIFO. That is exactly why `java.util.ArrayDeque` is the recommended implementation
of **both** in modern Java: it is faster than `LinkedList` (contiguous, no node allocation) and
faster than the legacy `Stack` (no synchronization).

Its third role is the **monotonic deque**, which solves sliding-window maximum in O(n) by discarding
elements that can never again be the window's max — the deque cousin of the monotonic stack.

## Complexity

| Operation | Circular array | Linked | ArrayDeque |
|---|---|---|---|
| enqueue / dequeue / peek | O(1) | O(1) | O(1) amortized |
| space | O(capacity) | O(n) | O(n) |

## Files in this folder

| File | Covers |
|---|---|
| `ArrayQueue` | circular array with modulo wraparound |
| `LinkedQueue` | linked nodes with front **and** rear pointers |
| `Deque` | circular array supporting all four end operations |
| `queueUsingStacks` | FIFO from two LIFOs; costly-enqueue vs amortized-O(1) dequeue |

## Java note

`offer`/`poll`/`peek` return `null` or `false` on failure; `add`/`remove`/`element` **throw**. Pick
based on whether an empty queue is expected or exceptional. `ArrayDeque` forbids `null` elements,
because `null` is its "empty" signal.

## Where queues are used

**BFS** on graphs and trees; CPU, disk, and print scheduling; message brokers (Kafka, RabbitMQ);
producer–consumer pipelines and thread pools (`BlockingQueue`); network packet buffers; rate limiting;
keyboard and event buffers; and streaming/ring buffers in audio and video.

## Also in this folder

`slidingWindowMaximum` — the monotonic **deque**, and why this problem needs both ends.
