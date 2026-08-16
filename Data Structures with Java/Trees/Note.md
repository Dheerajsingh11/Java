# Trees

## What it is

A **tree** is a hierarchical structure: one root, each node linking to children, no cycles. A
**binary tree** limits each node to at most two children (left, right).

## Why trees exist — the balance between arrays and linked lists

Arrays give fast search but slow insertion; linked lists give fast insertion but slow search. A
**balanced search tree gives you both at O(log n)**:

| | Sorted array | Linked list | Balanced BST | Hash table |
|---|---|---|---|---|
| Search | O(log n) | O(n) | **O(log n)** | O(1) |
| Insert / delete | O(n) | O(1)* | **O(log n)** | O(1) |
| Sorted iteration | O(n) | O(n) | **O(n)** | **impossible** |
| Min / max / range queries | O(1) / O(log n) | O(n) | **O(log n)** | **impossible** |

`*` once positioned.

**The column that matters is the last one.** Hash tables beat trees on raw lookup, but they destroy
order. When you need *sorted iteration, nearest-value, or range queries*, a tree is not merely
convenient — it is the only structure that can answer at all.

## The BST invariant and why it gives O(log n)

`left subtree < node < right subtree`, applied recursively.

Each comparison tells you which subtree the key must be in, discarding the other **entirely**. That
is binary search made structural — and unlike a sorted array, insertion doesn't require shifting.

**The catch:** O(log n) assumes the tree is *balanced*. Insert sorted keys 1,2,3,4,5 into a plain BST
and every node becomes a right child — a linked list with extra pointers, degrading every operation
to **O(n)**. This is not a rare edge case; sorted input is extremely common.

## Why self-balancing trees exist

**AVL** (`AVLTree.java`) enforces `|height(left) − height(right)| ≤ 1` at every node, restoring it
after each insert with O(1) **rotations**. Height stays O(log n) *by construction*.

`AVLTree.java` demonstrates this concretely: inserting 1..7 in order yields a tree of height 3 with 4
at the root — where a plain BST would produce a straight line of height 7.

| | AVL | Red-Black |
|---|---|---|
| Balance | strict | looser |
| Lookups | **faster** (shorter tree) | slightly slower |
| Insert/delete | more rotations | **fewer rotations** |
| Best for | read-heavy | write-heavy |

Java's `TreeMap`/`TreeSet` use Red-Black trees — a pragmatic choice for mixed workloads.

## Traversals — and when each is the right one

| Order | Sequence | Use it when |
|---|---|---|
| **Pre**-order | node, left, right | **copying/serializing** — the root arrives first, so you can rebuild top-down |
| **In**-order | left, node, right | **BST → sorted output**. This is *the* reason in-order matters |
| **Post**-order | left, right, node | **deleting/freeing**, or any bottom-up computation (height, diameter) — children must finish before the parent |
| **Level**-order (BFS) | top to bottom | shortest path in edges; anything that works level by level |

**Post-order is the "aggregate upward" traversal.** `treeHeightAndDiameter.java` shows why: computing
height and diameter together in one post-order pass is O(n), versus O(n²) if you recompute height at
every node.

Recursive traversal is 3 lines; iterative (`TreeTraversalsIterative.java`) needs an explicit stack
but avoids stack overflow on very deep trees. Iterative post-order is easiest via the
"node,right,left then reverse" trick.

## Specialized trees — what problem each solves

| Structure | Solves | Time |
|---|---|---|
| **Trie** (`Trie.java`) | prefix queries over strings | O(L) — depends on **key length**, not on how many keys are stored |
| **Segment tree** (`SegmentTree.java`) | range aggregate **+ point update** | O(log n) both |
| **Fenwick / BIT** (`FenwickTree.java`) | prefix sums + point update, minimal code | O(log n) both |

**Why a Trie beats a HashMap for autocomplete:** a hash of the whole word tells you nothing about
prefixes. A trie stores shared prefixes as shared paths, so "all words starting with `app`" is just
following three edges — the one query hashing fundamentally cannot answer.

**Range structures compared:**

| Need | Prefix-sum array | Fenwick | Segment tree |
|---|---|---|---|
| Query | O(1) | O(log n) | O(log n) |
| Update | **O(n)** | O(log n) | O(log n) |
| Ops beyond sum (min/max/gcd) | no | limited | **yes** |

Use a prefix-sum array for **static** data, Fenwick when you need sums + updates with least code, and
a segment tree when you need general aggregates or range updates. Fenwick's `i & (-i)` trick isolates
the lowest set bit, which encodes exactly which range each slot covers.

## When to use a tree

- Ordered data with frequent insertion/deletion (`TreeMap`).
- Range or nearest-neighbour queries (`floor`, `ceiling`, `subMap`).
- Hierarchical data by nature: file systems, org charts, DOM, ASTs.
- Prefix/autocomplete work → trie.
- Range statistics over changing data → segment/Fenwick tree.

## When NOT to use one

- Pure key lookup with no ordering needs → **HashMap** is faster (O(1) vs O(log n)).
- Tiny datasets → a linear scan over an array wins on constants.
- You cannot guarantee balance and inserts may be sorted → use a *self-balancing* tree, never a plain BST.

## Complexity summary

| Structure | Search | Insert | Delete | Notes |
|---|---|---|---|---|
| BST (balanced) | O(log n) | O(log n) | O(log n) | |
| BST (degenerate) | O(n) | O(n) | O(n) | sorted input causes this |
| AVL / Red-Black | O(log n) | O(log n) | O(log n) | guaranteed |
| Trie | O(L) | O(L) | O(L) | L = key length |
| Segment / Fenwick | O(log n) query & update | | | |

## Files in this folder

`BinaryTree` (structure, all four traversals, height, size) · `TreeTraversalsIterative` ·
`BinarySearchTree` (insert/search/delete with the three delete cases) ·
`treeHeightAndDiameter` (naive O(n²) vs one-pass O(n)) · `lowestCommonAncestor` (BST O(h) vs general
O(n)) · `AVLTree` · `Trie` · `SegmentTree` · `FenwickTree`

## The three BST deletion cases (the part people get wrong)

1. **Leaf** — just remove it.
2. **One child** — splice the child into its place.
3. **Two children** — replace the node's value with its **in-order successor** (the smallest key in
   the right subtree), then delete that successor. The successor is chosen because it is the only
   value that preserves the BST invariant on *both* sides.

## Where trees are used

Database indexes (B-trees/B+trees — a generalization of BSTs for disk), file systems, `TreeMap`/
`TreeSet`, compilers (abstract syntax trees), the HTML DOM, routing tables, Huffman coding
(`Greedy/`), decision trees in ML, spatial indexes (quadtrees, k-d trees), and Git's object model.

## Also in this folder

`RedBlackTree` (relaxed balancing, fewer rotations than AVL) · `validateBST` (why the naive per-node check is wrong) · `treeViews` (level order, zigzag, left/right/top views)
