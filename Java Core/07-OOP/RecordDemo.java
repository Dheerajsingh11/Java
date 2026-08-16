// Problem  : Model plain immutable data without writing boilerplate (fields, constructor,
//            getters, equals, hashCode, toString).
// Approach : Use a record (Java 16+), add validation via a compact constructor, and compare with
//            what it replaces.
// Intuition: Most "data holder" classes are 90% mechanical code. A record generates all of it from
//            a one-line declaration of the components, and makes the object immutable.
// Time     : n/a   Space: n/a
// Trade-off: Records are perfect for transparent, immutable data carriers, but they are final,
//            cannot extend other classes, and are a poor fit for mutable/behaviour-heavy types.
// NOTE     : Requires Java 16 or newer to compile.

// This single line generates: private final int x, y; a canonical constructor; x(), y() accessors;
// and correct equals(), hashCode(), toString().
record Point(int x, int y) {

    // Compact constructor: run validation before the fields are assigned. No parameter list, no
    // manual "this.x = x" - Java assigns the components for you after this block.
    Point {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("coordinates must be non-negative");
        }
    }

    // You can still add extra methods.
    double distanceToOrigin() {
        return Math.sqrt((double) x * x + (double) y * y);
    }
}

public class RecordDemo {
    public static void main(String[] args) {
        Point a = new Point(3, 4);
        Point b = new Point(3, 4);

        System.out.println(a);                 // auto toString -> expected: Point[x=3, y=4]
        System.out.println(a.x() + "," + a.y()); // auto accessors -> expected: 3,4

        // Value-based equality is generated for you (two points with same components are equal).
        System.out.println(a.equals(b));       // expected: true
        System.out.println(a.distanceToOrigin()); // expected: 5.0

        // a.x = 10;                            // <- would NOT compile: record components are final
    }
}
