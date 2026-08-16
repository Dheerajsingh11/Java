# Object-Oriented Programming (OOP)

## What it is

OOP models a program as **objects** — bundles of state (fields) and behaviour (methods) that
interact. Java is object-oriented to the core: essentially all code lives inside a class.

## Why OOP exists — the problem it solves

Before OOP, programs were data structures plus functions that operated on them, kept separate. As
programs grew, this failed in predictable ways: anyone could corrupt any data, "which functions are
allowed to touch this struct?" had no answer, and adding a new variant of something meant editing
every `switch` statement in the codebase.

OOP addresses each:

| Problem | OOP's answer |
|---|---|
| Anyone can corrupt data | **Encapsulation** — hide state, expose guarded operations |
| Duplicated code across similar types | **Inheritance** — share what is common |
| Adding a variant means editing every branch | **Polymorphism** — add a subclass, callers unchanged |
| Callers depend on internal details | **Abstraction** — publish *what*, hide *how* |

**Polymorphism is the biggest win.** It is what lets you add a new `Shape` without touching the loop
that draws shapes.

## The four pillars — what each is actually for

### Encapsulation (`Encapsulation.java`)
Make fields `private`; expose behaviour that enforces the rules.

**Why:** an object should be **impossible to put into an invalid state**. If `balance` is public,
every line in the codebase is a potential bug. If it is private with a validating `withdraw`, the
rule "balance never goes negative" is enforced in exactly one place — and stays enforced.

Secondary benefit: you can change the internal representation later without breaking callers.

### Inheritance (`Inheritance.java`)
`class Dog extends Animal` — reuse and extend.

**Why:** removes duplication for genuine **"is-a"** relationships.
**Caution:** inheritance is the most *overused* pillar. It couples the subclass to the parent's
implementation, and Java allows only one superclass. **Prefer composition ("has-a") unless the
"is-a" is real.** A `Car` is not an `Engine`; it *has* one.

### Polymorphism (`Polymorphism.java`)
One interface, many behaviours. Two distinct kinds:

| | Overloading | Overriding |
|---|---|---|
| Resolved | **compile time** by argument types | **run time** by the actual object |
| Also called | static / early binding | dynamic / late binding |
| Enables | convenient APIs (`println` for every type) | **extensibility** |

**How run-time dispatch works:** each object carries a hidden pointer to its class's method table, so
`shape.area()` looks up the *actual* object's implementation. This is why a `Shape[]` loop
automatically handles a `Triangle` class written years later.

**Three limits worth knowing:** fields are *not* polymorphic (resolved by reference type); `static`
methods are **hidden**, not overridden; `private` and `final` methods cannot be overridden.

### Abstraction (`Abstraction.java`)
An `abstract` class cannot be instantiated and may declare methods with no body.

**Why:** some concepts are too generic to exist concretely — there is no such thing as "a Payment",
only a card payment or a UPI payment. `abstract` makes that a **compile-time guarantee**, and forces
every subclass to supply the missing behaviour.

## Interface vs abstract class — the decision people get wrong

| | Interface | Abstract class |
|---|---|---|
| Multiple inheritance | **yes** — implement many | no — extend only one |
| Instance state (fields) | no (constants only) | **yes** |
| Constructor | no | yes |
| Models | a **capability** ("can be drawn") | an **is-a family** sharing implementation |

**Rule of thumb:** if unrelated classes share a *capability*, use an interface. If related classes
share *state and partial implementation*, use an abstract class. Interfaces are the more flexible
default — they decouple callers from implementations, which is what makes testing and swapping
implementations easy.

Since Java 8 interfaces can have `default` methods (so they can evolve without breaking implementers),
`static` methods, and `private` helpers. If two interfaces supply the same `default` method, the class
**must** override it — the "diamond problem", resolved explicitly with `InterfaceName.super.method()`.

## `static` — and why it is easy to misuse

`static` belongs to the **class**, not to any object: one shared copy, reachable without an instance.

**Legitimate uses:** constants (`static final`), counters, stateless utility methods (`Math.max`), and
`main` (which must run before any object exists).

**Why to be careful:** static mutable state is **global state**. It makes code hard to test (tests
leak into each other), hard to reason about, and unsafe under concurrency. Prefer instance state.

Key rules: a static method has no `this`, so it cannot touch instance members directly; instance
methods *can* read static members freely.

## `record` and `enum` — modern replacements for boilerplate

**`record`** (Java 16+) generates fields, constructor, accessors, `equals`, `hashCode`, and
`toString` from a one-line declaration, and makes the object **immutable**.
Use for transparent data carriers (DTOs, coordinates, results). Not for mutable or behaviour-heavy
types — records are final and cannot extend a class.

**`enum`** — a fixed set of named constants that are real objects, so they can carry fields and
methods. Use it wherever you would otherwise use magic ints or strings: the compiler then guarantees
only valid values exist, and `switch` can be checked for completeness.

## Nested classes — which of the four to use

| Kind | Use when |
|---|---|
| **static nested** | a helper type that needs no outer instance — the **safe default** |
| **inner** (non-static) | it genuinely needs the outer object's state |
| **local** | used inside a single method only |
| **anonymous** | a one-off implementation; mostly superseded by **lambdas** |

Inner classes hold a hidden reference to the outer instance, which can keep it alive longer than
expected (a memory-leak source). Prefer `static` nested unless you need the link.

## When NOT to use OOP-heavy design

- Simple scripts and pure computation — a couple of static methods beat a class hierarchy.
- Deep inheritance trees — brittle; favour composition and interfaces.
- Data-transformation pipelines — the functional style (`11-Functional/`) is often clearer.
- Creating a class per concept "because OOP" — abstraction has a cost; add it when it pays.

## Files in this folder

`ClassAndObject` (blueprint vs instance, stack/heap, references & aliasing) · `Constructors`
(chaining with `this(...)`) · `ThisKeyword` (all four uses) · `Encapsulation` · `Inheritance`
(`super`, upcasting, construction order) · `Polymorphism` (overloading + overriding, dynamic dispatch)
· `Abstraction` · `Interfaces` (multiple, `default`, diamond rule) · `StaticMembers` ·
`NestedClasses` · `EnumDemo` · `RecordDemo`

## Pitfalls

- Public mutable fields — breaks encapsulation permanently once callers depend on them.
- Confusing **overloading** with **overriding**. Always write `@Override` so the compiler catches it.
- `super(...)` / `this(...)` must be the **first** statement in a constructor.
- Overriding `equals` without `hashCode` — the object then misbehaves in `HashMap`/`HashSet`.
- Assuming fields are polymorphic. They are not.

## Where OOP is used

Domain modelling (User, Order, Account); frameworks and plugin systems (interfaces as extension
points); GUI toolkits; design patterns (Strategy, Factory, Observer are all polymorphism); ORM
entity mapping; and essentially every large Java codebase.

## Also in this folder

`EqualsAndHashCode` (the contract, and what breaks without it) · `AccessModifiers` (the visibility table) · `ImmutableClass` (the five rules, incl. defensive copying).
