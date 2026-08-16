// Problem  : Represent a graph so we can add edges and list each vertex's neighbours efficiently.
// Approach : Adjacency LIST - an array of lists, where adj[v] holds v's neighbours. Contrast noted
//            against the adjacency MATRIX.
// Intuition: Most real graphs are SPARSE (few edges per vertex). Storing only the edges that exist
//            (lists) uses O(V+E) space and makes "who are v's neighbours?" a direct O(degree) scan.
// Time     : addEdge O(1); iterate neighbours of v O(degree(v))   Space: O(V + E)
// Trade-off: Adjacency list = O(V+E) space, great for sparse graphs and traversal. Adjacency matrix
//            = O(V^2) space but O(1) edge-existence checks - better for dense graphs. This class is
//            reused by the BFS/DFS/shortest-path files in this folder.

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private final int V;                 // number of vertices (labelled 0..V-1)
    private final List<List<Integer>> adj;
    private final boolean directed;

    Graph(int V, boolean directed) {
        this.V = V;
        this.directed = directed;
        adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>()); // one empty list per vertex
    }

    // Add an edge u-v. For an undirected graph, record it in BOTH directions.
    void addEdge(int u, int v) {
        adj.get(u).add(v);
        if (!directed) adj.get(v).add(u);
    }

    List<Integer> neighbours(int v) { return adj.get(v); }
    int size() { return V; }

    void print() {
        for (int v = 0; v < V; v++) {
            System.out.println(v + " -> " + adj.get(v));
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph(5, false); // undirected graph with 5 vertices
        g.addEdge(0, 1);
        g.addEdge(0, 4);
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(3, 4);
        g.print();
        // 0 -> [1, 4]
        // 1 -> [0, 2, 3]
        // 2 -> [1]
        // 3 -> [1, 4]
        // 4 -> [0, 3]
    }
}
