# Exceptions

## What it is

An **exception** is an object describing something that went wrong. Throwing one unwinds the call
stack until a matching `catch` handles it.

## Why exceptions exist — what they replaced

The alternative is **error codes**: every function returns a status, and every caller checks it.

```c
int rc = readFile(path);
if (rc != 0) { ... }        // and if you forget this check, the error vanishes
```

Three problems that exceptions fix:

1. **Error codes are ignorable.** Forget the check and the program continues with bad data. An
   unhandled exception cannot be silently ignored — it propagates.
2. **They tangle the logic.** Error handling gets interleaved with the actual algorithm, obscuring it.
3. **They cannot cross layers.** A deep helper must return an error through every intermediate
   function. An exception propagates automatically to whoever can actually handle it.

**The core benefit: error handling is separated from the happy path, and errors cannot be lost.**

## The hierarchy

```
Throwable
├── Error              — serious JVM problems. DO NOT catch.
│                        OutOfMemoryError, StackOverflowError
└── Exception
    ├── (checked)      — compiler-enforced: IOException, SQLException
    └── RuntimeException (unchecked) — NullPointerException,
                          ArrayIndexOutOfBoundsException, IllegalArgumentException
```

## Checked vs unchecked — the distinction that matters

| | **Checked** | **Unchecked** (`RuntimeException`) |
|---|---|---|
| Must catch or declare? | **yes** — compiler enforces | no |
| Represents | recoverable **external** conditions | **programming bugs** |
| Examples | file missing, network down, DB unavailable | null deref, bad index, invalid argument |

**The intended rule:** if the caller can *reasonably recover*, make it checked. If it indicates a
**bug in the code**, make it unchecked — because you cannot "handle" a null-pointer bug at run time;
you fix it.

A `NullPointerException` should never be caught and ignored; it means the code is wrong. A missing
file genuinely might warrant a retry or a default.

> Checked exceptions are controversial. They force callers to acknowledge failure, but lead to
> `throws` clauses propagating everywhere and to the worst anti-pattern: an empty `catch` block that
> silences the problem. Many modern Java libraries favour unchecked exceptions for this reason.

## `finally` and why try-with-resources superseded it

`finally` **always** runs — whether the block completed, threw, or returned. Its classic use was
cleanup:

```java
BufferedReader r = null;
try { r = new BufferedReader(...); ... }
finally { if (r != null) r.close(); }   // verbose; and close() itself can throw
```

**try-with-resources** does this correctly and briefly:

```java
try (BufferedReader r = Files.newBufferedReader(path)) { ... }   // auto-closed, even on exception
```

Any `AutoCloseable` works, resources close in reverse order, and it correctly handles the case where
both the body *and* `close()` throw (the latter is attached as a "suppressed" exception rather than
masking the original). **Always prefer it** for files, streams, sockets, and connections — leaked
file handles are a real and hard-to-diagnose failure mode.

## Catch order

Catch blocks are checked top-down, so **specific before general**:

```java
catch (FileNotFoundException e) { ... }   // narrower first
catch (IOException e)           { ... }
catch (Exception e)             { ... }   // broadest last
```

Putting the broad one first makes the later ones unreachable — a compile error in Java, fortunately.

Multi-catch avoids duplication: `catch (IOException | SQLException e)`.

## Custom exceptions

Define one when you want callers to distinguish *your* failure from generic ones:

```java
class InsufficientFundsException extends Exception { ... }   // checked - caller can recover
```

Extend `Exception` for recoverable conditions, `RuntimeException` for programming errors. Give it a
useful message — including the values involved, as `ExceptionsDemo` does.

## Best practices

- **Catch what you can handle.** Catching `Exception` broadly hides bugs.
- **Never swallow silently.** An empty `catch {}` is where debugging goes to die. At minimum, log it.
- **Preserve the cause** when rethrowing: `throw new AppException("saving order", e)`. Losing the
  original stack trace is a common and costly mistake.
- **Don't use exceptions for control flow.** They are expensive (stack capture) and obscure intent —
  a loop that ends by catching `ArrayIndexOutOfBoundsException` is a bug, not a technique.
- **Fail fast** — validate arguments up front with `IllegalArgumentException`.
- **Never catch `Error`.** The JVM is in trouble; you cannot fix it from inside.

## When NOT to use exceptions

- **Expected outcomes.** "Item not found" in a lookup is normal — return `Optional` or `null`, don't
  throw.
- **Validation of routine user input** — return a result object; a wrong password is not exceptional.
- **Hot loops** — creating an exception captures the stack trace, which is comparatively slow.

## File in this folder

`ExceptionsDemo.java` — try/catch/finally, multi-catch and ordering, a checked custom exception, an
unchecked `ArithmeticException`, and try-with-resources showing automatic cleanup.

## Pitfalls

- Empty catch blocks.
- Catching `Exception` when you meant one specific type.
- Losing the cause by rethrowing without the original.
- Assuming `finally` cannot be skipped — `System.exit()` does skip it.
- A `return` inside `finally` **discards** any in-flight exception. Never do it.
- Catching `NullPointerException` instead of fixing the null.

## Where this leads

Every I/O operation (`12-Advanced/FileIODemo`), every parse (`Integer.parseInt` throws
`NumberFormatException`), and every collection/array access can fail. The guard-clause style used
throughout this repo — validate early, return or throw immediately — is the practical application.
