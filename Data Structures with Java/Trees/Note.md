# Trees

A **tree** is a hierarchical structure of nodes: one root, and each node linking to child nodes,
with no cycles. A **binary tree** limits each node to at most two children (left, right).

## Files

| File | Covers |
|------|--------|
| `BinaryTree.java` | node structure; pre/in/post-order + level-order; size; height |
| `TreeTraversalsIterative.java` | the same DFS orders without recursion (explicit stack) |
| `BinarySearchTree.java` | ordered set: insert / search / delete (3 delete cases) |
| `treeHeightAndDiameter.java` | height; diameter (naive O(n²) vs efficient O(n)) |
| `lowestCommonAncestor.java` | LCA for a BST (O(h)) and a general binary tree (O(n)) |
| `AVLTree.java` | self-balancing BST via rotations → guaranteed O(log n) |
| `Trie.java` | prefix tree for strings: insert / search / startsWith |
| `SegmentTree.java` | range-sum query + point update, O(log n) each |
| `FenwickTree.java` | Binary Indexed Tree: prefix sums + point update, minimal code |

## Traversals

| Order | Visit sequence | Typical use |
|-------|----------------|-------------|
| Preorder | node, left, right | copy/serialize a tree |
| Inorder | left, node, right | **BST → sorted output** |
| Postorder | left, right, node | delete/free (children first) |
| Level-order (BFS) | top to bottom, left to right | shortest edges, level grouping |

## Binary Search Tree invariant

`left subtree < node < right subtree`. This makes search/insert/delete O(h). But a plain BST can
degrade to a line (h = n) on sorted input — that is exactly what **AVL** (and Red-Black) trees fix
by keeping height O(log n).

## Range-query structures

| Need | Prefix-sum array | Fenwick (BIT) | Segment tree |
|------|------------------|---------------|--------------|
| Query | O(1) | O(log n) | O(log n) |
| Update | O(n) | O(log n) | O(log n) |
| Flexible ops (min/max/gcd) | no | limited | yes |

Use prefix sums for static data, Fenwick for prefix-sum + updates (least code), segment tree when
you need general aggregates or range updates.

## Complexity summary

| Structure | search | insert | delete |
|-----------|--------|--------|--------|
| BST (balanced) | O(log n) | O(log n) | O(log n) |
| BST (skewed) | O(n) | O(n) | O(n) |
| AVL | O(log n) | O(log n) | O(log n) |
| Trie | O(L) | O(L) | O(L) |

## Applications

- BST/AVL: ordered maps/sets, databases. Trie: autocomplete, spell-check, IP routing.
  Segment/Fenwick trees: range statistics in competitive programming and analytics. BFS/DFS
  traversals underpin almost every tree/graph algorithm.
