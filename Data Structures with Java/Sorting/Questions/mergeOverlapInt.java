package Questions;

import java.util.Arrays;
import java.util.Comparator;

// Basic solution to merge the overlapping intervals 

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
        Interval arr[] = { new Interval(1, 3), new Interval(2, 4), new Interval(5, 7), new Interval(6, 8) };
        mergeInverval(arr, arr.length);
    }

    static void mergeInverval(Interval arr[], int n) {
        Arrays.sort(arr, new Comparator<Interval>() {
            public int compare(Interval o1, Interval o2) {
                return o1.start - o2.start;
            }
        });
        int res = 0;
        for (int i = 1; i < n; i++) {
            if (arr[res].end >= arr[i].start) {
                arr[res].end = Math.max(arr[res].end, arr[i].end);
                arr[res].start = Math.min(arr[res].start, arr[i].start);
            } else {
                res++;
                arr[res] = arr[i];
            }
        }
        for (int i = 0; i <= res; i++) {
            System.out.print(arr[i].start + " " + arr[i].end + " ");
        }
    }
}

// Time complexity: O(n)