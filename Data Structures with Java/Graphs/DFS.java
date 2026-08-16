// Problem  : Traverse a graph in depth-first order (go deep before wide), both recursively and
//            iteratively.
// Approach : Recursion (implicit call stack) or an explicit stack; mark visited to avoid cycles.
// Intuition: DFS dives down one path as far as possible, then backtracks. It naturally explores
//            connected components and underlies cycle detection, topological sort, and path finding.
// Time     : O(V + E)   Space: O(V) for visited + O(depth) stack (recursion can overflow on deep graphs)
// Trade-off: DFS uses less memory than BFS on wide graphs but does NOT give shortest paths. The
//            iterative version avoids stack overflow on very deep graphs.

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DFS {

    // ---- Recursive DFS ----
    static void dfsRecursive(List<List<Integer>> adj, int v, boolean[] visited, List<Integer> order) {
        visited[v] = true;
        order.add(v);                       // visit on entry (preorder)
        for (int nb : adj.get(v)) {
            if (!visited[nb]) dfsRecursive(adj, nb, visited, order); // dive into each unvisited neighbour
        }
    }

    // ---- Iterative DFS with an explicit stack ----
    static List<Integer> dfsIterative(List<List<Integer>> adj, int start) {
        int n = adj.size();
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (visited[v]) continue;       // may be pushed multiple times; skip if already done
            visited[v] = true;
            order.add(v);
            // Push neighbours; they will be explored in reverse push order (LIFO).
            for (int nb : adj.get(v)) {
                if (!visited[nb]) stack.push(nb);
            }
        }
        return order;
    }

    // Count connected components in an undirected graph (a common DFS use).
    static int countComponents(List<List<Integer>> adj) {
        int n = adj.size(), components = 0;
        boolean[] visited = new boolean[n];
        for (int v = 0; v < n; v++) {
            if (!visited[v]) {
                components++;                       // a new unvisited vertex = a new component
                dfsRecursive(adj, v, visited, new ArrayList<>());
            }
        }
        return components;
    }

    public static void main(String[] args) {
        Graph g = new Graph(6, false);
        g.addEdge(0, 1); g.addEdge(0, 2); g.addEdge(1, 3);
        g.addEdge(2, 3); g.addEdge(4, 5); // 4-5 form a separate component

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < g.size(); i++) adj.add(g.neighbours(i));

        List<Integer> rec = new ArrayList<>();
        dfsRecursive(adj, 0, new boolean[g.size()], rec);
        System.out.println("DFS recursive: " + rec);            // e.g. [0, 1, 3, 2]
        System.out.println("DFS iterative: " + dfsIterative(adj, 0));
        System.out.println("components   : " + countComponents(adj)); // 2
    }
}
