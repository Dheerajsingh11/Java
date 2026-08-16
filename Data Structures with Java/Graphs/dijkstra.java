// Problem  : Shortest path from a source to all vertices in a graph with NON-NEGATIVE edge weights.
// Approach : Greedy with a min-priority-queue: repeatedly settle the nearest unsettled vertex and
//            relax its outgoing edges.
// Intuition: With no negative edges, once we pick the closest unsettled vertex, no later (farther)
//            vertex can offer a shorter route to it - so its distance is final. The heap always hands
//            us that closest vertex next.
// Time     : O((V + E) log V) with a binary heap   Space: O(V + E)
// Trade-off: Fast and optimal for non-negative weights, but FAILS with negative edges (use
//            Bellman-Ford). Uses a priority queue; storing (dist, vertex) pairs and skipping stale
//            entries keeps it simple.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class dijkstra {

    // Edge to a neighbour with a weight.
    static class Edge { int to, w; Edge(int to, int w) { this.to = to; this.w = w; } }

    static int[] shortestPaths(List<List<Edge>> adj, int src) {
        int n = adj.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);   // "infinity" = unreached
        dist[src] = 0;

        // Min-heap ordered by current distance: {distance, vertex}.
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{ 0, src });

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0], u = top[1];
            if (d > dist[u]) continue;          // stale entry (a better dist was found already) -> skip

            for (Edge e : adj.get(u)) {
                // Relax: is going u -> e.to cheaper than the best known to e.to?
                if (dist[u] + e.w < dist[e.to]) {
                    dist[e.to] = dist[u] + e.w;
                    pq.offer(new int[]{ dist[e.to], e.to });
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        int n = 5;
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        // directed weighted edges
        adj.get(0).add(new Edge(1, 4));
        adj.get(0).add(new Edge(2, 1));
        adj.get(2).add(new Edge(1, 2));
        adj.get(1).add(new Edge(3, 1));
        adj.get(2).add(new Edge(3, 5));
        adj.get(3).add(new Edge(4, 3));

        System.out.println(Arrays.toString(shortestPaths(adj, 0)));
        // 0->2 (1), 2->1 (1+2=3), 1->3 (3+1=4), 3->4 (4+3=7): [0, 3, 1, 4, 7]
    }
}
