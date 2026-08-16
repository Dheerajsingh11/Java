// Problem  : Repeat work with Java's loop constructs and control their flow.
// Approach : Demonstrate for, while, do-while, enhanced for-each, and the break / continue /
//            labeled-break jump statements.
// Intuition: A loop repeats a block while a condition holds. The three loops differ mainly in
//            WHEN the condition is checked and WHERE the counter lives.
// Time     : O(n) per single loop over n items   Space: O(1)
// Trade-off: Use "for" when the count is known, "while" when you loop until some event, "do-while"
//            when the body must run at least once, and "for-each" for read-only iteration over a
//            collection/array (cleanest, but no index and cannot modify the source).

public class loops {
    public static void main(String[] args) {
        // ---- for: counter known up front (init; condition; update) ----
        // init runs once; condition is checked BEFORE each pass; update runs AFTER each pass.
        System.out.print("for      : ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println(); // expected: 1 2 3 4 5

        // ---- while: condition checked FIRST; body may run zero times ----
        System.out.print("while    : ");
        int n = 5;
        while (n > 0) {          // if n started at 0, the body would never run
            System.out.print(n + " ");
            n--;
        }
        System.out.println(); // expected: 5 4 3 2 1

        // ---- do-while: body runs ONCE before the condition is checked (>= 1 iteration) ----
        System.out.print("do-while : ");
        int k = 0;
        do {
            System.out.print(k + " ");
            k++;
        } while (k < 3);
        System.out.println(); // expected: 0 1 2

        // ---- for-each: iterate elements directly, no index bookkeeping ----
        // Great for reading; you get a COPY of each element (reassigning it won't change the array).
        int[] arr = { 10, 20, 30 };
        System.out.print("for-each : ");
        for (int value : arr) {
            System.out.print(value + " ");
        }
        System.out.println(); // expected: 10 20 30

        // ---- break: exit the nearest loop immediately ----
        System.out.print("break    : ");
        for (int i = 1; i <= 10; i++) {
            if (i == 4) break;           // stop the whole loop when i hits 4
            System.out.print(i + " ");
        }
        System.out.println(); // expected: 1 2 3

        // ---- continue: skip the REST of this iteration, go to the next ----
        System.out.print("continue : ");
        for (int i = 1; i <= 6; i++) {
            if (i % 2 == 0) continue;    // skip even numbers
            System.out.print(i + " ");
        }
        System.out.println(); // expected: 1 3 5

        // ---- labeled break: exit an OUTER loop from inside a nested one ----
        // A plain break only leaves the inner loop; a label lets you break out of both at once.
        System.out.print("labeled  : ");
        outer:
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                if (i * j >= 4) break outer; // jumps completely out of BOTH loops
                System.out.print("(" + i + "," + j + ") ");
            }
        }
        System.out.println(); // expected: (1,1) (1,2) (1,3) (2,1)
    }
}
