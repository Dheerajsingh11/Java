# Stack (LIFO)

A **stack** is a Last-In-First-Out container: the last element pushed is the first popped. Think of
a stack of plates — you add and remove only from the top.

## Core operations (all O(1))

| Operation | Meaning |
|-----------|---------|
| `push(x)` | add x on top |
| `pop()` | remove and return the top |
| `peek()` / `top()` | look at the top without removing |
| `isEmpty()` | is the stack empty? |

Two failure edges: **overflow** (push to a full fixed-size stack) and **underflow** (pop/peek an
empty stack).

## Implementations here

| File | Notes |
|------|-------|
| `ArrayStack.java` | fixed-capacity array + `top` index; cache-friendly, can overflow |
| `LinkedStack.java` | singly linked list, push/pop at head; grows unbounded |

In real code, prefer `java.util.ArrayDeque` as a stack (see `Java Core/09-Collections/StackQueueDeque.java`).

## Classic problems

| Problem | Files | Idea |
|---------|-------|------|
| Balanced brackets | `balancedParenthesesNaive/Efficient` | push openers, match closers with the top |
| Next greater element | `nextGreaterElementNaive/Efficient` | **monotonic stack**, one pass |
| Stock span | `stockSpanNaive/Efficient` | monotonic stack of previous-higher indices |
| Min stack (O(1) getMin) | `minStackNaive/Medium/Efficient` | pair-per-element → aux stack → O(1)-space encoding |

## The monotonic-stack pattern

Several problems (next greater/smaller, stock span, daily temperatures, largest rectangle in a
histogram) share one trick: maintain a stack whose values are kept in increasing or decreasing
order by popping elements that can no longer be an answer. This turns O(n²) scans into a single
O(n) pass — each element is pushed and popped at most once.

## Applications

- Function call stack / recursion, expression evaluation & parsing (infix→postfix), undo/redo,
  browser back button, DFS (iterative), and balanced-syntax checking in compilers.
