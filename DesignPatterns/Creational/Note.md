# Creational Patterns

Concerned with **how objects are created** — decoupling callers from concrete classes, controlling
instantiation, and keeping construction readable when it gets complicated.

| Pattern | Use when | Avoid when |
|---|---|---|
| **Singleton** | Genuinely one shared resource (pool, registry) | Almost always — prefer DI. It is global mutable state |
| **Factory** | The concrete type is chosen at run time | Only one implementation exists |
| **Abstract Factory** | A *family* of products must match each other | The products are independent |
| **Builder** | Many optional parameters, or you want immutability | Fewer than ~4 params — use a constructor or `record` |
| **Prototype** | Copying is far cheaper than constructing | The object is cheap, or immutable (just share it) |

## The key ideas

- **Singleton** — use the `enum` form: it is the only one safe against both reflection *and*
  serialization. But ask first whether a DI container should own the lifecycle instead. This is the
  most overused pattern in the catalogue: it hides dependencies, breaks test isolation, and becomes
  a contention point under concurrency.
- **Builder** — replaces telescoping constructors (unreadable at the call site) *and* JavaBean
  setters (mutable, and temporarily invalid between `new` and the last setter). Validation belongs
  in `build()`, so the object is never born broken.
- **Prototype** — the whole pattern is the **shallow vs deep** distinction. A shallow copy shares
  mutable state, so mutating the "copy" corrupts the original. `PrototypePattern.java` demonstrates
  that corruption happening before showing the deep copy that avoids it.
- **Factory vs Abstract Factory** — one product versus a matching family. Abstract Factory's
  weakness: adding a new *product type* means editing every factory.

## Avoid `Cloneable`

Java's built-in cloning is a known design mistake: `Cloneable` declares no method, `clone()` is
protected and throws a checked exception, it is shallow by default, and it bypasses constructors.
Use a **copy constructor** or a plain `copy()` method instead.

## Files

`SingletonPattern` · `FactoryPattern` · `AbstractFactoryPattern` · `BuilderPattern` · `PrototypePattern`
