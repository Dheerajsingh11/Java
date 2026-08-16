package Questions;

// Problem  : Given guests' arrival and departure times, find the MAXIMUM number present at once.
// Approach : SWEEP LINE - sort arrivals and departures INDEPENDENTLY, then walk both lists in time
//            order, incrementing a counter on an arrival and decrementing on a departure.
// Intuition: The key realization is that we do NOT need to know which departure belongs to which
//            guest - only how many events of each type have occurred by a given moment. So the two
//            time lists can be sorted separately and merged like a two-pointer walk. The running
//            counter is the live occupancy, and its peak is the answer.
// Time     : O(n log n) - the two sorts dominate; the sweep is O(n)
// Space    : O(1) extra (sorting the given arrays in place)
// Trade-off: Far better than the naive O(n^2) of checking every guest against every interval. The
//            "decouple the endpoints and sweep" idea generalizes to many interval problems.
// Applications: room/server capacity planning, peak concurrent users, train platform allocation,
//               and the classic "minimum meeting rooms required" question.

import java.util.Arrays;

public class meetingMaxGuest {
    public static void main(String[] args) {
        int arr[] = { 800, 700, 600, 500 };   // arrival times
        int dep[] = { 840, 820, 830, 530 };   // departure times (NOT aligned with arr by index)
        System.out.println("Maximum guests: " + maxGuests(arr, dep, arr.length)); // expected: 3
    }

    static int maxGuests(int arr[], int dep[], int n) {
        // Sorting the two arrays SEPARATELY deliberately breaks the guest-to-guest pairing. That is
        // sound here because occupancy depends only on the COUNT of events so far, not on identity.
        Arrays.sort(arr);
        Arrays.sort(dep);

        // Start having processed the first arrival: one guest is present.
        int i = 1, j = 0, res = 1, curr = 1;

        while (i < n && j < n) {
            // '<=' treats a simultaneous arrival and departure as the arrival happening first,
            // which counts the overlap. Use '<' if a guest leaving at time t frees the slot for
            // someone arriving at exactly t.
            if (arr[i] <= dep[j]) {
                curr++;    // next event in time order is an ARRIVAL
                i++;
            } else {
                curr--;    // next event is a DEPARTURE
                j++;
            }
            res = Math.max(res, curr);   // track the peak occupancy
        }
        return res;
        // Once arrivals are exhausted, occupancy can only fall - so there is no need to drain the
        // remaining departures.
    }
}
