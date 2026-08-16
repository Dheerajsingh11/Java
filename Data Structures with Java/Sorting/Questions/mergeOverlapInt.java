package Questions;

// Problem  : Merge all OVERLAPPING intervals. e.g. [1,3],[2,4],[5,7],[6,8] -> [1,4],[5,8]
// Approach : SORT the intervals by start time, then sweep once, extending the current interval
//            whenever the next one overlaps it, otherwise starting a new one.
// Intuition: Sorting by start is what makes a single pass sufficient: once ordered, any interval that
//            overlaps the current one must begin before the current one ends. So we only ever need to
//            compare against the LAST kept interval, never against all previous ones.
// Time     : O(n log n) - dominated by the sort; the sweep itself is O(n)
// Space    : O(1) extra - merging happens in place within the array (plus the sort's own overhead)
// Trade-off: Sorting as a preprocessing step again turns an O(n^2) all-pairs comparison into a linear
//            sweep. Without sorting you would have to repeatedly re-scan for overlaps.
// Applications: calendar/meeting consolidation, CPU or bandwidth scheduling, merging IP ranges,
//               genome interval analysis.

import java.util.Arrays;
import java.util.Comparator;

class Interval {
    int start;
    int end;

    Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
}

public class mergeOverlapInt {
    public static void main(String[] args) {
        Interval arr[] = { new Interval(1, 3), new Interval(2, 4),
                           new Interval(5, 7), new Interval(6, 8) };
        mergeInverval(arr, arr.length);
        System.out.println();      // expected: [1,4] [5,8]
    }

    static void mergeInverval(Interval arr[], int n) {
        // Sort by START time - the precondition that makes the one-pass sweep valid.
        // (Modern equivalent: Arrays.sort(arr, Comparator.comparingInt(o -> o.start));)
        Arrays.sort(arr, new Comparator<Interval>() {
            public int compare(Interval o1, Interval o2) {
                return o1.start - o2.start;   // note: subtraction can overflow for extreme values;
                                              // Integer.compare(o1.start, o2.start) is safer
            }
        });

        int res = 0;    // index of the last KEPT (possibly merged) interval

        for (int i = 1; i < n; i++) {
            // Overlap test: the current interval ends at or after the next one starts. Valid only
            // because sorting guarantees arr[i].start >= arr[res].start.
            if (arr[res].end >= arr[i].start) {
                // Absorb interval i by extending the kept interval to cover both.
                arr[res].end = Math.max(arr[res].end, arr[i].end);
                arr[res].start = Math.min(arr[res].start, arr[i].start);
            } else {
                // No overlap - interval i begins a new merged group.
                res++;
                arr[res] = arr[i];
            }
        }

        for (int i = 0; i <= res; i++) {
            System.out.print("[" + arr[i].start + "," + arr[i].end + "] ");
        }
    }
}
