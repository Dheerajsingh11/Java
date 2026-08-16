// Problem  : Detect whether a graph contains a cycle - handled differently for undirected vs directed.
// Approach : Undirected - DFS and look for a visited neighbour that is NOT the parent. Directed - DFS
//            tracking vertices on the CURRENT recursion path (a "back edge" to one of them = cycle).
// Intuition: Undirected: revisiting any already-visited vertex (other than where we came from) closes
//            a loop. Directed: a cycle needs an edge back to an ancestor still on the active path, so
//            we mark vertices "in progress" and flag an edge into one of them.
// Time     : O(V + E)   Space: O(V)
// Trade-off: The two graph types genuinely need different rules - using the undirected rule on a
//            directed graph gives wrong answers. Cycle detection underpins deadlock detection and
//            topological-sort validity.

import java.util.ArrayList;
import java.util.List;

public class cycleDetection {

    // ---- Undirected: a visited non-parent neighbour means a cycle ----
    static boolean undirectedDFS(int u, int parent, List<List<Integer>> adj, boolean[] visited) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                if (undirectedDFS(v, u, adj, visited)) return true;
            } else if (v != parent) {
                return true;                    // reached an already-visited vertex we didn't come from
            }
        }
        return false;
    }
    static boolean hasCycleUndirected(List<List<Integer>> adj) {
        int n = adj.size();
        boolean[] visited = new boolean[n];
        for (int v = 0; v < n; v++)             // check every component
            if (!visited[v] && undirectedDFS(v, -1, adj, visited)) return true;
        return false;
    }

    // ---- Directed: back edge to a vertex on the current path ----
    // state: 0 = unvisited, 1 = in progress (on the recursion stack), 2 = fully done.
    static boolean directedDFS(int u, List<List<Integer>> adj, int[] state) {
        state[u] = 1;                           // enter: mark as on the active path
        for (int v : adj.get(u)) {
            if (state[v] == 1) return true;     // edge into an in-progress vertex = cycle
            if (state[v] == 0 && directedDFS(v, adj, state)) return true;
        }
        state[u] = 2;                           // leave: fully processed, no longer on the path
        return false;
    }
    static boolean hasCycleDirected(List<List<Integer>> adj) {
        int n = adj.size();
        int[] state = new int[n];
        for (int v = 0; v < n; v++)
            if (state[v] == 0 && directedDFS(v, adj, state)) return true;
        return false;
    }

    public static void main(String[] args) {
        // Undirected with a cycle 0-1-2-0
        List<List<Integer>> u = new ArrayList<>();
        for (int i = 0; i < 4; i++) u.add(new ArrayList<>());
        int[][] ue = { {0,1},{1,2},{2,0},{2,3} };
        for (int[] e : ue) { u.get(e[0]).add(e[1]); u.get(e[1]).add(e[0]); }
        System.out.println("undirected has cycle: " + hasCycleUndirected(u)); // true

        // Directed acyclic: 0->1->2, 0->2
        List<List<Integer>> d = new ArrayList<>();
        for (int i = 0; i < 3; i++) d.add(new ArrayList<>());
        d.get(0).add(1); d.get(1).add(2); d.get(0).add(2);
        System.out.println("directed has cycle : " + hasCycleDirected(d)); // false
        d.get(2).add(0);                                                    // add back edge -> cycle
        System.out.println("directed has cycle : " + hasCycleDirected(d)); // true
    }
}
