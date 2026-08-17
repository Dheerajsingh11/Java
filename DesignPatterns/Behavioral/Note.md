# Behavioural Patterns

Concerned with **how objects interact** — assigning responsibility and managing communication.

| Pattern | Use when | Avoid when |
|---|---|---|
| **Strategy** | An algorithm should be swappable | Two fixed cases — an `if` is clearer |
| **Observer** | Many parties react to one event | Only one listener, forever |
| **Command** | You need undo, queuing, logging, replay | None of those — just call the method |
| **State** | Behaviour changes with internal state, with transition rules | 2-3 states — an enum + switch shows the whole machine |
| **Chain of Responsibility** | One of several handlers should respond | Exactly one handler is always correct |
| **Template Method** | A fixed sequence with varying steps | Only one step varies — use Strategy |
| **Iterator** | Traverse without exposing structure | A plain list with one natural order |
| **Mediator** | N objects reference each other | Only 2-3 objects — direct calls are clearer |

## The confusable pairs

- **Strategy vs State** — identical in structure. Strategy: the **client** chooses, and the strategy
  never changes itself. State: the object **transitions itself** as events arrive. In
  `StatePattern.java` each method *returns the next state* — that self-transition is the tell.
- **Template Method vs Strategy** — Template uses **inheritance** to vary several steps of a fixed
  algorithm; Strategy uses **composition** to swap the whole algorithm at run time.
- **Mediator vs Observer** — Mediator **coordinates** and applies rules (two-way, it may filter,
  route or transform). Observer **broadcasts** with no logic (one-way, fire and forget).

## The pitfalls that bite in production

- **Observer → memory leaks.** The subject holds strong references to its listeners, so a listener
  that is never unsubscribed keeps its entire object graph alive. This is a leading cause of heap
  growth in long-running applications. Also isolate exceptions — one broken listener must not abort
  the notification loop for the rest.
- **Command → state-capture timing.** Each command must capture what it needs to reverse itself at
  the *right* moment: before mutating (replace-all), during execution (delete), or not at all
  (insert). Get this wrong and undo works on simple input and corrupts on real input.
- **Chain of Responsibility → silent drops.** A request can fall off the end unhandled. Always
  terminate the chain with a catch-all or make "unhandled" an explicit, visible outcome.
- **State → no single view.** The machine is spread across classes, so no one file shows the whole
  picture. Keep a transition table in the notes, as `StatePattern.java` does.
- **Template Method → the yo-yo problem.** Deep hierarchies mean jumping repeatedly between base and
  subclass to follow one operation. Mark the template method `final` so subclasses can change the
  steps but never the algorithm.
- **Iterator → filtering in the wrong method.** A filtering iterator must skip in `hasNext()`, not in
  `next()`, or `hasNext()` promises an element that `next()` then skips.

## Modern Java shortcuts

Several of these shrink dramatically with lambdas:

- A single-method **Strategy** is just a lambda or a `Function`.
- `java.util.Comparator` **is** Strategy; `Runnable` **is** Command (without `undo`).
- **Iterator** — implement `Iterable` and for-each works automatically. Hand-write one only for
  custom traversal orders (reversed, filtered, infinite).
- Note `java.util.Observer`/`Observable` were **deprecated in Java 9** — not thread-safe and they
  forced inheritance. Use your own listener interface, `PropertyChangeListener`, or a reactive library.

## Files

`StrategyPattern` · `ObserverPattern` · `CommandPattern` · `StatePattern` ·
`ChainOfResponsibilityPattern` · `TemplateMethodPattern` · `IteratorPattern` · `MediatorPattern`
