// Problem  : Find a Minimum Spanning Tree (MST) - a cheapest set of edges connecting all vertices
//            with no cycle.
// Approach : Kruskal's - sort all edges by weight and add each edge if it joins two DIFFERENT
//            components (checked with Union-Find), skipping edges that would form a cycle.
// Intuition: Greedily taking the globally cheapest safe edge is optimal (cut property). Union-Find
//            makes "would this edge create a cycle?" an O(alpha) check.
// Time     : O(E log E) dominated by sorting the edges   Space: O(V + E)
// Trade-off: Edge-centric and simple; ideal for SPARSE graphs given as an edge list. Prim's is often
//            better for DENSE graphs. Depends on the DisjointSet structure (see DisjointSet folder).

import java.util.Arrays;

public class kruskalMST {

    static class Edge implements Comparable<Edge> {
        int u, v, w;
        Edge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; }
        public int compareTo(Edge o) { return Integer.compare(this.w, o.w); } // sort by weight asc
    }

    // Minimal inline Union-Find (see DisjointSet/DisjointSet.java for the fully commented version).
    static int[] parent;
    static int find(int x) { return parent[x] == x ? x : (parent[x] = find(parent[x])); }
    static boolean union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        parent[rb] = ra;
        return true;
    }

    static int mstWeight(int n, Edge[] edges) {
        Arrays.sort(edges);                 // cheapest edges first
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        int total = 0, used = 0;
        for (Edge e : edges) {
            if (union(e.u, e.v)) {          // connects two separate components -> safe to add
                total += e.w;
                used++;
                if (used == n - 1) break;   // an MST has exactly V-1 edges
            }
            // else: e.u and e.v already connected -> adding this edge would make a cycle -> skip
        }
        return total;
    }

    public static void main(String[] args) {
        Edge[] edges = {
            new Edge(0, 1, 10), new Edge(0, 2, 6), new Edge(0, 3, 5),
            new Edge(1, 3, 15), new Edge(2, 3, 4)
        };
        System.out.println("MST weight: " + mstWeight(4, edges)); // 19  (edges 2-3, 0-3, 0-1)
    }
}
