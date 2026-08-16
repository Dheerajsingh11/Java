// Problem  : A rat starts at the top-left of a grid and must reach the bottom-right, moving only
//            through open cells (1). Find whether a path exists (and mark one).
// Approach : Backtracking DFS - from a cell, try each direction; if it leads to a solution keep it,
//            otherwise undo the step and try the next direction.
// Intuition: Explore one direction fully; if it dead-ends, retreat and try another. Marking a cell as
//            part of the current path prevents walking in circles; unmarking on failure frees it for
//            other paths.
// Time     : O(4^(R*C)) worst case, heavily pruned by walls/visited   Space: O(R*C) recursion + marks
// Trade-off: Backtracking naturally handles arbitrary obstacle layouts; BFS would find the SHORTEST
//            path but this simpler DFS just needs A path. Visited-marking is essential to terminate.

public class ratInMaze {

    // Try to reach (R-1, C-1) from (r, c). 'sol' records the chosen path (1 = on the path).
    static boolean solve(int[][] maze, int r, int c, int[][] sol) {
        int R = maze.length, C = maze[0].length;

        // Out of bounds, a wall, or already on the current path -> not a valid step.
        if (r < 0 || c < 0 || r >= R || c >= C || maze[r][c] == 0 || sol[r][c] == 1) return false;

        sol[r][c] = 1;                              // choose: step onto this cell

        if (r == R - 1 && c == C - 1) return true;  // reached the destination

        // Explore the four directions; the first that succeeds fixes the path.
        if (solve(maze, r + 1, c, sol)) return true; // down
        if (solve(maze, r, c + 1, sol)) return true; // right
        if (solve(maze, r - 1, c, sol)) return true; // up
        if (solve(maze, r, c - 1, sol)) return true; // left

        sol[r][c] = 0;                              // un-choose (backtrack): this cell led nowhere
        return false;
    }

    public static void main(String[] args) {
        int[][] maze = {
            { 1, 0, 0, 0 },
            { 1, 1, 0, 1 },
            { 0, 1, 0, 0 },
            { 1, 1, 1, 1 }
        };
        int[][] sol = new int[maze.length][maze[0].length];
        if (solve(maze, 0, 0, sol)) {
            System.out.println("Path found:");
            for (int[] row : sol) System.out.println(java.util.Arrays.toString(row));
        } else {
            System.out.println("No path");
        }
        // A valid path exists down the left column, right along the bottom row.
    }
}
