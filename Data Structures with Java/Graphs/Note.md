# Graphs

A **graph** is a set of vertices connected by edges. Edges may be **directed** or **undirected**,
and **weighted** or **unweighted**. Graphs model networks, maps, dependencies, and relationships.

## Representations

| Representation | Space | Edge lookup | Best for |
|----------------|-------|-------------|----------|
| Adjacency list | O(V + E) | O(degree) | sparse graphs, traversal (**default**) |
| Adjacency matrix | O(V²) | O(1) | dense graphs, frequent edge checks |

See `Graph.java` for the adjacency-list class reused across this folder.

## Algorithms in this folder

| File | Solves | Time |
|------|--------|------|
| `BFS.java` | traversal; shortest path on **unweighted** graphs | O(V+E) |
| `DFS.java` | traversal (recursive + iterative); components | O(V+E) |
| `dijkstra.java` | single-source shortest path, **non-negative** weights | O((V+E) log V) |
| `bellmanFord.java` | single-source with **negative** edges; negative-cycle detection | O(V·E) |
| `floydWarshall.java` | **all-pairs** shortest paths | O(V³) |
| `topologicalSort.java` | order a DAG (Kahn's + DFS) | O(V+E) |
| `cycleDetection.java` | cycle in undirected / directed graph | O(V+E) |
| `kruskalMST.java` | minimum spanning tree (edge-based + Union-Find) | O(E log E) |
| `primMST.java` | minimum spanning tree (vertex-based + heap) | O(E log V) |

## Choosing a shortest-path algorithm

- Unweighted → **BFS**.
- Non-negative weights, one source → **Dijkstra**.
- Negative weights / detect negative cycle → **Bellman-Ford**.
- All pairs, small graph → **Floyd-Warshall**.

## Choosing an MST algorithm

- Sparse / edge list → **Kruskal** (with Union-Find, see `DisjointSet/`).
- Dense / adjacency list → **Prim** (with a heap).

## Key ideas

- **Mark visited** to avoid infinite loops on cycles.
- BFS = queue (level order); DFS = stack/recursion (go deep).
- **Relaxation** (`if dist[u]+w < dist[v]`) is the heart of every shortest-path algorithm.
- Topological order exists **iff** the directed graph is acyclic (a DAG).

## Applications

- Maps/GPS routing, social networks, web crawling, dependency resolution (build systems, package
  managers), scheduling, network flow, and compiler analysis.
