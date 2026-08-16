# Disjoint Set (Union-Find)

## What it is

A **Disjoint Set Union (DSU)** maintains a collection of non-overlapping groups and answers two
questions extremely fast:

- `find(x)` — which group is x in?
- `union(a, b)` — merge a's group with b's group.

## Why it exists — the problem nothing else solves well

"Are these two things connected?" asked repeatedly, while connections keep being **added**.

| Approach | Check connected | Add connection |
|---|---|---|
| Re-run BFS/DFS each query | O(V + E) per query | O(1) |
| Maintain a component-id array | O(1) | **O(n)** — relabel a whole group |
| **Union-Find** | **~O(1)** | **~O(1)** |

With q queries the naive approach costs O(q·(V+E)). Union-Find makes it effectively O(q). That gap is
why DSU exists.

## How it works

Each group is a tree; every element points to a parent, and the **root** identifies the group. `find`
walks to the root. `union` links one root under the other.

Done naively, those trees grow tall and `find` degrades to O(n). Two optimizations fix that — and
**both are needed**:

### 1. Path compression (in `find`)
After walking to the root, repoint **every node on the path** directly at the root.

```java
if (parent[x] != x) parent[x] = find(parent[x]);   // flatten on the way back
```

Each traversal permanently makes future traversals shorter — the structure literally learns from use.

### 2. Union by rank / size (in `union`)
Always attach the **shorter** tree under the taller one. Attaching the taller under the shorter would
increase the height for no reason.

**Together** these give an amortized cost of **O(α(n))**, where α is the inverse Ackermann function —
below 5 for any n that fits in the universe. Effectively constant, though not technically so.

## Why the return value of `union` is useful

`union(a, b)` returns `false` when a and b were **already** in the same group. That single boolean is
exactly the cycle test Kruskal's algorithm needs: adding an edge between two already-connected
vertices would create a cycle, so it must be skipped. A whole graph search collapses into one call.

## When to use Union-Find

- **Dynamic connectivity** — connections are added over time and you keep asking "connected?"
- **Kruskal's MST** — the canonical use (`Graphs/kruskalMST.java`).
- **Cycle detection in an undirected graph** — an edge whose endpoints already share a root closes a cycle.
- **Grouping / clustering** — friend circles, connected components, image-segment merging.
- **Counting components** — start at n, decrement on each successful union.

## When NOT to use it

- **You need to remove connections.** DSU is *merge-only*; there is no efficient `split`. Deletions
  require a different structure (link-cut trees) or rebuilding from scratch.
- **You need the actual path** between two elements — DSU only answers *whether* they are connected,
  not *how*. Use BFS/DFS for the route.
- **Directed graphs** — DSU models symmetric, transitive relationships. Directed reachability is a
  different problem (use SCC algorithms).
- The graph is static and you only ask once — a single DFS is simpler.

## Complexity

| Operation | Naive | Path compression only | + union by rank |
|---|---|---|---|
| `find` / `union` | O(n) | O(log n) amortized | **O(α(n)) ≈ O(1)** |
| Space | O(n) | O(n) | O(n) |

## File in this folder

`DisjointSet.java` — full implementation with path compression and union by rank, plus a demo showing
merging, connectivity queries, and the "already connected" return value.

## Where it is used

**Kruskal's MST**; connected components and "number of islands" style problems; network connectivity;
image segmentation (merging similar regions); percolation models in physics; account/identity merging
("are these the same user?"); type unification in compilers; and detecting cycles when adding
dependencies.
