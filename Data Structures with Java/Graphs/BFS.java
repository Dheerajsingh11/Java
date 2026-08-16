// Problem  : Traverse/explore a graph in breadth-first order and find shortest paths (in edges) on
//            an UNWEIGHTED graph.
// Approach : Use a queue. Visit a start vertex, then all its neighbours, then their neighbours, level
//            by level, marking visited to avoid revisiting.
// Intuition: BFS expands outward in rings of increasing distance, so the first time it reaches a
//            vertex is necessarily via a shortest (fewest-edge) path.
// Time     : O(V + E) - each vertex enqueued once, each edge examined once   Space: O(V) for queue+visited
// Trade-off: BFS gives shortest paths on UNWEIGHTED graphs for free; for WEIGHTED graphs you need
//            Dijkstra. Uses more memory than DFS on wide graphs (the frontier can be large).

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BFS {

    // Standard BFS order from 'start'.
    static List<Integer> bfs(List<List<Integer>> adj, int start) {
        int n = adj.size();
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();
        Queue<Integer> q = new ArrayDeque<>();

        visited[start] = true;         // mark BEFORE enqueue so a vertex is never queued twice
        q.offer(start);
        while (!q.isEmpty()) {
            int v = q.poll();
            order.add(v);              // "visit" v
            for (int nb : adj.get(v)) {
                if (!visited[nb]) {
                    visited[nb] = true;
                    q.offer(nb);
                }
            }
        }
        return order;
    }

    // Shortest distance (edge count) from start to every vertex; -1 if unreachable.
    static int[] shortestDistances(List<List<Integer>> adj, int start) {
        int n = adj.size();
        int[] dist = new int[n];
        java.util.Arrays.fill(dist, -1);
        Queue<Integer> q = new ArrayDeque<>();
        dist[start] = 0;
        q.offer(start);
        while (!q.isEmpty()) {
            int v = q.poll();
            for (int nb : adj.get(v)) {
                if (dist[nb] == -1) {        // first visit = shortest distance
                    dist[nb] = dist[v] + 1;  // one more edge than the parent
                    q.offer(nb);
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        // Build a small undirected graph via the Graph class.
        Graph g = new Graph(6, false);
        g.addEdge(0, 1); g.addEdge(0, 2); g.addEdge(1, 3);
        g.addEdge(2, 3); g.addEdge(3, 4); g.addEdge(4, 5);

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < g.size(); i++) adj.add(g.neighbours(i));

        System.out.println("BFS order: " + bfs(adj, 0));                 // e.g. [0, 1, 2, 3, 4, 5]
        System.out.println("distances: " + java.util.Arrays.toString(shortestDistances(adj, 0)));
        // [0, 1, 1, 2, 3, 4]
    }
}
