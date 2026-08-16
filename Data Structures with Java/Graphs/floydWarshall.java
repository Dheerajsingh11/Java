// Problem  : Shortest distances between ALL pairs of vertices (any signs, no negative cycles).
// Approach : Dynamic programming over an intermediate vertex k: allow paths that may pass through
//            vertices 0..k, improving the distance matrix in place.
// Intuition: dist[i][j] using intermediates up to k is the better of "not using k" and "going
//            i -> k -> j". Sweeping k from 0..V-1 lets every vertex become a potential waypoint.
// Time     : O(V^3)   Space: O(V^2)
// Trade-off: Simple triple loop giving ALL-pairs distances - great for small dense graphs. For a
//            single source, Dijkstra/Bellman-Ford are cheaper. Cannot handle negative CYCLES.

public class floydWarshall {

    static final int INF = 1_000_000_000; // large but safe from overflow when added once

    static int[][] allPairs(int[][] w) {
        int n = w.length;
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) dist[i] = w[i].clone(); // start from the direct edges

        // Try each vertex k as an intermediate waypoint.
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Only relax if both legs are reachable (avoids INF + something overflow/false path).
                    if (dist[i][k] < INF && dist[k][j] < INF && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        int INF = floydWarshall.INF;
        int[][] w = {
            { 0,   5,   INF, 10 },
            { INF, 0,   3,   INF },
            { INF, INF, 0,   1 },
            { INF, INF, INF, 0 }
        };
        int[][] d = allPairs(w);
        // 0->1->2->3 = 5+3+1 = 9 (better than the direct 10)
        System.out.println("dist[0][3] = " + d[0][3]); // 9
        System.out.println("dist[0][2] = " + d[0][2]); // 8 (0->1->2)
    }
}
