// Problem  : Show Java's eight primitive data types, their sizes, and their value ranges.
// Approach : Declare one variable of each primitive and print it alongside its Min/Max limits.
// Intuition: A primitive stores a raw value directly (not a reference). Its type fixes how many
//            bits it uses, which in turn fixes the range of values it can hold.
// Time     : O(1)   Space: O(1)
// Trade-off: Smaller types (byte/short) save memory in huge arrays but overflow sooner; wider
//            types (long/double) are safer but use more memory. Pick the smallest that fits.

public class dataType {
    public static void main(String[] args) {
        // ---- Integer family (whole numbers, signed / two's complement) ----
        byte  c    = 4;          // 8-bit,  range -128 .. 127
        short s    = 56;         // 16-bit, range -32,768 .. 32,767
        int   b    = 10;         // 32-bit, the DEFAULT choice for whole numbers
        long  l    = 123123L;    // 64-bit; the 'L' suffix marks a long literal (needed for big values)

        // ---- Floating-point family (fractional numbers, IEEE-754) ----
        float  f = 4.123123f;    // 32-bit; the 'f' suffix is REQUIRED (a decimal literal is double by default)
        double d = 4.12312312;   // 64-bit; the default and more precise choice for decimals

        // ---- Other primitives ----
        char    a    = 'A';      // 16-bit UNSIGNED; holds a single Unicode character (also usable as a number 0..65535)
        boolean bool = true;     // only true/false; size is JVM-dependent (not a usable bit count)

        System.out.println("byte    = " + c + "  (range " + Byte.MIN_VALUE + " .. " + Byte.MAX_VALUE + ")");
        System.out.println("short   = " + s + "  (range " + Short.MIN_VALUE + " .. " + Short.MAX_VALUE + ")");
        System.out.println("int     = " + b + "  (range " + Integer.MIN_VALUE + " .. " + Integer.MAX_VALUE + ")");
        System.out.println("long    = " + l + "  (range " + Long.MIN_VALUE + " .. " + Long.MAX_VALUE + ")");
        System.out.println("float   = " + f);
        System.out.println("double  = " + d);
        System.out.println("char    = " + a + "  (as number: " + (int) a + ")"); // 'A' has code 65
        System.out.println("boolean = " + bool);

        // Edge / gotcha: overflow wraps around silently, it does NOT throw an error.
        // int max is 2,147,483,647; adding 1 wraps to the most negative int.
        int overflow = Integer.MAX_VALUE + 1;
        System.out.println("Integer.MAX_VALUE + 1 = " + overflow); // expected: -2147483648

        // Edge / gotcha: integer division truncates toward zero (no fraction kept).
        System.out.println("7 / 2 (int)    = " + (7 / 2));     // expected: 3, not 3.5
        System.out.println("7 / 2.0 (double) = " + (7 / 2.0)); // expected: 3.5 (one double operand promotes both)
    }
}
