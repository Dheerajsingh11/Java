// Problem  : Shortest paths from a source even with NEGATIVE edge weights, and detect negative cycles.
// Approach : Relax ALL edges V-1 times; a V-th round that still improves something proves a negative
//            cycle exists.
// Intuition: A shortest path visits at most V-1 edges, so V-1 full relaxation rounds are enough to
//            propagate every shortest distance. If distances can STILL shrink after that, some cycle
//            has negative total weight (infinitely improvable).
// Time     : O(V * E)   Space: O(V)
// Trade-off: Slower than Dijkstra's O((V+E) log V) but handles NEGATIVE edges and detects negative
//            cycles - things Dijkstra cannot. Use it only when negative weights are possible.

import java.util.Arrays;

public class bellmanFord {

    static class Edge { int u, v, w; Edge(int u, int v, int w) { this.u = u; this.v = v; this.w = w; } }

    // Returns dist[] or null if a negative cycle is reachable from src.
    static int[] shortestPaths(int n, Edge[] edges, int src) {
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[src] = 0;

        // Relax every edge V-1 times.
        for (int i = 0; i < n - 1; i++) {
            for (Edge e : edges) {
                if (dist[e.u] != Long.MAX_VALUE && dist[e.u] + e.w < dist[e.v]) {
                    dist[e.v] = dist[e.u] + e.w;   // found a cheaper route to e.v
                }
            }
        }

        // One more pass: any further improvement means a negative cycle.
        for (Edge e : edges) {
            if (dist[e.u] != Long.MAX_VALUE && dist[e.u] + e.w < dist[e.v]) {
                return null;                       // negative cycle detected
            }
        }

        int[] res = new int[n];
        for (int i = 0; i < n; i++) res[i] = (dist[i] == Long.MAX_VALUE) ? Integer.MAX_VALUE : (int) dist[i];
        return res;
    }

    public static void main(String[] args) {
        Edge[] edges = {
            new Edge(0, 1, 4), new Edge(0, 2, 5),
            new Edge(1, 2, -3),                    // a negative edge Dijkstra could not handle
            new Edge(2, 3, 4), new Edge(3, 1, -10) // this creates a negative cycle 1->2->3->1 (-3+4-10)
        };
        System.out.println(Arrays.toString(shortestPaths(4, edges, 0))); // null-cycle case below

        Edge[] ok = { new Edge(0, 1, 4), new Edge(0, 2, 5), new Edge(1, 2, -3), new Edge(2, 3, 4) };
        System.out.println(Arrays.toString(shortestPaths(4, ok, 0))); // [0, 4, 1, 5]
    }
}
