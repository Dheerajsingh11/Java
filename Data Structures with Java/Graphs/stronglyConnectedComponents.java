// Problem  : Find the STRONGLY CONNECTED COMPONENTS of a directed graph - maximal groups where every
//            vertex can reach every other vertex.
// Approach : KOSARAJU's algorithm - two DFS passes. First pass records vertices by FINISH time; then
//            reverse every edge; then DFS in decreasing finish order, and each tree is one SCC.
// Intuition: In a directed graph "u reaches v" does not imply "v reaches u", so connectivity splits
//            into one-way reachability. The trick: a vertex that finishes LAST in the first pass lies
//            in a "source" SCC. Reversing the edges makes that a "sink" SCC, so a DFS from it cannot
//            escape its own component - it collects exactly one SCC and stops. Repeating in
//            decreasing finish order peels off the components one at a time.
// Time     : O(V + E) - two full traversals plus building the reversed graph   Space: O(V + E)
// Trade-off: Kosaraju is the easiest SCC algorithm to understand and implement (two plain DFS runs),
//            at the cost of two passes and an explicit reversed graph. Tarjan's algorithm does it in
//            ONE pass with low-link values - faster in practice but considerably harder to follow.

import java.util.*;

public class stronglyConnectedComponents {

    // PASS 1 - DFS on the original graph, pushing each vertex onto a stack when it FINISHES
    // (after all its descendants). The stack therefore ends up in decreasing finish order.
    static void fillOrder(int u, List<List<Integer>> adj, boolean[] visited, Deque<Integer> stack) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) fillOrder(v, adj, visited, stack);
        }
        stack.push(u);          // pushed AFTER the recursion - this ordering is the whole algorithm
    }

    // PASS 2 - plain DFS on the REVERSED graph, collecting one component.
    static void collect(int u, List<List<Integer>> rev, boolean[] visited, List<Integer> component) {
        visited[u] = true;
        component.add(u);
        for (int v : rev.get(u)) {
            if (!visited[v]) collect(v, rev, visited, component);
        }
    }

    static List<List<Integer>> kosaraju(int n, List<List<Integer>> adj) {
        // Step 1: order vertices by finish time.
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (!visited[i]) fillOrder(i, adj, visited, stack);
        }

        // Step 2: reverse every edge. This is what turns source components into sink components,
        // trapping each DFS inside a single SCC.
        List<List<Integer>> rev = new ArrayList<>();
        for (int i = 0; i < n; i++) rev.add(new ArrayList<>());
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) rev.get(v).add(u);
        }

        // Step 3: DFS the reversed graph in decreasing finish order. Each traversal collects
        // exactly one SCC, because it cannot leave the component.
        Arrays.fill(visited, false);
        List<List<Integer>> sccs = new ArrayList<>();
        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                List<Integer> component = new ArrayList<>();
                collect(u, rev, visited, component);
                Collections.sort(component);        // sorted only to make the output deterministic
                sccs.add(component);
            }
        }
        return sccs;
    }

    public static void main(String[] args) {
        int n = 8;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        int[][] edges = {
            {0,1}, {1,2}, {2,0},        // SCC: {0,1,2} - a directed cycle
            {2,3},                      // one-way link into the next component
            {3,4}, {4,5}, {5,3},        // SCC: {3,4,5}
            {5,6},                      // one-way link
            {6,7}                       // 6 and 7 are each alone (no way back)
        };
        for (int[] e : edges) adj.get(e[0]).add(e[1]);

        List<List<Integer>> sccs = kosaraju(n, adj);
        System.out.println("number of SCCs: " + sccs.size());   // expected: 4
        for (List<Integer> c : sccs) System.out.println("  " + c);
        // expected components: [0, 1, 2], [3, 4, 5], [6], [7]

        // A graph that is entirely one cycle collapses to a single SCC.
        List<List<Integer>> ring = new ArrayList<>();
        for (int i = 0; i < 3; i++) ring.add(new ArrayList<>());
        ring.get(0).add(1); ring.get(1).add(2); ring.get(2).add(0);
        System.out.println("ring SCCs     : " + kosaraju(3, ring));   // [[0, 1, 2]]

        // A DAG has no cycles, so every vertex is its own SCC.
        List<List<Integer>> dag = new ArrayList<>();
        for (int i = 0; i < 3; i++) dag.add(new ArrayList<>());
        dag.get(0).add(1); dag.get(1).add(2);
        System.out.println("DAG SCCs      : " + kosaraju(3, dag));    // [[0], [1], [2]]
    }
}

/* ------------------------ WHY REVERSING THE EDGES WORKS ------------------------
 * Collapse every SCC into a single node and the result is always a DAG (the "condensation") - if two
 * components could reach each other they would be one component.
 *
 * The vertex with the LATEST finish time in pass 1 belongs to a SOURCE component of that DAG - one
 * with no incoming edges. After reversing, it becomes a SINK: nothing leads out of it. A DFS started
 * there is therefore CONFINED to that single component, so it collects precisely one SCC. Remove it
 * and repeat, and the next-latest finishing vertex heads the next component. That confinement is the
 * entire reason the finish-time ordering matters.
 *
 * ------------------------------- WHERE IT IS USED -------------------------------
 *   - 2-SAT: build an implication graph; the formula is satisfiable iff no variable shares an SCC
 *     with its own negation.
 *   - Dead-code and dependency analysis: mutually recursive functions form an SCC.
 *   - Deadlock detection: an SCC in a wait-for graph is a set of mutually blocked processes.
 *   - Web link analysis and social-network clustering.
 *   - Condensing a cyclic graph into a DAG so that topological sort becomes possible.
 * -------------------------------------------------------------------------------- */
