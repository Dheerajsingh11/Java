# Hashing

## What it is

Hashing converts a key of any size into a small integer — an array index — so that data can be found
by **computing** its location instead of **searching** for it.

## Why hashing is a big deal

Every other lookup structure narrows candidates by comparing. Hashing skips comparison entirely:

| Structure | Lookup | How it finds things |
|---|---|---|
| Unsorted array | O(n) | inspect every element |
| Sorted array | O(log n) | halve the range repeatedly |
| Balanced BST | O(log n) | follow the ordering |
| **Hash table** | **O(1) average** | **compute the address directly** |

O(1) means the cost does not grow with the data — looking up one key among ten is the same as among
ten million. That is why hash tables are everywhere.

## The catch: collisions are guaranteed

A hash function squeezes a huge key space into a small table, so different keys **must** sometimes
collide (pigeonhole principle).

**The Birthday Paradox** shows how fast this happens: with just **23 people** in a room there is a
~50% chance two share a birthday; with 70 it is 99.9%. Collisions are not an edge case — they are the
normal operating condition, so a hash table is really defined by *how it handles them*.

## What makes a hash function good

1. **Deterministic** — the same key must always map to the same slot.
2. **Uniform** — spread keys evenly; clustering destroys the O(1) property.
3. **Fast** — O(1) for integers, O(length) for strings.
4. **Full range** — produce values across `0 .. m-1`.

Typical forms: `key % m` (use a **prime** m — a composite modulus makes keys sharing a factor
cluster); weighted polynomial sums for strings, `(s[0]·x⁰ + s[1]·x¹ + …) % m`.

## Collision handling — the two families

### Separate chaining (`Chaining.java`, `myHash.java`)
Each bucket holds a list of everything that hashed there.
- **Pros:** never "fills up"; deletion is trivial; degrades gracefully.
- **Cons:** per-node memory overhead; poor cache locality.
- Java's `HashMap` uses chaining — and converts a bucket to a **balanced tree** once it exceeds ~8
  entries, capping the worst case at O(log n) instead of O(n).

### Open addressing — everything in one array
On a collision, probe for another slot.

| Scheme | Probe sequence | Problem it solves / has |
|---|---|---|
| **Linear** (`linearProbing`) | h+1, h+2, h+3… | simple, great cache locality; suffers **primary clustering** (runs merge and grow) |
| **Quadratic** (`quadraticProbing`) | h+1², h+2², h+3²… | breaks up long runs; still has **secondary clustering** (same start ⇒ same path) |
| **Double hashing** (`doubleHashing`) | h₁ + i·h₂(key) | step size *depends on the key*, so no two keys share a path — best distribution |

**Deletion needs a tombstone.** In open addressing you cannot mark a removed slot as EMPTY — that
would truncate the probe path and hide keys stored beyond it. A separate DELETED marker (`-2` in
these files) means "keep probing" while still allowing reuse. This is a genuinely subtle point and a
common source of bugs.

## Load factor — the dial that controls performance

`α = n / m` (entries ÷ buckets) is the **average chain length**, which is exactly what a lookup
scans — hence O(1 + α).

- α too high → long chains/probe runs → lookups drift toward O(n)
- α too low → wasted memory

The fix is **rehashing**: when α crosses a threshold (Java uses 0.75), allocate a larger table and
reinsert everything. That single resize is O(n), but amortized across many insertions each operation
stays effectively O(1). Open addressing needs α < ~0.7 or probing degrades sharply.

## When to use hashing

- Membership tests, deduplication, frequency counting.
- Key→value lookup where **order does not matter**.
- Caching / memoization (dynamic programming leans on this).
- Detecting duplicates in one pass.

## When NOT to use hashing — this matters

Hashing destroys order, so it **cannot** answer:

- **Sorted iteration** — use a `TreeMap`/`TreeSet`.
- **Nearest / floor / ceiling value** — "largest key ≤ x" needs ordering.
- **Range queries** — "all keys between 10 and 50".
- **Prefix search** — use a **Trie** (`Trees/Trie.java`).
- **Worst-case guarantees** — adversarial keys can force O(n) (a real DoS vector; Java's
  tree-ification of buckets is a defence).

## The prefix-sum + hashing pattern

Several files here share one powerful idea. With `P(i)` = sum of the first i elements,
`sum(i..j) = P(j) − P(i)`. Therefore:

- **Zero-sum subarray exists** ⟺ some prefix sum **repeats** (`zeroSumArrEfficient`)
- **Subarray summing to k exists** ⟺ some earlier prefix equals `P(j) − k` (`sumInArrayEfficient`)
- **Longest such subarray** ⟺ store the **earliest** index per prefix sum (`longestSubArrEfficient`)

This converts O(n²) subarray scans into a single O(n) pass, and unlike sliding windows it **works
with negative numbers**. Seed the set/map with prefix 0 or subarrays starting at index 0 are missed —
a bug that was present in this repo.

## Files in this folder

**Implementations:** `myHash` (chaining), `Chaining`, `linearProbing`, `quadraticProbing`, `doubleHashing`
**Problems:** `countDistinct{Naive,Efficient}`, `frequency{Naive,Efficient}`,
`sumInArray{Naive,Efficient}`, `longestSubArr{Naive,Efficient}`, `zeroSumArr{Naive,Efficient}`

## Java specifics

- `HashMap` allows one null key; `Hashtable` does not; `ConcurrentHashMap` is the thread-safe choice.
- Keys must implement `hashCode`/`equals` **consistently**: equal objects must have equal hash codes,
  or lookups fail silently. Mutating a key after insertion makes it unfindable.
- `LinkedHashMap` preserves insertion order; `TreeMap` keeps keys sorted.

## Where hashing is used

Dictionaries and symbol tables in compilers, database indexing, caches (LRU pairs a `HashMap` with a
doubly linked list), routers, deduplication, cryptographic digests, blockchains, and Git — which
addresses every object by its content hash.
