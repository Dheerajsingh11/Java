// Problem  : Define a capability that unrelated classes can promise to provide.
// Approach : Declare an interface (a pure contract), implement it in multiple classes, and show
//            default methods and multiple-interface implementation.
// Intuition: An interface says "any type that implements me CAN do these things", regardless of
//            its place in the class hierarchy. This is how Java does "multiple inheritance of type".
// Time     : n/a   Space: n/a
// Trade-off: A class can implement MANY interfaces (but extend only one class). Interfaces give
//            flexibility and decoupling; abstract classes give shared state. Choose accordingly.

// An interface is a contract: method signatures with no body (implicitly public + abstract).
interface Drawable {
    void draw();                        // implementers MUST provide this

    // "default" method: an interface can supply a body so implementers get it for free.
    // Added in Java 8 so interfaces could grow without breaking existing implementers.
    default void hint() {
        System.out.println("(anything drawable can be rendered)");
    }
}

// A second, unrelated capability.
interface Resizable {
    void resize(double factor);
}

// One class can implement MULTIPLE interfaces - this is Java's answer to multiple inheritance.
class Square implements Drawable, Resizable {
    double side;
    Square(double side) { this.side = side; }

    @Override
    public void draw() {                // note: interface methods are public, so override as public
        System.out.println("Drawing a square with side " + side);
    }

    @Override
    public void resize(double factor) {
        side *= factor;
    }
}

public class Interfaces {
    public static void main(String[] args) {
        Square sq = new Square(4);

        // We can treat the object through EITHER contract it fulfils.
        Drawable d = sq;                // view it as "something drawable"
        d.draw();                       // expected: Drawing a square with side 4.0
        d.hint();                       // expected: (anything drawable can be rendered)

        Resizable r = sq;               // same object, different capability view
        r.resize(2);
        sq.draw();                      // expected: Drawing a square with side 8.0

        // Deeper dive - what interfaces can/cannot hold, and the "diamond" rule:
        // - Fields in an interface are implicitly public static final (CONSTANTS), never instance state.
        // - Besides abstract and default methods, interfaces may have STATIC methods and (Java 9+)
        //   PRIVATE helper methods to share code between default methods.
        // - A class may implement many interfaces. If two interfaces provide a default method with
        //   the SAME signature (the "diamond problem"), the class MUST override it to resolve the
        //   clash, and can pick one explicitly with  InterfaceName.super.method().
        // - Prefer an interface when unrelated types share a capability; prefer an abstract class
        //   when related types share STATE plus partial implementation.
    }
}
