// Problem  : Find the SECOND largest DISTINCT element of an array (returns its 1-based POSITION).
// Approach : EFFICIENT / single pass - carry BOTH the largest and the second largest as we sweep,
//            updating them together whenever a new element arrives.
// Intuition: When a new maximum appears, the OLD maximum is automatically demoted to second place -
//            that single observation removes the need for a second pass. Otherwise the element only
//            competes for the runner-up slot.
// Time     : THETA(n) - exactly one pass (n comparisons vs the naive version's 2n)
// Space    : THETA(1)
// Trade-off: Same big-O as the naive two-pass version but half the comparisons, and crucially it is
//            SINGLE-PASS - so it works on streaming data that can only be read once. The cost is
//            trickier logic: the two variables must be updated in the right order.
// RETURNS  : a 1-BASED POSITION, not the value.

public class getSecLargEfficient {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        System.out.println(secondLargest(arr));                    // expected: 5 (position of 5)
        System.out.println(secondLargest(new int[]{ 10, 10, 7 }));  // expected: 3 (skips duplicate max)
    }

    static int secondLargest(int arr[]) {
        int res = -1;        // INDEX of the best runner-up so far; -1 = none yet
        int largest = 0;     // INDEX of the maximum so far (assume element 0 to start)

        for (int i = 1; i < arr.length; i++) {

            if (arr[i] > arr[largest]) {
                // A new maximum. The previous champion is, by definition, now the second largest -
                // this demotion is the key trick that makes one pass sufficient.
                res = largest;
                largest = i;

            } else if (arr[i] != arr[largest]) {
                // Not a new maximum, and not a DUPLICATE of it (the != check enforces "distinct").
                // So it only competes for the runner-up slot.
                if (res == -1 || arr[i] > arr[res]) {
                    res = i;
                }
            }
            // Implicit third case: arr[i] EQUALS the maximum -> ignored entirely, which is what
            // keeps the answer strictly "second largest DISTINCT value".
        }
        return (res + 1);    // index -> 1-based position; returns 0 when all elements are identical
    }
}
