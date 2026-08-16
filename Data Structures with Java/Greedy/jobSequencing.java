// Problem  : Job sequencing with deadlines - each job has a deadline and a profit, takes one unit of
//            time, and earns its profit only if finished by its deadline. Maximize total profit.
// Approach : GREEDY - sort jobs by profit descending; schedule each in the LATEST free slot at or
//            before its deadline.
// Intuition: Take the most profitable jobs first. Placing a job as late as its deadline allows keeps
//            earlier slots free for other jobs whose deadlines are tighter.
// Time     : O(n^2) with a simple slot array (O(n log n) with a disjoint-set)   Space: O(maxDeadline)
// Trade-off: Greedy by profit is optimal for unit-time jobs. The naive slot search is O(n·d); a
//            Union-Find "next free slot" trick (see DisjointSet/) speeds it up for large deadlines.

import java.util.Arrays;

public class jobSequencing {

    static int[] schedule(int[][] jobs) { // jobs[i] = { deadline, profit }
        // Sort by profit descending so we consider the most valuable jobs first.
        Arrays.sort(jobs, (a, b) -> Integer.compare(b[1], a[1]));

        int maxDeadline = 0;
        for (int[] j : jobs) maxDeadline = Math.max(maxDeadline, j[0]);

        boolean[] slot = new boolean[maxDeadline + 1]; // slot[t] used? (1-indexed time units)
        int countDone = 0, totalProfit = 0;

        for (int[] j : jobs) {
            // Place this job in the latest free slot <= its deadline.
            for (int t = j[0]; t >= 1; t--) {
                if (!slot[t]) {
                    slot[t] = true;
                    countDone++;
                    totalProfit += j[1];
                    break;               // scheduled -> move to the next job
                }
            }
            // If no slot was free by the deadline, the job is skipped (its profit is forfeited).
        }
        return new int[]{ countDone, totalProfit };
    }

    public static void main(String[] args) {
        // { deadline, profit }
        int[][] jobs = { {2,100}, {1,19}, {2,27}, {1,25}, {3,15} };
        int[] res = schedule(jobs);
        System.out.println("jobs done = " + res[0] + ", profit = " + res[1]); // 3 jobs, profit 142
        // Chosen: profit 100 (deadline 2), 27 (deadline 2 -> slot 1), 15 (deadline 3).
    }
}
