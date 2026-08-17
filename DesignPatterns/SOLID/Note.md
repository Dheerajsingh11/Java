# SOLID Principles

Five principles for structuring classes so that change stays cheap and localized. Each file here
shows a **Before** (the violation) and an **After** (the fix) in one runnable program.

| | Principle | In one line | The smell it fixes |
|---|---|---|---|
| **S** | Single Responsibility | One reason to change | A class three different teams keep editing |
| **O** | Open-Closed | Extend without modifying | A `switch` over types repeated in every method |
| **L** | Liskov Substitution | Subtypes usable as the parent | An override that throws `UnsupportedOperationException` |
| **I** | Interface Segregation | No client forced to depend on unused methods | Empty method bodies |
| **D** | Dependency Inversion | Depend on abstractions | `new MySqlDatabase()` inside business logic |

## What each really means

**S — "responsibility" means audience, not size.** A class may have many methods and still have one
responsibility. Ask *who requests the changes*: if the DBA, marketing and compliance all edit the
same file, it does too much. The practical symptom is that testing one part requires setting up the
others — a tax calculation test needing a database and an SMTP server.

**O — you cannot be open to everything.** Abstracting one axis makes change along another harder.
Polymorphism is open to new **types** but closed to new **operations**; a `switch` over a **sealed**
type is exactly the reverse, and gets compiler-checked exhaustiveness (see
`Java Core/12-Advanced/SealedAndPatternMatching.java`). This is the "expression problem". Choose the
axis that actually varies — and only once the second case exists.

**L — "is-a" in English is not "is-a" in code.** A square *is* a rectangle mathematically, but a
`Rectangle` whose width and height vary independently is a *behavioural contract* that a `Square`
cannot honour. Subclasses must not strengthen preconditions, weaken postconditions, or break
invariants. Even the JDK violates this: `Arrays.asList()` returns a `List` whose `add()` throws.

**I — split by capability, not by method.** Empty bodies and `UnsupportedOperationException` are the
tell. A useful test: would any class implement one of these interfaces *without* the others? If not,
they belong together. One interface per method is its own kind of mess.

**D — "inversion" refers to the direction of the arrow.** Normally policy points at plumbing. Invert
it: define the interface in the **business layer's** language (`OrderRepository`, not `Database`) and
make the plumbing implement it. The payoff is testability — business rules verified with no
database, no network and no mail server.

## How they connect

- **I and L reinforce each other.** A fat interface forces implementers to fake methods, which is
  itself a Liskov violation. Segregating the interface fixes both at once.
- **D enables testing; S makes it worthwhile.** Inverted dependencies let you inject fakes; single
  responsibility means there is something small enough to be worth testing.
- **O usually needs D.** Extending without modifying generally means depending on an abstraction.

## DIP vs DI vs IoC

These three are constantly conflated:

- **DIP** — the *principle*: depend on abstractions.
- **Dependency Injection** — the *technique* for supplying them. Prefer **constructor** injection:
  dependencies become required, visible in the signature, and the fields can be `final`. Field
  injection hides them and cannot be set in a plain unit test.
- **Inversion of Control** — the broader idea that a framework calls your code rather than the reverse.

You can follow DIP with no framework at all — `main` does the wiring (the "composition root").

## The counter-risk

Every principle has an over-application failure mode:

- **SRP** → dozens of anaemic one-method classes; following one operation means opening eight files.
- **OCP** → interfaces abstracting an axis that never actually varies.
- **DIP** → an interface per class, each with exactly one implementation that will never change.

**Apply them to solve a problem you have felt, not preemptively.**

## Files

`S_SingleResponsibility` · `O_OpenClosed` · `L_LiskovSubstitution` · `I_InterfaceSegregation` ·
`D_DependencyInversion`
