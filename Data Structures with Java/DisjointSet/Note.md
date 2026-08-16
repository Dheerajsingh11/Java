# Disjoint Set (Union-Find)

A **Disjoint Set Union (DSU)** maintains a partition of elements into non-overlapping sets and
answers two questions fast: *are x and y in the same set?* and *merge x's set with y's set*.

## Operations

| Operation | Meaning | Time (optimized) |
|-----------|---------|------------------|
| `find(x)` | representative (root) of x's set | ~O(α(n)) |
| `union(a, b)` | merge the sets of a and b | ~O(α(n)) |
| `connected(a, b)` | same set? | ~O(α(n)) |

`α(n)` is the inverse Ackermann function — effectively a small constant (< 5) for any real n.

## The two optimizations (both needed for near-constant time)

1. **Path compression** (in `find`): after walking to the root, repoint every node on the path
   directly at the root, flattening the tree.
2. **Union by rank/size** (in `union`): attach the shorter/smaller tree under the taller/larger one
   so trees stay shallow.

Without them, `find` degrades to O(n) on adversarial input.

## Applications

- **Kruskal's MST** (detect whether adding an edge forms a cycle — see `Graphs/kruskalMST.java`).
- Connected components / dynamic connectivity, cycle detection in undirected graphs, network/
  friend-circle grouping, percolation, and image-segmentation label merging.
