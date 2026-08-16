// Problem  : Group a helper type inside the class that uses it, at the right scope.
// Approach : Demonstrate the four nested forms - static nested, inner (non-static), local, and
//            anonymous classes.
// Intuition: If a class only makes sense in the context of another, nesting it keeps related code
//            together and controls access. The static/non-static choice decides whether it needs
//            an enclosing OBJECT or just the enclosing CLASS.
// Time     : n/a   Space: n/a
// Trade-off: Inner classes can access the outer object's private state (convenient) but hold a
//            hidden reference to it (watch for leaks). Static nested classes are the safer default.

public class NestedClasses {
    private int outerField = 10;

    // 1) STATIC nested class: no link to any outer object; used like a mini top-level class.
    static class Engine {
        void start() { System.out.println("Engine started"); }
    }

    // 2) INNER (non-static) class: tied to an OUTER INSTANCE and can read its private fields.
    class Wheel {
        void roll() {
            System.out.println("Wheel rolling, outerField=" + outerField); // sees outer's field
        }
    }

    void demoLocalAndAnonymous() {
        // 3) LOCAL class: declared inside a method, visible only there.
        class Horn {
            void beep() { System.out.println("Beep! outerField=" + outerField); }
        }
        new Horn().beep();

        // 4) ANONYMOUS class: a one-off class + object in a single expression. Common for
        //    implementing an interface on the spot (pre-lambda style).
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous Runnable running");
            }
        };
        r.run();
    }

    public static void main(String[] args) {
        // Static nested: no outer object needed.
        new Engine().start();                         // expected: Engine started

        // Inner class needs an outer OBJECT first, then outer.new Inner().
        NestedClasses outer = new NestedClasses();
        NestedClasses.Wheel w = outer.new Wheel();
        w.roll();                                     // expected: Wheel rolling, outerField=10

        outer.demoLocalAndAnonymous();
        // expected: Beep! outerField=10  /  Anonymous Runnable running
    }
}
