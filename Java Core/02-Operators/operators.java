// Problem  : Demonstrate every category of Java operator and how each evaluates.
// Approach : Run one example of each: arithmetic, unary (pre/post inc/dec), assignment (incl.
//            compound bitwise), relational, logical (with short-circuit), ternary, bitwise, shifts.
// Intuition: Operators are the verbs of expressions. The subtle parts are (a) pre vs post
//            increment, (b) short-circuit logical operators, and (c) the three shift operators.
// Time     : O(1)   Space: O(1)
// Trade-off: Bitwise/shift operators are fast, low-level tools (flags, powers of two); logical
//            &&/|| short-circuit and are what you normally want for boolean conditions.

public class operators {
    public static void main(String[] args) {
        int a = 10;
        int b = 20;

        // ---- Arithmetic ----
        System.out.println("\nArithmetic (a=10, b=20)");
        System.out.println("a + b = " + (a + b)); // 30
        System.out.println("a - b = " + (a - b)); // -10
        System.out.println("a * b = " + (a * b)); // 200
        System.out.println("a / b = " + (a / b)); // 0  <- INTEGER division truncates (10/20 = 0.5 -> 0)
        System.out.println("a % b = " + (a % b)); // 10 <- remainder of 10 / 20

        // ---- Unary increment/decrement: POST vs PRE ----
        // Post (a++): use the current value in the expression, THEN change a.
        // Pre  (++a): change a FIRST, then use the new value.
        System.out.println("\nUnary (order matters)");
        System.out.println("a++ = " + a++); // prints 10, then a becomes 11
        System.out.println("++a = " + ++a); // a becomes 12, then prints 12
        System.out.println("a-- = " + a--); // prints 12, then a becomes 11
        System.out.println("--a = " + --a); // a becomes 10, then prints 10

        // ---- Assignment + compound assignment ----
        // "f += 3" means "f = f + 3". Compound forms exist for every arithmetic/bitwise operator.
        int f = 7;
        System.out.println("\nAssignment");
        System.out.println("f += 3      -> " + (f += 3));       // 10
        System.out.println("f -= 2      -> " + (f -= 2));       // 8
        System.out.println("f *= 4      -> " + (f *= 4));       // 32
        System.out.println("f /= 3      -> " + (f /= 3));       // 10 (32/3 truncates)
        System.out.println("f %= 2      -> " + (f %= 2));       // 0
        System.out.println("f = 6 then f &= 0b1010 -> " + ((f = 6) & 0b1010)); // 6 & 10 = 2

        // ---- Relational (produce boolean) ----
        int c = 5;
        System.out.println("\nRelational (a=10, b=20, c=5)");
        System.out.println("a > b : " + (a > b));  // false
        System.out.println("a < b : " + (a < b));  // true
        System.out.println("a == c: " + (a == c)); // false
        System.out.println("a != c: " + (a != c)); // true

        // ---- Logical with SHORT-CIRCUIT ----
        // && stops at the first false; || stops at the first true. The right side may never run -
        // this is used to guard, e.g.  (arr != null && arr.length > 0).
        boolean x = true, y = false;
        System.out.println("\nLogical");
        System.out.println("x && y: " + (x && y)); // false (evaluates y)
        System.out.println("x || y: " + (x || y)); // true  (short-circuits after x, y not needed)
        System.out.println("!x    : " + (!x));     // false

        // ---- Ternary (compact if/else expression) ----
        // condition ? valueIfTrue : valueIfFalse. Here nested to pick the max of three.
        int max = (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
        System.out.println("\nTernary max(a,b,c) = " + max); // 20

        // ---- Bitwise (operate on individual bits) ----
        int d = 0b1010; // 10
        int e = 0b1100; // 12
        System.out.println("\nBitwise (d=1010, e=1100)");
        System.out.println("d & e = " + (d & e)); // 1000 = 8  (AND: bit set only if BOTH are)
        System.out.println("d | e = " + (d | e)); // 1110 = 14 (OR: bit set if EITHER is)
        System.out.println("d ^ e = " + (d ^ e)); // 0110 = 6  (XOR: bit set if bits DIFFER)
        System.out.println("~d    = " + (~d));     // -11       (NOT: flips all bits; ~x == -x-1)

        // ---- Shifts ----
        // <<  n : multiply by 2^n (shift bits left, fill zeros)
        // >>  n : arithmetic right shift, keeps the sign bit (divide by 2^n for non-negatives)
        // >>> n : logical right shift, always fills zeros (matters for negative numbers)
        System.out.println("\nShifts");
        System.out.println("d << 2 = " + (d << 2)); // 40 (10 * 4)
        System.out.println("e >> 1 = " + (e >> 1)); // 6  (12 / 2)
        System.out.println("-8 >> 1  = " + (-8 >> 1));  // -4 (sign preserved)
        System.out.println("-8 >>> 28 = " + (-8 >>> 28)); // 15 (zero-filled, so a large positive)
    }
}
