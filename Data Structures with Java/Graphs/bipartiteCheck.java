// Problem  : Can the vertices be split into TWO groups such that every edge connects a vertex in one
//            group to a vertex in the other? (Equivalently: can the graph be 2-coloured?)
// Approach : BFS/DFS colouring. Give the start vertex colour 0, every neighbour the opposite colour,
//            and so on. A conflict - an edge whose endpoints already share a colour - proves it is
//            not bipartite.
// Intuition: Bipartite means "no odd-length cycle". Colouring alternates along every path, so after
//            an EVEN number of steps you are back to the original colour and after an ODD number you
//            are on the opposite one. An odd cycle therefore forces a vertex to be both colours at
//            once - which is exactly the conflict the algorithm detects.
// Time     : O(V + E) - one traversal   Space: O(V) for the colour array and the queue
// Trade-off: Very cheap for what it proves. The subtlety is remembering to restart the traversal for
//            every unvisited vertex, because a disconnected graph must be bipartite in EVERY
//            component - checking only the component containing vertex 0 is the usual bug.

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class bipartiteCheck {

    static boolean isBipartite(List<List<Integer>> adj) {
        int n = adj.size();
        int[] colour = new int[n];
        Arrays.fill(colour, -1);              // -1 = not yet coloured

        // Restart for each component. Skipping this checks only the first component and wrongly
        // reports true when a LATER component contains an odd cycle.
        for (int start = 0; start < n; start++) {
            if (colour[start] != -1) continue;

            colour[start] = 0;
            Queue<Integer> q = new ArrayDeque<>();
            q.offer(start);

            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : adj.get(u)) {

                    if (colour[v] == -1) {
                        colour[v] = 1 - colour[u];   // flip 0 <-> 1
                        q.offer(v);
                    } else if (colour[v] == colour[u]) {
                        // Both endpoints of this edge want the same colour -> an odd cycle exists.
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static List<List<Integer>> graph(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) { adj.get(e[0]).add(e[1]); adj.get(e[1]).add(e[0]); }
        return adj;
    }

    public static void main(String[] args) {
        // A 4-cycle 0-1-2-3-0. Even cycle -> bipartite ({0,2} and {1,3}).
        System.out.println("square (even cycle) : "
                + isBipartite(graph(4, new int[][]{ {0,1},{1,2},{2,3},{3,0} })));   // true

        // A triangle 0-1-2-0. Odd cycle -> NOT bipartite.
        System.out.println("triangle (odd cycle): "
                + isBipartite(graph(3, new int[][]{ {0,1},{1,2},{2,0} })));          // false

        // A tree has no cycles at all, so it is always bipartite.
        System.out.println("tree                : "
                + isBipartite(graph(5, new int[][]{ {0,1},{0,2},{1,3},{1,4} })));    // true

        // Two components: one fine, one containing a triangle. Only the per-component restart
        // catches this.
        System.out.println("disconnected + odd  : "
                + isBipartite(graph(6, new int[][]{ {0,1}, {2,3},{3,4},{4,2} })));   // false
    }
}

/* -------------------------- WHY ODD CYCLES ARE THE TEST --------------------------
 * Walking a cycle alternates colours at every step. Return to the start after k steps and your
 * colour has flipped k times, so you end on the original colour only when k is EVEN. An odd cycle
 * therefore demands that the starting vertex be both colours - impossible. Conversely, any graph
 * with no odd cycle can always be 2-coloured, so "bipartite" and "no odd cycle" are equivalent.
 *
 * ---------------------------------- WHERE IT IS USED --------------------------------
 *   - MATCHING PROBLEMS: jobs to workers, students to projects, riders to drivers. Bipartite
 *     matching (Hopcroft-Karp, or max-flow) requires the graph to be bipartite in the first place.
 *   - Two-team / two-group assignment: "can these people be split so no two enemies share a team?"
 *   - 2-colouring in scheduling and register allocation.
 *   - Detecting conflicts in constraint graphs, where an odd cycle means the constraints are
 *     unsatisfiable.
 *
 * ------------------------------------- VARIANTS -------------------------------------
 * The same colouring approach with DFS works identically. Note that general k-colouring for k >= 3
 * is NP-complete - it is only k = 2 that is easy, precisely because each vertex's colour is FORCED
 * by its neighbour and there is never a choice to search over.
 * ------------------------------------------------------------------------------------ */
