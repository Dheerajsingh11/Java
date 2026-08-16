// Problem  : Print the n-th term of an Arithmetic Progression (AP) given first term a, position n,
//            and common difference d.
// Approach : Apply the closed-form AP formula  a_n = a + (n - 1) * d  directly.
// Intuition: An AP adds d each step: a, a+d, a+2d, ... The n-th term has added d exactly (n-1)
//            times to the first term, so no loop is needed - it is a single formula.
// Time     : O(1)   Space: O(1)     (a loop summing d would be O(n); the formula avoids that)
// Trade-off: The formula is instant regardless of n. A loop would also work and might feel more
//            intuitive, but it is O(n) for no benefit here.
// INPUT    : three integer lines: a (first term), n (position, 1-based), d (common difference).
//            Example stdin:  2\n 5\n 3   -> series 2,5,8,11,14 -> 5th term = 14

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class nthTerm {
    public static void main(String[] args) throws IOException {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))) {
            int a = Integer.parseInt(bf.readLine()); // first term
            int n = Integer.parseInt(bf.readLine()); // which term we want (1-based)
            int d = Integer.parseInt(bf.readLine()); // common difference (can be negative or zero)

            // Edge: n = 1 gives a + 0*d = a (the first term itself), which the formula handles.
            // Edge: for very large n or d, (n-1)*d can overflow int; use long if that is a concern.
            int res = a + ((n - 1) * d);

            System.out.println("The " + n + "-th term = " + res);
        }
    }
}
