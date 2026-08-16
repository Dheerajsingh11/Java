# Stack (LIFO)

## What it is

A **stack** is a Last-In-First-Out container: you add and remove only at one end, the *top*. Like a
stack of plates — the last one placed is the first one taken.

## Why restricting yourself to one end is useful

A stack is deliberately *less* capable than a list. That restriction is the point:

1. **Every operation is O(1).** Only one end moves, so there is no shifting or searching.
2. **It matches how nested things unwind.** Anything opened must be closed in reverse order —
   brackets, function calls, HTML tags, undo history. LIFO *is* that ordering.
3. **It remembers "where I was".** Push state before descending, pop to resume. This is exactly what
   the CPU's call stack does for function calls and what iterative DFS does for graph traversal.

> If a problem involves **matching**, **nesting**, or **backtracking to the most recent thing**, a
> stack is almost certainly the right tool.

## Core operations — all O(1)

| Operation | Meaning |
|---|---|
| `push(x)` | add on top |
| `pop()` | remove and return the top |
| `peek()` | look at the top without removing |
| `isEmpty()` | is it empty? |

Two failure modes: **overflow** (push onto a full fixed-size stack) and **underflow** (pop/peek an
empty one).

## Array vs linked implementation

| | `ArrayStack` | `LinkedStack` |
|---|---|---|
| Storage | one contiguous array + `top` index | one node per element |
| Capacity | **fixed** — can overflow | unbounded |
| Memory/element | value only | value + reference + object header |
| Cache locality | **excellent** | poor |

Prefer the array form unless the size is genuinely unpredictable. In production use
`java.util.ArrayDeque` — it is a growable array-backed deque. **Avoid the legacy `java.util.Stack`:**
it is synchronized (slower) and extends `Vector`, which leaks list operations like `get(i)` that
violate stack semantics.

## When to use a stack

- Matching / balancing (brackets, tags, quotes).
- Undo–redo, browser back button.
- Expression evaluation and infix→postfix conversion.
- Iterative DFS (replacing recursion).
- Any "most recent unmatched X" question — see the monotonic stack below.

## When NOT to use a stack

- You need the **oldest** item first → that is a **queue** (FIFO).
- You need access by index or search → use a list.
- You need the **smallest/largest** rather than the most recent → use a heap.

## The monotonic stack — the pattern worth mastering

Several problems here share one trick: keep a stack whose values stay in increasing or decreasing
order by **popping everything that can no longer be an answer**.

**Why it works:** if element `b` comes after `a` and `b ≥ a`, then `a` can never be the "next greater"
for anything further along — `b` blocks it forever. So `a` is useless and can be discarded
permanently. Each element is pushed once and popped at most once, giving **amortized O(1)** per step
and O(n) overall — turning the obvious O(n²) double loop into a single pass.

Recognize it when a problem asks for the **nearest greater/smaller element** in some direction:
next greater element, stock span, daily temperatures, largest rectangle in a histogram.

Files: `nextGreaterElement{Naive,Efficient}`, `stockSpan{Naive,Efficient}`.

## The min-stack: a 3-tier lesson in space optimization

"Return the minimum in O(1) at any time" is solved three ways here, each improving on the last:

| Tier | Idea | Extra space |
|---|---|---|
| `minStackNaive` | store `(value, minSoFar)` with **every** element | O(n), always |
| `minStackMedium` | a **second stack** holding only minimums, pushed when the min changes | O(n) worst, usually far less |
| `minStackEfficient` | **encode** the previous min into the pushed value (`2x − min`) | **O(1)** |

The progression shows a general principle: *store only what actually changes* (Medium), then *encode
information into data you are already storing* (Efficient). The Efficient version's cost is
readability and overflow risk — in real code the Medium two-stack version is usually the right call.

## Complexity

| Operation | Cost |
|---|---|
| push / pop / peek / isEmpty | O(1) |
| search | O(n) — and a sign you want a different structure |
| space | O(n) |

## Files in this folder

**Implementations:** `ArrayStack` (fixed array), `LinkedStack` (unbounded)
**Problems:** `balancedParentheses{Naive,Efficient}`, `nextGreaterElement{Naive,Efficient}`,
`stockSpan{Naive,Efficient}`, `minStack{Naive,Medium,Efficient}`

## Where stacks are used

The **call stack** itself (every function call pushes a frame — and recursion depth limits come from
this); compilers and parsers checking balanced syntax; expression evaluation in calculators and
virtual machines (the JVM is a stack machine); undo/redo in editors; browser history;
iterative DFS and backtracking; and the "unwinding" phase of exception handling.

## Also in this folder

`largestRectangleHistogram` — the hardest of the monotonic-stack problems.
