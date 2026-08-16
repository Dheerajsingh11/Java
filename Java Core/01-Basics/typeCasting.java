// Problem  : Convert values between types safely, and know when Java does it for you.
// Approach : Show widening (implicit), narrowing (explicit), promotion in expressions, and the
//            data-loss traps each can hide.
// Intuition: A cast tells the compiler "I know this conversion may lose information - do it anyway".
//            Java performs SAFE conversions silently but forces you to spell out lossy ones, so an
//            explicit cast is really a written acknowledgement of risk.
// Time     : O(1)   Space: O(1)
// Trade-off: Narrowing casts are occasionally necessary but silently corrupt data when the value does
//            not fit - there is no exception, just a wrong number. Prefer a wider type over a cast.

public class typeCasting {
    public static void main(String[] args) {

        // ---- WIDENING (implicit): small type -> larger type. Always safe, no cast needed. ----
        // byte -> short -> int -> long -> float -> double,  and char -> int
        int i = 100;
        long l = i;        // int fits in long
        double d = l;      // long fits in double
        System.out.println("widening: " + i + " -> " + l + " -> " + d);

        // ---- NARROWING (explicit): larger -> smaller. A cast is REQUIRED, and data may be lost. ----
        double pi = 3.99;
        int truncated = (int) pi;     // TRUNCATES toward zero - it does NOT round
        System.out.println("(int) 3.99 = " + truncated);          // expected: 3
        System.out.println("Math.round(3.99) = " + Math.round(pi)); // expected: 4  <- use this to round

        // The dangerous case: the value does not fit, and Java says nothing.
        int big = 300;
        byte b = (byte) big;          // byte holds -128..127; 300 wraps around
        System.out.println("(byte) 300 = " + b);                  // expected: 44   <- SILENT corruption

        long huge = 3_000_000_000L;
        int overflowed = (int) huge;  // exceeds int range
        System.out.println("(int) 3000000000 = " + overflowed);   // expected: -1294967296

        // ---- PROMOTION inside expressions ----
        // byte/short/char are promoted to int before arithmetic, which is why this needs a cast back.
        byte x = 10, y = 20;
        // byte sum = x + y;          // does NOT compile: x + y is an int
        byte sum = (byte) (x + y);
        System.out.println("byte + byte needs a cast: " + sum);

        // If ANY operand is a double, the whole expression becomes double. This is the fix for the
        // classic integer-division trap.
        System.out.println("7 / 2       = " + (7 / 2));           // 3    (int division truncates)
        System.out.println("7 / 2.0     = " + (7 / 2.0));         // 3.5
        System.out.println("(double)7/2 = " + ((double) 7 / 2));  // 3.5

        // ---- char is numeric ----
        char c = 'A';
        System.out.println("'A' as int   = " + (int) c);          // 65
        System.out.println("65 as char   = " + (char) 65);        // A
        System.out.println("'c' - 'a'    = " + ('c' - 'a'));      // 2 - the index idiom

        // ---- REFERENCE casting: upcast is safe, downcast can fail at RUN TIME ----
        Object o = "hello";
        String s = (String) o;                 // fine - o really is a String
        System.out.println("downcast ok: " + s.toUpperCase());

        Object n = Integer.valueOf(42);
        // String bad = (String) n;            // compiles, but throws ClassCastException at run time
        // Guard downcasts with instanceof (pattern matching form, Java 16+):
        if (n instanceof String str) System.out.println(str);
        else System.out.println("not a String - instanceof prevented a ClassCastException");
    }
}

/* -------------------------------- RULES OF THUMB --------------------------------
 * - Widening is implicit and lossless; narrowing needs a cast and MAY lose data silently.
 * - (int) TRUNCATES; use Math.round() when you want rounding.
 * - Prefer choosing a wider type over casting. A cast is a claim you should be able to justify.
 * - int -> float and long -> double are "widening" but can still lose PRECISION, because floats
 *   have limited significant digits: (float) 16777217 comes back as 16777216.
 * - Always guard a reference downcast with instanceof, or you risk ClassCastException.
 * -------------------------------------------------------------------------------- */
