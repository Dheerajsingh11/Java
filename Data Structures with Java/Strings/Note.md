# String Algorithms

## What it is

Algorithms over sequences of characters — dominated by **pattern matching**: finding where (or
whether) a pattern occurs inside a text.

## Why pattern matching needs its own algorithms

The naive approach slides the pattern one position at a time and re-compares from scratch. Its
weakness is **forgetting what it just learned**:

```
Text:    A A A A A A A A B
Pattern: A A A B
```

At each position it matches "AAA", fails on the 4th character, then shifts by one and re-compares
those same characters again. That is O(n·m).

**The key realization: a partial match tells you something.** If you matched "AAA" before failing,
you already know what those characters were — so you should be able to skip ahead without re-reading
the text. Exploiting that is what KMP and Rabin-Karp do, in two very different ways.

## KMP — never re-read the text

KMP precomputes the **LPS array** ("longest proper prefix that is also a suffix") for the pattern.
On a mismatch after matching `j` characters, `LPS[j-1]` says how many of those already-matched
characters can be **reused** as a new prefix — so the text pointer never moves backwards.

For pattern `AABAA`, LPS is `[0,1,0,1,2]`. The trailing "AA" is also a prefix, so after a mismatch
following "AABAA" we can resume as if "AA" were already matched instead of restarting.

**Result: O(n + m) guaranteed.** Each text character is examined once. The preprocessing is O(m).

## Rabin-Karp — compare numbers, not strings

Hash the pattern once, then hash each length-m window of the text and compare **integers**. Most
windows are rejected by a single comparison.

The trick is the **rolling hash**: sliding the window by one is O(1) — subtract the leaving
character's contribution, multiply by the base, add the entering character. You never re-hash the
whole window.

**Crucially, you must verify on a hash match.** Equal hashes do not guarantee equal strings
(collisions). The implementation compares the actual substring on a hit — skip that and the algorithm
is simply wrong.

Average O(n + m); worst case O(n·m) if an adversary forces constant collisions.

## Which matcher to use

| Situation | Use | Why |
|---|---|---|
| Short text/pattern, clarity matters | **naive** | no preprocessing, easy to verify |
| Single pattern, need a **guarantee** | **KMP** | O(n+m) worst case, no collision risk |
| **Multiple patterns** at once | **Rabin-Karp** | hash all patterns, compare each window against the set |
| Plagiarism / duplicate detection | **Rabin-Karp** | fingerprinting is exactly what rolling hashes do |
| Real production search in Java | `String.indexOf` / `Pattern` | tuned library code |
| **Prefix** queries, autocomplete | **Trie** (`Trees/Trie.java`) | matching is a different problem from searching |

**Rabin-Karp's real advantage is multiple patterns**, not raw speed on one — that is where its
hashing pays off and KMP would need a separate pass per pattern.

## Java strings: the facts that change how you write code

**Strings are immutable.** Every "modification" creates a new object. The consequences are practical:

- Concatenating in a loop with `+` is **O(n²)** — each step copies the entire accumulated string.
  Use `StringBuilder` for O(n).
- Immutability makes strings safe to share across threads and safe as `HashMap` keys.
- **`==` compares references, `.equals()` compares content.** Literals are interned in the string
  pool so `"hi" == "hi"` is true, but `new String("hi") == "hi"` is false. This inconsistency is why
  you must always use `.equals()`.

`s.charAt(i) - 'a'` maps a lowercase letter to `0..25` — the standard way to build a 26-slot
frequency table, as in `anagramCheck`.

## Frequency counting vs sorting (the anagram lesson)

Two strings are anagrams when they contain the same multiset of characters.

- **Sort both and compare** — O(n log n), works for any alphabet, 3 lines.
- **Count frequencies** — O(n), fixed O(1) space for a known alphabet.

`anagramCheck.java` shows both. The counting version increments for one string and **decrements** for
the other, then checks all zeros — one pass instead of two. The same frequency-table idea drives
sliding-window substring problems.

## When to use these algorithms

- Search inside large text bodies (editors, logs, `grep`).
- Bioinformatics — DNA/protein motif finding, where texts are enormous.
- Plagiarism and near-duplicate detection (rolling hashes).
- Tokenizers, lexers, and protocol parsers.
- Anagram/permutation grouping.

## When NOT to use them

- **Prefix or autocomplete queries** → a Trie. Hashing a whole word tells you nothing about its
  prefixes.
- **Fuzzy / approximate matching** → edit distance (`DynamicProgramming/editDistance.java`), not exact
  matching.
- **Complex patterns** (wildcards, character classes) → regular expressions.
- Small inputs where `String.indexOf` is clearer and fast enough — do not hand-roll KMP for a
  20-character search.

## Files in this folder

| File | Problem | Time | Space |
|---|---|---|---|
| `naivePatternMatch` | substring search, brute force | O(n·m) | O(1) |
| `KMP` | substring search via the LPS array | **O(n+m)** | O(m) |
| `rabinKarp` | substring search via rolling hash | O(n+m) avg | O(1) |
| `anagramCheck` | anagram test, sorting vs frequency counting | O(n) | O(1) |

## Pitfalls

- Using `==` instead of `.equals()` — works for literals, fails for computed strings. Insidious.
- Building strings with `+` inside a loop → quadratic.
- Forgetting to **verify** a Rabin-Karp hash match.
- Off-by-one in window bounds: the last valid start index is `n - m`.
- `substring(a, b)` — `b` is **exclusive**.

## Where these are used

Text editors and IDE search, `grep` and `ripgrep`, DNA sequence alignment, spam and plagiarism
detection, intrusion-detection signature matching, spell-checkers (with tries and edit distance),
compilers' lexical analysis, and log processing.
