# Structural Patterns

Concerned with **how objects are composed** into larger structures while keeping them flexible.

| Pattern | Intent | One-line test |
|---|---|---|
| **Adapter** | Make an incompatible class fit | "I can't change this class but need a different interface" |
| **Decorator** | Add behaviour, same interface | "I need these features in any combination" |
| **Proxy** | Control access, same interface | "I want to intercept *whether* this is called" |
| **Facade** | Simplify a subsystem | "Callers shouldn't need to know these six classes" |
| **Composite** | Treat one and many alike | "This is a tree and I keep writing `instanceof`" |
| **Bridge** | Stop two dimensions multiplying | "I have Type x Channel classes" |
| **Flyweight** | Share memory across many objects | "I have a million near-identical objects" |

## The distinctions that matter

- **Adapter vs Facade** — Adapter wraps *one* class to **change** its interface (compatibility).
  Facade wraps *many* to **reduce** complexity (simplification).
- **Decorator vs Proxy** — structurally near-identical; the difference is intent. A decorator
  **adds** behaviour and always delegates. A proxy **controls** access and may skip the real object
  entirely — a cache hit never calls it.
- **Bridge vs Adapter** — Bridge is **designed in** before the fact; Adapter is **retrofitted** after.
- **Bridge vs Strategy** — same structure. Bridge is structural and about long-term independent
  evolution of two hierarchies; Strategy is behavioural and about swapping an algorithm at run time.

## The class-explosion argument

Both Decorator and Bridge exist to stop combinatorial growth:

- **Decorator** — 4 optional features would need **2⁴ = 16** subclasses; decorators need **4**.
- **Bridge** — 3 types x 3 channels would need **9** classes; Bridge needs **3 + 3 = 6**.

The saving compounds: with Bridge, adding one channel costs one class instead of one per type.

## Watch out for

- **Decorator order matters.** `cache(retry(x))` and `retry(cache(x))` behave very differently — one
  caches a success so retries never happen again, the other may retry a cached failure forever.
- **Facades becoming god objects.** Keep them task-oriented, and leave the subsystem usable directly
  for callers with unusual needs.
- **Flyweights must be immutable** — they are shared, so a single mutation affects every user at once.
- **Deep decorator stacks** make stack traces long and debugging harder. `java.io` is the cautionary
  example: powerful, but `new BufferedReader(new InputStreamReader(new FileInputStream(f)))`.
- **Proxies that hide cost** — a lazy proxy quietly triggering a database load inside a loop is the
  classic Hibernate N+1 problem.

## Files

`AdapterPattern` · `DecoratorPattern` · `ProxyPattern` · `FacadePattern` · `CompositePattern` ·
`BridgePattern` · `FlyweightPattern`
