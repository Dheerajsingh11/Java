// Problem  : Build and modify strings efficiently (without creating a new String on every change).
// Approach : Use StringBuilder - a MUTABLE character buffer - for append/insert/reverse/delete.
// Intuition: Because String is immutable, "s += x" in a loop allocates and copies the whole string
//            each iteration (O(n^2) overall). StringBuilder mutates one growable buffer, so appends
//            are amortized O(1).
// Time     : append amortized O(1); toString O(n)   Space: O(n) buffer
// Trade-off: StringBuilder is the go-to for string construction in loops. StringBuffer is the same
//            API but thread-safe (synchronized) and slower - only use it across threads.

public class StringBuilderDemo {
    public static void main(String[] args) {
        // ---- Why it matters: concatenation in a loop is O(n^2) ----
        // Bad:   String s = ""; for (...) s += i;   // copies the whole string each time
        // Good:
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i).append(',');
        System.out.println(sb.toString()); // 1,2,3,4,5,

        // ---- Mutating operations (all in place, returning 'this' for chaining) ----
        StringBuilder b = new StringBuilder("Hello");
        b.append(" World");                // Hello World
        b.insert(5, ",");                  // Hello, World
        b.reverse();                       // dlroW ,olleH
        System.out.println(b);
        b.reverse();                       // back to Hello, World
        b.delete(5, 6);                    // remove the comma -> Hello World
        b.replace(0, 5, "Howdy");          // Howdy World
        System.out.println(b);             // Howdy World
        System.out.println("length: " + b.length()); // 11

        // Common idiom: reverse a string quickly.
        System.out.println(new StringBuilder("racecar").reverse()); // racecar (a palindrome)
    }
}
