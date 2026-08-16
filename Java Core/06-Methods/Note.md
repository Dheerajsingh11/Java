# Methods

A **method** is a named, reusable block of code that may take parameters and return a value. Methods
are the unit of behavior in Java.

## Files

| File | Covers |
|------|--------|
| `MethodsDemo.java` | overloading, varargs, recursion, pass-by-value |

## Concepts

- **Overloading**: same name, different parameter lists. Resolved by the **compiler** from the
  argument types (compile-time polymorphism). Return type alone cannot distinguish overloads.
- **Varargs** (`int... nums`): accept any number of arguments; inside, it is an array. Must be the
  last parameter.
- **Recursion**: a method calling itself. Needs a **base case** (to stop) and a **recursive case**
  (progressing toward the base). Costs O(depth) stack — deep recursion can overflow.

## Pass-by-value — Java's one rule (and a common trap)

Java is **always pass-by-value**:
- For **primitives**, the value is copied → changes inside a method don't affect the caller
  (so a "swap method" on two ints does nothing outside).
- For **objects**, the *reference* is copied, but the copy points at the **same object** → mutating
  the object's fields IS visible to the caller; reassigning the parameter is not.

## Good method design

- One clear responsibility; a descriptive verb name.
- Prefer returning values over mutating parameters (fewer surprises).
- Keep parameter lists short; validate inputs (guard clauses) early.

## Applications

- Everything: decomposition, code reuse, APIs. Recursion underpins tree/graph traversal and
  divide-and-conquer (see `Recursions/`, `Trees/`, merge/quick sort).
