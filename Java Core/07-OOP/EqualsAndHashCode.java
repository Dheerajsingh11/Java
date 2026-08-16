// Problem  : Make value-based equality work correctly, so objects behave properly in collections.
// Approach : Override equals() and hashCode() together, honouring the contract between them, and
//            demonstrate what breaks when you don't.
// Intuition: By default, equals() compares REFERENCES - two objects with identical contents are
//            "different". For value types (a Point, a Money, an Id) that is wrong. But overriding
//            equals alone silently breaks HashMap/HashSet, because they locate entries by HASH first
//            and only then compare with equals.
// Time     : equals/hashCode O(number of fields)   Space: O(1)
// Trade-off: This is boilerplate, which is exactly why `record` (RecordDemo.java) exists - it
//            generates both correctly. Write them by hand only for mutable classes or custom
//            equality rules.

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// ---------- BROKEN: overrides equals but NOT hashCode ----------
class BadPoint {
    int x, y;
    BadPoint(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BadPoint)) return false;
        BadPoint p = (BadPoint) o;
        return x == p.x && y == p.y;
    }
    // hashCode NOT overridden -> inherits Object's identity hash, so two equal BadPoints almost
    // always land in DIFFERENT buckets and the set never notices they are duplicates.
}

// ---------- CORRECT ----------
final class Point {
    private final int x, y;      // final fields: the hash can never go stale

    Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                 // fast path: same object
        if (o == null || getClass() != o.getClass()) return false; // null-safe + type check
        Point p = (Point) o;
        return x == p.x && y == p.y;                // compare the VALUE-defining fields
    }

    @Override
    public int hashCode() {
        // MUST be derived from exactly the same fields equals() uses, or the contract breaks.
        return Objects.hash(x, y);
    }

    @Override
    public String toString() { return "Point(" + x + ", " + y + ")"; }
}

public class EqualsAndHashCode {
    public static void main(String[] args) {

        // ---- Why overriding equals alone is not enough ----
        Set<BadPoint> bad = new HashSet<>();
        bad.add(new BadPoint(1, 2));
        bad.add(new BadPoint(1, 2));               // "equal" to the first...
        System.out.println("BadPoint set size : " + bad.size());          // expected: 2  <- WRONG
        System.out.println("BadPoint contains : " + bad.contains(new BadPoint(1, 2))); // false <- WRONG

        Set<Point> good = new HashSet<>();
        good.add(new Point(1, 2));
        good.add(new Point(1, 2));
        System.out.println("Point set size    : " + good.size());         // expected: 1  correct
        System.out.println("Point contains    : " + good.contains(new Point(1, 2))); // true

        // WHY: HashSet first computes hashCode() to pick a bucket, and only compares with equals()
        // INSIDE that bucket. Different hashes -> different buckets -> equals() is never called.

        System.out.println("equal objects, same hash? "
                + (new Point(1, 2).hashCode() == new Point(1, 2).hashCode())); // true
    }
}

/* ------------------------------- THE CONTRACT -------------------------------
 * equals() must be:
 *   reflexive   x.equals(x) is true
 *   symmetric   x.equals(y) == y.equals(x)
 *   transitive  x.equals(y) && y.equals(z)  =>  x.equals(z)
 *   consistent  repeated calls give the same answer while the objects are unchanged
 *   null-safe   x.equals(null) is false, never an exception
 *
 * The bridge to hashCode():
 *   equal objects MUST have equal hash codes.        <- break this and collections misbehave
 *   unequal objects MAY share a hash code (collision) - that is legal and normal.
 *
 * Two further rules that bite in practice:
 *   1. NEVER use a mutable field in hashCode(). If a key's hash changes after insertion, the map
 *      looks in the wrong bucket and the entry becomes unreachable - a silent leak.
 *   2. getClass() vs instanceof: getClass() makes equality strict (a subclass is never equal to its
 *      parent), which keeps SYMMETRY. instanceof allows subclass equality but is easy to get wrong -
 *      prefer getClass(), or make the class final as Point is here.
 * -------------------------------------------------------------------------- */
