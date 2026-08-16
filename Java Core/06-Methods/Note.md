# Methods

## What it is

A **method** is a named, reusable block of code that may take parameters and return a value. Methods
are the unit of behaviour in Java and the primary tool for managing complexity.

## Why methods matter beyond "avoiding repetition"

Removing duplication is the obvious benefit. The larger ones:

- **Naming = documentation.** `isEligibleForDiscount(customer)` explains itself; the same logic
  inline as a five-condition `if` does not.
- **A unit of testing.** You can test a method; you cannot test the middle of a 200-line method.
- **A unit of change.** Fix a bug in one place instead of the seven places it was pasted.
- **Abstraction boundary.** Callers depend on *what* it does, not *how* — so the how can change.

## Overloading: same name, different parameters

```java
int    add(int a, int b)
double add(double a, double b)
int    add(int a, int b, int c)
```

The **compiler** picks which one to call, based on the argument types — this is *compile-time*
(static) polymorphism, in contrast to overriding, which is resolved at run time.

**Why it exists:** it gives one conceptual operation one name. `System.out.println` is overloaded for
every type; without it you would need `printlnInt`, `printlnString`, `printlnDouble`.

**Rules and traps:**
- The parameter **list** must differ. Changing only the **return type** is not overloading — it will
  not compile.
- Beware ambiguity with autoboxing and varargs; if the compiler cannot pick a single best match, it
  errors.

## Varargs

```java
static int sum(int... nums) { for (int n : nums) ... }   // nums behaves as an int[]
```

Lets callers pass any number of arguments. Must be the **last** parameter, and there can be only one.
Useful for genuinely variable inputs (`String.format`, `List.of`); avoid it when a fixed count is
expected, since it silently accepts zero arguments.

## Java is always pass-by-value — the rule people get wrong

This causes more confusion than any other Java rule, so state it precisely:

> **Java copies the value of the argument. Always.**
> For a primitive, that is the number.
> For an object, that is the **reference** — not the object.

Consequences:

```java
void tryToSwap(int x, int y) { ... }   // swaps COPIES -> caller sees nothing
void mutate(int[] arr)  { arr[0] = 99; }   // SAME array object -> caller DOES see it
void reassign(int[] arr) { arr = new int[]{7}; }   // rebinds the local copy -> caller sees nothing
```

The distinction that resolves it: **mutating the object** a reference points to is visible;
**reassigning the reference** is not. Java is *not* "pass-by-reference" — it passes references *by
value*.

This is exactly why the linked-list methods in this repo **return** the new head rather than assigning
to a `head` parameter (`Linked List/insertAtBegin.java`).

## Recursion

A method calling itself, with a **base case** to stop and a **recursive case** that makes progress
toward it.

**Use it when** the problem or data structure is self-similar — trees, graphs, divide and conquer,
backtracking. **Avoid it when** a simple loop does the job, or when depth is proportional to n on
large inputs (stack overflow risk).

Full treatment in `Data Structures with Java/Recursions/`.

## Designing good methods

- **One responsibility.** If you need "and" to describe it, split it.
- **Descriptive verb names.** `calculateTax`, not `doStuff` or `process`.
- **Few parameters.** More than ~4 usually signals a missing object.
- **Validate early with guard clauses** — check invalid input and return, rather than nesting the
  happy path inside layers of `if`.
- **Prefer returning a value over mutating a parameter.** Explicit data flow is easier to follow and
  to test.
- **Be consistent about `null`.** Prefer returning an empty collection or `Optional` (`11-Functional/`)
  over `null`.

## When NOT to extract a method

Decomposition has limits — over-extraction is a real failure mode:

- **A one-line method used once**, whose name adds nothing over the code itself (`private int
  addOne(int x)`), is indirection without benefit.
- **When it forces you to pass five parameters** to reconstruct context, the split is in the wrong
  place — the parameters are telling you an object is missing.
- **When it breaks a coherent sequence** into fragments the reader must jump between. A 30-line method
  that reads top-to-bottom can be clearer than six 5-line methods scattered across a file.
- **When the name would just restate the body** — `validateAndSaveAndNotify` means it should be three
  methods, not one badly named one.

The test is not line count; it is **whether the name lets a reader skip the body**. If it does,
extract. If they must read the body anyway, the extraction bought nothing.

## `static` vs instance methods

| | `static` | instance |
|---|---|---|
| Belongs to | the class | an object |
| Has `this` | **no** | yes |
| Can access instance fields | not directly | yes |
| Use for | stateless utilities (`Math.max`), factories, `main` | behaviour depending on object state |

`main` is static because **no object exists** when the program starts. A static method cannot touch
instance fields directly because it would not know *which* object's fields to use.

Static methods are **hidden**, not overridden — they are resolved by the reference type at compile
time, so they are not polymorphic (see `07-OOP`).

## Method signature — what actually distinguishes methods

The signature is the **name + parameter types**. Not the return type, not parameter *names*, not
`final`. That is why two methods differing only in return type cannot coexist.

## File in this folder

`MethodsDemo.java` — overloading, varargs, recursion, and a runnable demonstration that primitives
are unaffected by a method while a passed array is mutated.

## Pitfalls

- Expecting a "swap method" to work on primitives. It cannot — pass-by-value.
- Trying to overload on return type alone.
- Very long methods — the usual sign that several methods are hiding inside one.
- Deep recursion without considering stack depth.
- Returning `null` where an empty list or `Optional` would be safer.

## Where this leads

Methods are the vehicle for everything else: recursion drives tree/graph traversal and
divide-and-conquer sorts; overloading shapes the Collections API; and the pass-by-value rule explains
the return-the-new-head pattern used throughout the linked-list files.
