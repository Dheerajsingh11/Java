// Problem  : Understand methods - overloading, varargs, recursion, and Java's pass-by-value semantics.
// Approach : One class demonstrating each concept with clear before/after output.
// Intuition: A method is a named, reusable block. The subtle points are (1) overloading picks a method
//            by argument types at compile time, and (2) Java ALWAYS passes arguments by value - even
//            object references are copied (the reference is copied, not the object).
// Time     : n/a   Space: recursion uses O(depth) stack
// Trade-off: Overloading and varargs improve API ergonomics; recursion can be cleaner than loops but
//            costs stack space. Knowing pass-by-value prevents "why didn't my swap work?" bugs.

public class MethodsDemo {

    // ---- Overloading: same name, different parameter lists (resolved at compile time) ----
    static int add(int a, int b) { return a + b; }
    static double add(double a, double b) { return a + b; }

    // ---- Varargs: accept any number of ints as an array 'nums' ----
    static int sum(int... nums) {
        int total = 0;
        for (int n : nums) total += n; // 'nums' behaves like an int[]
        return total;
    }

    // ---- Recursion: a method calling itself with a base case ----
    static long factorial(int n) {
        if (n <= 1) return 1;          // base case stops the recursion
        return n * factorial(n - 1);   // recursive case shrinks toward the base
    }

    // ---- Pass-by-value proof ----
    static void tryToSwap(int x, int y) { int t = x; x = y; y = t; } // swaps COPIES -> no effect outside
    static void mutate(int[] arr) { arr[0] = 999; } // the reference is copied, but it points to the SAME array

    public static void main(String[] args) {
        System.out.println(add(2, 3));      // 5    (int version)
        System.out.println(add(2.5, 3.5));  // 6.0  (double version)
        System.out.println(sum(1, 2, 3, 4)); // 10  (varargs)
        System.out.println(factorial(5));    // 120

        // Primitives: pass-by-value means the caller's variables are untouched.
        int p = 1, q = 2;
        tryToSwap(p, q);
        System.out.println("p=" + p + " q=" + q); // p=1 q=2  (swap did NOT work)

        // Objects: the reference is copied, so mutating the SAME object IS visible to the caller.
        int[] arr = { 1, 2, 3 };
        mutate(arr);
        System.out.println("arr[0]=" + arr[0]); // 999 (same array object mutated)
    }
}
