# Graphs

## What it is

A **graph** is a set of **vertices** connected by **edges**. Edges may be **directed** (one-way) or
**undirected**, **weighted** (carrying a cost) or unweighted.

## Why graphs matter

Trees and lists are *special cases* of graphs — a tree is a connected graph with no cycles, a linked
list is a path. Graphs are the general model for **anything with relationships**: roads, friendships,
web links, task dependencies, network packets, state machines.

Because the model is so general, learning a handful of graph algorithms lets you solve problems that
look completely unrelated on the surface.

## Representation: the first real decision

| | Adjacency **list** | Adjacency **matrix** |
|---|---|---|
| Space | **O(V + E)** | O(V²) |
| List a vertex's neighbours | **O(degree)** | O(V) |
| Test "is there an edge u→v?" | O(degree) | **O(1)** |
| Best for | **sparse** graphs (most real ones) | dense graphs, frequent edge tests |

**Default to the adjacency list.** Real graphs are overwhelmingly sparse — a social network with a
billion users does not have a billion friends per user. A matrix would waste O(V²) memory storing
mostly zeros. `Graph.java` implements the list form used throughout this folder.

## BFS vs DFS — same cost, different superpower

Both visit every vertex and edge once: **O(V + E)**. The only difference is the container holding the
frontier — and that changes everything.

| | BFS (**queue**) | DFS (**stack**/recursion) |
|---|---|---|
| Explores | in rings of increasing distance | one path as deep as possible, then backtracks |
| Gives you | **shortest path (unweighted)** | cycle detection, topological order, components |
| Memory | O(width) — can be huge on wide graphs | O(depth) |

**Why BFS finds shortest paths free:** it expands strictly in order of distance, so the *first* time
it reaches a vertex is necessarily along a fewest-edge path. Nothing extra is computed — the FIFO
ordering *is* the proof.

**Why DFS suits ordering problems:** a vertex "finishes" only after everything reachable from it has
finished. That property directly yields topological sort and cycle detection.

## Relaxation — the idea behind every shortest-path algorithm

```java
if (dist[u] + weight(u,v) < dist[v]) dist[v] = dist[u] + weight(u,v);
```

"Have I found a cheaper way to reach v?" Dijkstra, Bellman-Ford, and Floyd-Warshall differ only in
**the order and how many times** they relax edges. Recognizing this makes all three feel like one
algorithm with three schedules.

## Choosing a shortest-path algorithm

| Situation | Use | Why |
|---|---|---|
| **Unweighted** | **BFS** — O(V+E) | edge count *is* the distance |
| Non-negative weights, one source | **Dijkstra** — O((V+E) log V) | greedy: the nearest unsettled vertex is final |
| **Negative** weights | **Bellman-Ford** — O(V·E) | Dijkstra's greedy assumption breaks |
| Need to **detect a negative cycle** | **Bellman-Ford** | a V-th improving pass proves one exists |
| **All pairs**, small dense graph | **Floyd-Warshall** — O(V³) | 3 nested loops, trivially simple |

**Why Dijkstra fails on negative edges — the crux:** Dijkstra assumes that once it settles the
closest unvisited vertex, no later path can improve it. A negative edge can *reduce* a total after
the fact, breaking that guarantee. Bellman-Ford makes no such assumption — it just relaxes every edge
V−1 times (a shortest path uses at most V−1 edges), which is why it is slower but more general.

**Why Bellman-Ford detects negative cycles:** after V−1 rounds all genuine shortest paths are final.
If a V-th round still improves something, the only explanation is a cycle with negative total weight
that can be looped forever.

**Floyd-Warshall** is dynamic programming: `dist[i][j]` allowing intermediates `0..k` is the better of
"don't use k" and "go i→k→j". Sweeping k over all vertices considers every possible waypoint.

## Minimum Spanning Tree: connect everything as cheaply as possible

| | **Kruskal** | **Prim** |
|---|---|---|
| Thinks in | **edges** — sort globally, add if it joins two components | **vertices** — grow one tree, always take the cheapest edge leaving it |
| Needs | **Union-Find** (`DisjointSet/`) for cycle checks | a priority queue |
| Time | O(E log E) | O(E log V) |
| Best for | **sparse** graphs / edge lists | **dense** graphs / adjacency lists |

Both are greedy and both are provably optimal by the **cut property**: the cheapest edge crossing any
partition of the vertices is safe to include in some MST. Kruskal applies it globally, Prim locally.

Union-Find makes Kruskal practical — "would this edge create a cycle?" becomes a near-O(1) check
instead of a graph search.

## Cycle detection differs by graph type — don't mix them up

- **Undirected:** during DFS, reaching an already-visited vertex that is **not your parent** closes a
  cycle.
- **Directed:** you need an edge back to a vertex still **on the current recursion path** (a "back
  edge"). Track three states — unvisited / in-progress / finished. Seeing a *finished* vertex is fine;
  seeing an *in-progress* one is a cycle.

Applying the undirected rule to a directed graph gives wrong answers — a genuinely common bug.

## Topological sort — and why it only works on DAGs

An ordering where every edge `u→v` puts u before v. It exists **if and only if** the graph is acyclic
— a cycle means mutually-dependent tasks with no valid order.

- **Kahn's (BFS):** repeatedly take a vertex with in-degree 0. If vertices remain unplaced at the end,
  there is a cycle — detection comes free.
- **DFS:** push each vertex after its descendants finish, then reverse.

## When to use graphs

Anything expressible as "things and connections": maps and routing, dependency resolution (build
systems, package managers, spreadsheet recalculation), social and recommendation networks, state
machines, scheduling with prerequisites, network flow, garbage-collection reachability.

## When NOT to reach for a graph

If the data is strictly hierarchical with a single root and no cycles, a **tree** is simpler. If it is
purely sequential, use a list. Also beware: many graph algorithms are polynomial, but some graph
*problems* (Travelling Salesman, graph colouring, Hamiltonian path) are **NP-hard** — recognizing
those saves you from hunting for an efficient exact algorithm that does not exist.

## Files in this folder

| File | Solves | Time |
|---|---|---|
| `Graph` | adjacency-list representation | — |
| `BFS` | traversal + unweighted shortest paths | O(V+E) |
| `DFS` | traversal (recursive & iterative), connected components | O(V+E) |
| `dijkstra` | shortest paths, non-negative weights | O((V+E) log V) |
| `bellmanFord` | shortest paths with negatives; negative-cycle detection | O(V·E) |
| `floydWarshall` | all-pairs shortest paths | O(V³) |
| `topologicalSort` | DAG ordering (Kahn's + DFS) | O(V+E) |
| `cycleDetection` | cycles in undirected and directed graphs | O(V+E) |
| `kruskalMST` | MST via sorted edges + Union-Find | O(E log E) |
| `primMST` | MST via a growing tree + heap | O(E log V) |

## Pitfalls

- **Forgetting `visited`** → infinite loops on any cycle.
- Marking visited at **dequeue** rather than **enqueue** in BFS → vertices queued multiple times.
- Assuming BFS gives shortest paths on a **weighted** graph. It does not — use Dijkstra.
- Using Dijkstra with negative edges — it fails silently, producing plausible wrong answers.
- Forgetting that an undirected edge must be added in **both** directions.

## Where graph algorithms are used

GPS and routing (Dijkstra/A*), internet routing protocols (Bellman-Ford underlies RIP), build systems
and package managers (topological sort), social networks (BFS for degrees of separation), web crawling
and PageRank, network design (MST), compilers (dependency and dataflow analysis), and deadlock
detection (cycle detection in a wait-for graph).

## Also in this folder

`numberOfIslands` (a grid *is* a graph) · `bipartiteCheck` (2-colouring; bipartite ⟺ no odd cycle) · `stronglyConnectedComponents` (Kosaraju; why reversing the edges works).
