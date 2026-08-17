# Design Patterns & SOLID

## What this is

**Design patterns** are named, reusable solutions to problems that recur in object-oriented design.
They are not code you copy — they are *shapes* you recognize, plus a shared vocabulary. Saying
"that's a Strategy" communicates in three words what would otherwise take a paragraph.

**SOLID** is five principles for structuring classes so that change is cheap and localized.

## Why patterns are worth learning — and the honest caveat

The value is real but often oversold:

**What they genuinely give you**
- **Vocabulary.** "Wrap it in a decorator" is precise and instantly understood.
- **Recognition.** You stop re-deriving solutions to problems that were solved in 1994.
- **Reading other people's code.** The JDK, Spring and Hibernate are built from these shapes.

**What they do not give you**
- Patterns are **not goals**. Code is not better for containing more of them.
- Applied speculatively they are **the definition of over-engineering** — indirection you pay for
  today against flexibility you may never need.
- Several exist mainly to work around limitations of 1990s languages. In modern Java a Strategy is
  often just a lambda, and a Singleton is usually better handled by a DI container.

> **The rule that matters: reach for a pattern when you have felt the pain it solves — not before.**
> Write the `switch` first. When the third case arrives and the switch appears in four methods, *then*
> introduce polymorphism, with evidence rather than a guess.

## The three categories

| Category | Concerned with | Patterns here |
|---|---|---|
| **Creational** | *how objects are made* | Singleton, Factory, Abstract Factory, Builder, Prototype |
| **Structural** | *how objects are composed* | Adapter, Decorator, Proxy, Facade, Composite, Bridge, Flyweight |
| **Behavioural** | *how objects interact* | Strategy, Observer, Command, State, Chain of Responsibility, Template Method, Iterator, Mediator |

## Quick selection guide

| If you need to… | Use |
|---|---|
| Build an object with many optional fields | **Builder** |
| Decide which implementation at run time | **Factory** |
| Create a matching SET of related objects | **Abstract Factory** |
| Guarantee exactly one instance | **Singleton** (or a DI container — usually better) |
| Copy an expensive object | **Prototype** |
| Make an incompatible class fit your interface | **Adapter** |
| Add behaviour without subclassing every combination | **Decorator** |
| Control access — lazy, secured, cached | **Proxy** |
| Simplify a complicated subsystem | **Facade** |
| Treat one object and a group identically | **Composite** |
| Stop two varying dimensions multiplying | **Bridge** |
| Share memory across very many similar objects | **Flyweight** |
| Swap an algorithm | **Strategy** |
| Notify many parties of an event | **Observer** |
| Support undo, queuing, or replay | **Command** |
| Change behaviour as internal state changes | **State** |
| Pass a request until someone handles it | **Chain of Responsibility** |
| Fix a sequence but vary the steps | **Template Method** |
| Traverse without exposing the structure | **Iterator** |
| Stop N objects referencing each other | **Mediator** |

## The pairs people confuse

These look alike in code; the difference is **intent**:

| A vs B | The distinction |
|---|---|
| **Strategy vs State** | Strategy: the *client* picks. State: the object *transitions itself*. |
| **Adapter vs Facade** | Adapter *changes* one interface. Facade *simplifies* many classes. |
| **Decorator vs Proxy** | Decorator *adds* behaviour and always delegates. Proxy *controls* access and may not delegate at all. |
| **Bridge vs Strategy** | Bridge is structural and designed in up front. Strategy is behavioural and swapped at run time. |
| **Mediator vs Observer** | Mediator *coordinates* with rules. Observer *broadcasts* with none. |
| **Factory vs Abstract Factory** | Factory makes one product. Abstract Factory makes a matching family. |

## SOLID

| | Principle | In one line | The smell it fixes |
|---|---|---|---|
| **S** | Single Responsibility | One reason to change | A class edited by three different teams |
| **O** | Open-Closed | Extend without modifying | A `switch` over types repeated in every method |
| **L** | Liskov Substitution | Subtypes must be usable as the parent | An override that throws `UnsupportedOperationException` |
| **I** | Interface Segregation | No client forced to depend on unused methods | Empty method bodies |
| **D** | Dependency Inversion | Depend on abstractions | `new MySqlDatabase()` inside business logic |

Two connections worth noticing:

- **I and L reinforce each other.** A fat interface forces implementers to fake methods, which is
  itself a Liskov violation. Segregating the interface fixes both.
- **O has a limit.** You can be open to new *types* or to new *operations*, rarely both — the
  "expression problem". Polymorphism favours new types; a `switch` over a **sealed** type favours
  new operations and gets compiler-checked exhaustiveness (see
  `Java Core/12-Advanced/SealedAndPatternMatching.java`).

## Anti-patterns to recognize

- **God object** — a class that knows and does everything. Often a Mediator or Facade that grew.
- **Singleton abuse** — global mutable state with a respectable name. Hides dependencies, breaks
  tests, contends under concurrency.
- **Premature abstraction** — an interface with one implementation that will never gain a second.
- **Pattern fever** — applying patterns to demonstrate knowledge. A factory producing one type, or a
  strategy with one implementation, is pure cost.
- **Anaemic classes** — SRP taken so far that following one operation means opening eight files.

## Patterns you already use in the JDK

| Pattern | In the JDK |
|---|---|
| Strategy | `Comparator` |
| Decorator | `new BufferedReader(new InputStreamReader(in))` |
| Factory | `Calendar.getInstance()`, `DriverManager.getConnection()` |
| Builder | `StringBuilder`, `HttpRequest.newBuilder()` |
| Singleton | `Runtime.getRuntime()` |
| Iterator | every for-each loop |
| Observer | Swing listeners, `PropertyChangeListener` |
| Flyweight | the `Integer` cache (−128..127), string interning |
| Template Method | `AbstractList`, `HttpServlet.service()` |
| Proxy | `java.lang.reflect.Proxy`, Hibernate lazy loading |
| Adapter | `Arrays.asList()`, `InputStreamReader` |

## Folders

| Folder | Contents |
|---|---|
| `Creational/` | Singleton, Factory, Abstract Factory, Builder, Prototype |
| `Structural/` | Adapter, Decorator, Proxy, Facade, Composite, Bridge, Flyweight |
| `Behavioral/` | Strategy, Observer, Command, State, Chain of Responsibility, Template Method, Iterator, Mediator |
| `SOLID/` | Each principle as a Before/After pair in one runnable file |

Every file uses a real domain — payments, HTTP requests, order lifecycles, expense approval,
filesystems, playlists, chat rooms — rather than `Foo`/`Bar`, and each records **when NOT to use**
the pattern alongside when to use it.

## Where this leads

Patterns underpin every framework you will meet: Spring is Dependency Inversion plus Proxy plus
Template Method; Hibernate is Proxy plus Unit of Work; Kafka consumers are Observer. Recognizing the
shapes makes unfamiliar frameworks far faster to learn.
