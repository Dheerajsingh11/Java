# Object-Oriented Programming (OOP) in Java

OOP models a program as a collection of **objects** — bundles of state (fields) and behaviour
(methods) that interact. Java is object-oriented to the core: almost all code lives inside classes.

## The four pillars

| Pillar | One-line idea | File |
|--------|---------------|------|
| **Encapsulation** | Hide internal state; expose controlled access. | `Encapsulation.java` |
| **Inheritance** | Reuse/extend a base class ("is-a"). | `Inheritance.java` |
| **Polymorphism** | One interface, many forms (overload + override). | `Polymorphism.java` |
| **Abstraction** | Expose *what*, hide *how*; forbid incomplete objects. | `Abstraction.java` |

## Building blocks in this folder

| Concept | What it gives you | File |
|---------|-------------------|------|
| Class & Object | Blueprint vs. concrete instance | `ClassAndObject.java` |
| Constructors | Guaranteed valid initialization; chaining with `this(...)` | `Constructors.java` |
| `this` | Reference to the current object (shadowing, chaining, passing self) | `ThisKeyword.java` |
| Interfaces | Multiple capability contracts; `default` methods | `Interfaces.java` |
| `static` members | Class-level shared state/behaviour; initializer blocks | `StaticMembers.java` |
| Nested classes | static nested / inner / local / anonymous | `NestedClasses.java` |
| `enum` | Type-safe fixed set of constants with data + behaviour | `EnumDemo.java` |
| `record` | Boilerplate-free immutable data carrier (Java 16+) | `RecordDemo.java` |

## When to use what

- **Interface vs. abstract class:** need multiple "capabilities" or unrelated implementers →
  interface. Need shared *state* + partial implementation for an "is-a" family → abstract class.
  A class implements many interfaces but extends only one class.
- **Inheritance vs. composition:** prefer composition ("has-a") unless there is a genuine "is-a".
  Deep inheritance trees are brittle.
- **`record` vs. class:** immutable, transparent data → record. Mutable or behaviour-rich → class.
- **`enum` vs. constants:** any fixed set of named options → enum (type-safe, switchable).

## Common pitfalls

- Public mutable fields break encapsulation — keep fields `private`.
- Overriding requires the **same signature**; a different parameter list is *overloading*, not
  overriding. Use `@Override` so the compiler catches mistakes.
- `super(...)` / `this(...)` must be the **first** statement in a constructor.
- Static methods have no `this` and cannot access instance members directly.

## Real-world applications

- Domain modelling (Users, Orders, Accounts), GUI toolkits, plugin architectures (interfaces),
  strategy/factory patterns (polymorphism), configuration constants (enums), DTOs (records).
