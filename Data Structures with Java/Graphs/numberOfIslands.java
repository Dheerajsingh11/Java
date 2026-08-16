// Problem  : Count connected groups of land cells ('1') in a 2-D grid, where cells connect
//            horizontally and vertically.
// Approach : Scan every cell; each time an unvisited land cell is found, that is a NEW island, so
//            increment the count and flood-fill its entire component so it is never counted again.
// Intuition: This is graph connected-components in disguise. Each land cell is a vertex, adjacency
//            is an edge, and an island is a connected component. The insight worth carrying: a GRID
//            IS A GRAPH - you never build an adjacency list, because a cell's neighbours are
//            computable arithmetically as (r+-1, c) and (r, c+-1).
// Time     : O(rows * cols) - every cell is visited a constant number of times   Space: see below
// Trade-off: DFS is shorter but recurses to O(rows*cols) depth in the worst case (one huge island),
//            which can overflow the stack on a large grid. BFS uses an explicit queue bounded by the
//            frontier width, so it is the safer choice at scale. Both are O(cells) in time.

import java.util.ArrayDeque;
import java.util.Queue;

public class numberOfIslands {

    // Direction vectors - the standard way to express "the four neighbours" without four copies of
    // the same code. Add the diagonals here for 8-directional connectivity.
    private static final int[] DR = { -1, 1, 0, 0 };
    private static final int[] DC = { 0, 0, -1, 1 };

    // ---------- DFS (recursive flood fill) ----------
    static int countDFS(char[][] grid) {
        if (grid.length == 0) return 0;
        char[][] g = copy(grid);            // work on a copy so the caller's grid survives
        int count = 0;

        for (int r = 0; r < g.length; r++) {
            for (int c = 0; c < g[0].length; c++) {
                if (g[r][c] == '1') {
                    count++;                 // a brand-new component
                    sink(g, r, c);           // erase the whole island so it is counted once
                }
            }
        }
        return count;
    }

    static void sink(char[][] g, int r, int c) {
        // Bounds check plus "is this land?" - both handled by one guard.
        if (r < 0 || c < 0 || r >= g.length || c >= g[0].length || g[r][c] != '1') return;

        g[r][c] = '0';                       // mark visited BY SINKING IT - no separate visited array
        for (int d = 0; d < 4; d++) sink(g, r + DR[d], c + DC[d]);
    }

    // ---------- BFS (iterative, stack-safe) ----------
    static int countBFS(char[][] grid) {
        if (grid.length == 0) return 0;
        char[][] g = copy(grid);
        int rows = g.length, cols = g[0].length, count = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (g[r][c] != '1') continue;

                count++;
                Queue<int[]> q = new ArrayDeque<>();
                q.offer(new int[]{ r, c });
                g[r][c] = '0';               // mark on ENQUEUE, not on dequeue - otherwise a cell can
                                             // be queued several times before it is processed

                while (!q.isEmpty()) {
                    int[] cell = q.poll();
                    for (int d = 0; d < 4; d++) {
                        int nr = cell[0] + DR[d], nc = cell[1] + DC[d];
                        if (nr >= 0 && nc >= 0 && nr < rows && nc < cols && g[nr][nc] == '1') {
                            g[nr][nc] = '0';
                            q.offer(new int[]{ nr, nc });
                        }
                    }
                }
            }
        }
        return count;
    }

    static char[][] copy(char[][] g) {
        char[][] out = new char[g.length][];
        for (int i = 0; i < g.length; i++) out[i] = g[i].clone();
        return out;
    }

    public static void main(String[] args) {
        char[][] grid = {
            { '1', '1', '0', '0', '0' },
            { '1', '1', '0', '0', '0' },
            { '0', '0', '1', '0', '0' },
            { '0', '0', '0', '1', '1' }
        };
        System.out.println("islands DFS : " + countDFS(grid));   // 3
        System.out.println("islands BFS : " + countBFS(grid));   // 3

        char[][] allWater = { { '0', '0' }, { '0', '0' } };
        System.out.println("all water   : " + countDFS(allWater));  // 0

        char[][] allLand = { { '1', '1' }, { '1', '1' } };
        System.out.println("all land    : " + countDFS(allLand));   // 1 (one connected island)

        // Diagonals do NOT connect under 4-directional adjacency.
        char[][] diag = { { '1', '0' }, { '0', '1' } };
        System.out.println("diagonal    : " + countDFS(diag));      // 2
    }
}

/* --------------------------- THE GRID-AS-GRAPH IDEA ---------------------------
 * No adjacency list is ever constructed. A cell's neighbours are implied by its coordinates, so the
 * "graph" exists only as arithmetic. This applies to a whole family of problems:
 *   - Number of islands / provinces / connected components
 *   - Flood fill (a paint bucket tool)
 *   - Shortest path in a maze -> BFS, because BFS gives fewest-step paths (see BFS.java)
 *   - Rotting oranges, walls and gates -> MULTI-SOURCE BFS: seed the queue with every source at
 *     once, and the levels expand simultaneously
 *   - Surrounded regions, max area of island - the same sweep with a different accumulator
 *
 * ------------------------------ THE MARKING RULE -------------------------------
 * Marking cells as visited is what keeps this linear; without it, adjacent cells would revisit each
 * other forever. Sinking land to '0' avoids a separate visited array, but MUTATES the grid - hence
 * the defensive copy here. In BFS, always mark when ENQUEUEING rather than when dequeuing, or the
 * same cell can enter the queue multiple times before being processed.
 *
 * ALTERNATIVE: Union-Find also solves this (union each land cell with its right and down land
 * neighbours, then count distinct roots). It is the better tool when edges arrive INCREMENTALLY -
 * for example "count islands after each added land cell". See DisjointSet/.
 * ------------------------------------------------------------------------------- */
