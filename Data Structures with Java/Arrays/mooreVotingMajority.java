// Problem  : Find the MAJORITY element (appears more than n/2 times), if one exists.
// Approach : Two tiers. NAIVE counts each candidate (O(n^2) or O(n) with a hashmap). EFFICIENT uses
//            Boyer-Moore voting: one candidate + a counter, in O(n) time and O(1) space.
// Intuition: If an element occurs more than half the time, then pairing up each of its occurrences
//            with a different element still leaves some of it unpaired. The voting counter models
//            exactly that cancellation: matches increment, mismatches decrement, and the survivor is
//            the only possible majority.
// Time     : naive O(n^2) (or O(n) w/ map); efficient O(n)   Space: O(1) for the efficient one
// Trade-off: Boyer-Moore is optimal in time AND space, but assumes a majority EXISTS - so a second
//            pass to verify the candidate is required when that is not guaranteed.

public class mooreVotingMajority {

    // ---------- NAIVE: O(n^2), no extra space ----------
    static int majorityNaive(int[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) if (a[j] == a[i]) count++;
            if (count > n / 2) return a[i];
        }
        return -1; // no majority
    }

    // ---------- EFFICIENT: Boyer-Moore voting, O(n) time, O(1) space ----------
    static int majorityEfficient(int[] a) {
        // Phase 1: find a CANDIDATE by cancellation.
        int candidate = a[0], count = 0;
        for (int x : a) {
            if (count == 0) candidate = x;      // no one leading -> adopt current as candidate
            count += (x == candidate) ? 1 : -1; // vote for or against the candidate
        }
        // Phase 2: verify (the array may have no true majority).
        count = 0;
        for (int x : a) if (x == candidate) count++;
        return count > a.length / 2 ? candidate : -1;
    }

    public static void main(String[] args) {
        int[] a = { 2, 2, 1, 1, 1, 2, 2 };
        System.out.println("naive     : " + majorityNaive(a));     // 2 (appears 4 > 7/2)
        System.out.println("efficient : " + majorityEfficient(a)); // 2
        int[] b = { 1, 2, 3, 4 };
        System.out.println("no majority: " + majorityEfficient(b)); // -1
    }
}
