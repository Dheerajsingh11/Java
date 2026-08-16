// Problem  : Demonstrate the mechanics of a recursive call - how it starts, repeats, and unwinds.
// Approach : A minimal function that prints, then calls itself with a smaller argument, until a base
//            case stops it.
// Intuition: Recursion = a function solving a problem by calling itself on a SMALLER version of the
//            same problem. Two parts are mandatory: a BASE CASE that stops, and a RECURSIVE CASE
//            that makes measurable progress toward that base case.
// Time     : O(n) - one call per decrement of n
// Space    : O(n) - every pending call keeps a stack frame until the base case returns
// Trade-off: Recursion expresses self-similar problems (trees, divide & conquer) far more clearly
//            than loops, but each call costs a stack frame. Miss the base case - or fail to progress
//            toward it - and you get infinite recursion -> StackOverflowError.

public class recursion {

    static void fun1(int n) {
        // BASE CASE: the smallest input, answered directly, which terminates the recursion.
        // Remove this and the calls never stop -> StackOverflowError.
        if (n == 0) {
            return;
        }

        System.out.println("Fun1 with n = " + n); // work done on the way DOWN the recursion

        // RECURSIVE CASE: n - 1 is strictly closer to the base case, guaranteeing termination.
        fun1(n - 1);
    }

    public static void main(String[] args) {
        System.out.println("Main");
        fun1(3);
        System.out.println("After Fun1");
        // expected:
        // Main
        // Fun1 with n = 3
        // Fun1 with n = 2
        // Fun1 with n = 1
        // After Fun1
    }
    // How the CALL STACK behaves here:
    //   fun1(3) is pushed, prints, and calls fun1(2) -> pushed, prints, calls fun1(1) -> pushed,
    //   prints, calls fun1(0) -> hits the base case and returns. The frames then pop back in
    //   reverse order (0,1,2,3). At its deepest, 4 frames exist simultaneously - that is the O(n) space.
}
