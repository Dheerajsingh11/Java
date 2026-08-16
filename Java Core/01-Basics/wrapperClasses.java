// Problem  : Use primitives where OBJECTS are required (collections, generics), via wrapper classes.
// Approach : Show boxing/unboxing, the Integer cache and the == trap it creates, null-unboxing NPEs,
//            and the performance cost of boxing in bulk.
// Intuition: Generics and collections only work with objects, but `int` is not an object. Wrappers
//            (Integer, Double, ...) are immutable objects that hold one primitive, and autoboxing
//            makes the conversion invisible - which is convenient right up until it bites.
// Time     : boxing allocates (or hits a cache) O(1)   Space: an object header per boxed value
// Trade-off: Autoboxing buys readability and costs three real things: memory (~16 bytes vs 4 for an
//            int), speed in loops, and two silent failure modes (== comparison and null unboxing).

import java.util.ArrayList;
import java.util.List;

public class wrapperClasses {
    public static void main(String[] args) {

        // ---- The eight wrappers mirror the eight primitives ----
        // byte->Byte  short->Short  int->Integer  long->Long
        // float->Float  double->Double  char->Character  boolean->Boolean

        // ---- Autoboxing / auto-unboxing: the compiler inserts the conversions ----
        Integer boxed = 42;          // autoboxing:  Integer.valueOf(42)
        int unboxed = boxed;         // auto-unboxing: boxed.intValue()
        System.out.println("boxed=" + boxed + " unboxed=" + unboxed);

        // Collections cannot hold primitives, which is the main reason wrappers exist.
        List<Integer> nums = new ArrayList<>();
        nums.add(1);                 // autoboxed
        int first = nums.get(0);     // auto-unboxed
        System.out.println("from list: " + first);

        // ---- TRAP 1: the Integer cache, and why == sometimes "works" ----
        // Java caches Integer objects for -128..127, so valueOf returns the SAME object in that range.
        Integer a = 127, b = 127;
        Integer c = 128, d = 128;
        System.out.println("127 == 127 : " + (a == b));   // true  - both are the cached object
        System.out.println("128 == 128 : " + (c == d));   // false - two distinct objects  <-- TRAP
        System.out.println("128 .equals: " + c.equals(d)); // true - compares VALUES

        // This is the worst kind of bug: == appears correct in small test data and fails on real
        // values above 127. RULE: compare wrappers with .equals(), or unbox to primitives first.
        System.out.println("unboxed compare: " + (c.intValue() == d.intValue())); // true

        // ---- TRAP 2: unboxing null throws NullPointerException ----
        Integer maybeNull = null;
        try {
            int boom = maybeNull;    // compiles to maybeNull.intValue() -> NPE
            System.out.println(boom);
        } catch (NullPointerException e) {
            System.out.println("unboxing null -> NullPointerException");
        }
        // A wrapper can be null; a primitive cannot. That is useful ("no value recorded" vs 0), but
        // every unboxing site becomes a potential NPE.

        // ---- Useful static helpers on the wrappers ----
        System.out.println("parseInt(\"123\") + 1 = " + (Integer.parseInt("123") + 1));
        System.out.println("Integer.MAX_VALUE    = " + Integer.MAX_VALUE);
        System.out.println("Integer.compare(3,7) = " + Integer.compare(3, 7)); // overflow-safe
        System.out.println("toBinaryString(10)   = " + Integer.toBinaryString(10));
        System.out.println("Character.isDigit('7') = " + Character.isDigit('7'));

        // ---- TRAP 3: the cost of boxing in bulk ----
        // Each element of a List<Integer> is a separate heap object plus a reference - roughly
        // 4-5x the memory of an int[], with worse cache behaviour.
        long t0 = System.nanoTime();
        long sumBoxed = 0;
        List<Integer> big = new ArrayList<>();
        for (int i = 0; i < 200_000; i++) big.add(i);       // 200,000 allocations
        for (int v : big) sumBoxed += v;                     // 200,000 unboxings
        long t1 = System.nanoTime();

        int[] prim = new int[200_000];
        long sumPrim = 0;
        for (int i = 0; i < prim.length; i++) prim[i] = i;
        for (int v : prim) sumPrim += v;
        long t2 = System.nanoTime();

        System.out.println("sums equal: " + (sumBoxed == sumPrim));
        System.out.printf("List<Integer>: %d ms,  int[]: %d ms%n",
                (t1 - t0) / 1_000_000, (t2 - t1) / 1_000_000);
    }
}

/* ------------------------------ WHEN TO USE WHICH ------------------------------
 * Use a PRIMITIVE by default - it is smaller and faster.
 * Use a WRAPPER when you have no choice or genuinely need object semantics:
 *   - Collections and generics: List<Integer>, Map<String, Double>  (no List<int>)
 *   - You need to represent "absent": null means "no value", which 0 cannot express
 *   - You need the static utilities: Integer.parseInt, Integer.MAX_VALUE, Character.isLetter
 *
 * THE THREE RULES THAT PREVENT THE COMMON BUGS
 *   1. Compare wrappers with .equals(), never == (the cache makes == look correct below 128).
 *   2. Null-check before unboxing, or use a primitive.
 *   3. For large numeric data use int[] / double[], not List<Integer> - the boxing overhead is real.
 * ------------------------------------------------------------------------------- */
