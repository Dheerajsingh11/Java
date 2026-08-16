# String Algorithms

Strings are sequences of characters; these algorithms cover **pattern matching** (finding a substring)
and common transformations. In Java, `String` is **immutable** — building strings in a loop should use
`StringBuilder` to avoid O(n²) copying.

## Files

| File | Problem | Time |
|------|---------|------|
| `naivePatternMatch.java` | substring search (brute force) | O(n·m) |
| `KMP.java` | substring search (Knuth-Morris-Pratt) | O(n + m) |
| `rabinKarp.java` | substring search via rolling hash | O(n + m) avg |
| `anagramCheck.java` | are two strings anagrams | O(n) |

## Pattern matching compared

| Algorithm | Idea | Best for |
|-----------|------|----------|
| Naive | slide + compare | short inputs, simplicity |
| KMP | reuse matched prefix via LPS array | guaranteed linear, single pattern |
| Rabin-Karp | compare hashes, verify on hit | **multiple** patterns, plagiarism/dedup |

**LPS (KMP):** for each prefix, the length of the longest proper prefix that is also a suffix — it
tells KMP how far to "fall back" on a mismatch without re-reading the text.

**Rolling hash (Rabin-Karp):** the window's hash updates in O(1) as it slides (drop leaving char,
add entering char). Always verify the actual substring on a hash match — equal hashes can collide.

## Java string gotchas

- `==` compares references; use `.equals()` for content.
- `String` is immutable — concatenation in a loop is O(n²); use `StringBuilder`.
- `char c = s.charAt(i)`; `s.charAt(i) - 'a'` maps a lowercase letter to 0..25 for frequency tables.

## Applications

- Search (editors, grep), DNA/protein sequence matching, spam/plagiarism detection, autocomplete
  (with tries — see `Trees/Trie.java`), tokenizers, and compilers.
