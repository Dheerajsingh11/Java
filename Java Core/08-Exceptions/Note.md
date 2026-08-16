# Exceptions

An **exception** is an object representing an error or unusual condition. Throwing one unwinds the
call stack until a matching `catch` handles it — separating error handling from normal logic.

## Files

| File | Covers |
|------|--------|
| `ExceptionsDemo.java` | try/catch/finally, multi-catch, throw, custom exception, try-with-resources |

## The hierarchy

```
Throwable
├── Error            (serious JVM problems — don't catch: OutOfMemoryError, StackOverflowError)
└── Exception
    ├── (checked)    must be caught or declared: IOException, SQLException, custom checked
    └── RuntimeException (unchecked): NullPointerException, ArrayIndexOutOfBounds,
                          ArithmeticException, IllegalArgumentException, ...
```

## Checked vs unchecked

| | Checked | Unchecked (RuntimeException) |
|--|---------|------------------------------|
| Compiler enforces handling? | yes (catch or `throws`) | no |
| Represents | recoverable external conditions (I/O, DB) | programming bugs (null, bad index/arg) |

## Syntax essentials

- `try { risky } catch (SpecificException e) { … } finally { cleanup }`.
- **`finally` always runs** (barring JVM exit) — use for cleanup.
- Order catches **specific → general** (a broad `catch (Exception)` must come last).
- **`throw`** raises an exception; **`throws`** declares that a method may raise a checked one.
- **try-with-resources** `try (Resource r = …)` auto-closes any `AutoCloseable`, even on error —
  preferred over manual `finally { r.close(); }`.

## Best practices

- Catch the **most specific** exception you can handle; don't swallow exceptions silently.
- Don't use exceptions for ordinary control flow (they're costly and obscure intent).
- Add context to messages; wrap-and-rethrow to preserve the cause.
- Never catch `Error`.

## Applications

- Robust I/O and network/database code, input validation, resource management (files, sockets,
  connections), and clear API contracts about what can go wrong.
