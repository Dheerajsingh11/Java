// Problem  : Stock span - for each day, count how many consecutive days up to and including today
//            had a price <= today's price (i.e. today's price streak looking back).
// Approach : NAIVE - for each day i, walk backwards while prices are <= price[i], counting days.
// Intuition: The span is exactly "how far back can I go before hitting a strictly higher price?" -
//            so scanning left until a bigger price appears computes it directly.
// Time     : O(n^2) - each day may scan all previous days
// Space    : O(n) for the answer
// Trade-off: Straightforward but quadratic. The Efficient version uses a stack of previous-higher
//            days to get O(n).

import java.util.Arrays;

public class stockSpanNaive {

    static int[] span(int[] price) {
        int n = price.length;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int count = 1;                    // today itself always counts
            int j = i - 1;
            // Walk back while previous prices are not greater than today's.
            while (j >= 0 && price[j] <= price[i]) {
                count++;
                j--;                          // keep extending the streak leftward
            }
            res[i] = count;                   // stop at the first strictly higher price (or the start)
        }
        return res;
    }

    public static void main(String[] args) {
        int[] price = { 100, 80, 60, 70, 60, 75, 85 };
        System.out.println(Arrays.toString(span(price))); // [1, 1, 1, 2, 1, 4, 6]
        // day5 price 75: 75>=60,70,60 back to the 80 -> span 4; day6 price 85: back to the 100 -> span 6
    }
}
