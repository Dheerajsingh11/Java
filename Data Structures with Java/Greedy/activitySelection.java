// Problem  : Select the maximum number of non-overlapping activities given their start/finish times.
// Approach : GREEDY - sort activities by FINISH time, then repeatedly pick the next activity that
//            starts after the last chosen one finishes.
// Intuition: Finishing as early as possible leaves the most room for future activities. So among all
//            compatible choices, the one that ends soonest is always safe to take (exchange argument).
// Time     : O(n log n) for the sort   Space: O(1) beyond the sort
// Trade-off: Greedy is optimal HERE (provably), far simpler/faster than a DP search over subsets. The
//            key is sorting by finish time - sorting by start time or duration gives wrong answers.

import java.util.Arrays;

public class activitySelection {

    static int maxActivities(int[] start, int[] finish) {
        int n = start.length;
        // Pair up (start, finish) and sort by finish time ascending.
        int[][] acts = new int[n][2];
        for (int i = 0; i < n; i++) acts[i] = new int[]{ start[i], finish[i] };
        Arrays.sort(acts, (a, b) -> Integer.compare(a[1], b[1])); // by finish time

        int count = 0;
        int lastFinish = Integer.MIN_VALUE;   // when the previously chosen activity ended
        for (int[] a : acts) {
            if (a[0] >= lastFinish) {          // starts after the last one finished -> compatible
                count++;
                lastFinish = a[1];             // this activity is now the "last chosen"
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] start  = { 1, 3, 0, 5, 8, 5 };
        int[] finish = { 2, 4, 6, 7, 9, 9 };
        System.out.println(maxActivities(start, finish)); // 4  -> (1,2),(3,4),(5,7),(8,9)
    }
}
