// Problem  : Order the vertices of a DIRECTED ACYCLIC GRAPH (DAG) so every edge u->v has u before v.
// Approach : Two classic methods. Kahn's algorithm (BFS on in-degrees) and DFS with a finish-order
//            stack. Both also reveal a cycle (then no valid ordering exists).
// Intuition: A vertex can come first only if nothing must precede it (in-degree 0). Removing it frees
//            its successors. DFS instead pushes a vertex AFTER all its descendants finish, so
//            reversing finish order yields a valid topological order.
// Time     : O(V + E)   Space: O(V)
// Trade-off: Kahn's naturally detects cycles (leftover vertices) and is queue-based; DFS is compact
//            but needs care with the recursion stack. Only DAGs have a topological order.

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class topologicalSort {

    // ---- Kahn's algorithm (BFS on in-degrees) ----
    static List<Integer> kahn(List<List<Integer>> adj) {
        int n = adj.size();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (int v : adj.get(u)) indeg[v]++; // count incoming edges

        Queue<Integer> q = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (indeg[v] == 0) q.offer(v);       // sources first

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int v : adj.get(u)) {
                if (--indeg[v] == 0) q.offer(v); // removing u may expose new sources
            }
        }
        // If we could not place every vertex, the graph has a cycle.
        return order.size() == n ? order : null;
    }

    // ---- DFS-based: push each vertex after its subtree finishes, then reverse ----
    static void dfs(int u, List<List<Integer>> adj, boolean[] visited, Deque<Integer> stack) {
        visited[u] = true;
        for (int v : adj.get(u)) if (!visited[v]) dfs(v, adj, visited, stack);
        stack.push(u);                          // u finishes after all it can reach -> push last
    }

    static List<Integer> dfsTopo(List<List<Integer>> adj) {
        int n = adj.size();
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (!visited[v]) dfs(v, adj, visited, stack);
        return new ArrayList<>(stack);          // popping the stack (top->bottom) is the topo order
    }

    public static void main(String[] args) {
        // DAG: 5->2, 5->0, 4->0, 4->1, 2->3, 3->1
        int n = 6;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        adj.get(5).add(2); adj.get(5).add(0);
        adj.get(4).add(0); adj.get(4).add(1);
        adj.get(2).add(3); adj.get(3).add(1);

        System.out.println("Kahn : " + kahn(adj));      // a valid order, e.g. [4, 5, 2, 0, 3, 1]
        System.out.println("DFS  : " + dfsTopo(adj));   // another valid order, e.g. [5, 4, 2, 3, 1, 0]
    }
}
