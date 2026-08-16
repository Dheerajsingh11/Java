// Problem  : Find a Minimum Spanning Tree (MST) - cheapest edges connecting all vertices, no cycle.
// Approach : Prim's - grow the tree from one vertex, repeatedly adding the cheapest edge that leaves
//            the current tree, using a min-priority-queue of candidate edges.
// Intuition: Keep a "cut" between the tree and the rest; the cheapest edge crossing that cut is
//            always safe to add (cut property). The heap always hands us that cheapest crossing edge.
// Time     : O(E log V) with a binary heap   Space: O(V + E)
// Trade-off: Vertex-centric; often preferred for DENSE graphs given as adjacency lists, where
//            Kruskal's edge sort is costlier. Same MST weight as Kruskal's (MST cost is unique when
//            weights are distinct).

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class primMST {

    static class Edge { int to, w; Edge(int to, int w) { this.to = to; this.w = w; } }

    static int mstWeight(int n, List<List<Edge>> adj, int start) {
        boolean[] inTree = new boolean[n];  // vertices already pulled into the MST
        // Min-heap of {weight, vertex} candidate crossings.
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{ 0, start });    // start vertex, zero cost to include it

        int total = 0, count = 0;
        while (!pq.isEmpty() && count < n) {
            int[] top = pq.poll();
            int w = top[0], u = top[1];
            if (inTree[u]) continue;        // already added via a cheaper edge -> stale, skip

            inTree[u] = true;               // add u to the tree
            total += w;
            count++;
            for (Edge e : adj.get(u)) {     // offer all edges leaving u as new candidates
                if (!inTree[e.to]) pq.offer(new int[]{ e.w, e.to });
            }
        }
        return total;
    }

    public static void main(String[] args) {
        int n = 4;
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        // undirected: add each edge both ways
        int[][] es = { {0,1,10},{0,2,6},{0,3,5},{1,3,15},{2,3,4} };
        for (int[] e : es) { adj.get(e[0]).add(new Edge(e[1], e[2])); adj.get(e[1]).add(new Edge(e[0], e[2])); }

        System.out.println("MST weight: " + mstWeight(n, adj, 0)); // 19 (same as Kruskal's)
    }
}
