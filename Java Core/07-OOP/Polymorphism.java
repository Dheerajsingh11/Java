// Problem  : Let one name/interface work with many different types or argument lists.
// Approach : Show BOTH kinds of polymorphism - compile-time (method overloading) and
//            run-time (method overriding via a superclass reference).
// Intuition: "Poly" = many, "morph" = forms. The same call site behaves differently depending on
//            argument types (overloading) or the actual object type (overriding).
// Time     : n/a   Space: n/a
// Trade-off: Overloading is resolved by the compiler (fast, static); overriding is resolved at
//            run time (flexible, enables plugging in new subclasses without changing callers).

class Shape {
    double area() {
        return 0;                       // base version; subclasses give real formulas
    }
}

class Circle extends Shape {
    double r;
    Circle(double r) { this.r = r; }

    @Override
    double area() {
        return Math.PI * r * r;         // run-time polymorphism: called via a Shape reference
    }
}

class Rectangle extends Shape {
    double w, h;
    Rectangle(double w, double h) { this.w = w; this.h = h; }

    @Override
    double area() {
        return w * h;
    }
}

public class Polymorphism {

    // Compile-time polymorphism (overloading): same method name, different parameter lists.
    // The compiler picks which one to call based on the arguments you pass.
    static int add(int a, int b) {
        return a + b;
    }
    static double add(double a, double b) {   // different parameter types
        return a + b;
    }
    static int add(int a, int b, int c) {     // different parameter count
        return a + b + c;
    }

    public static void main(String[] args) {
        // --- Overloading resolved at compile time ---
        System.out.println(add(2, 3));        // expected: 5     (int, int)
        System.out.println(add(2.5, 3.5));    // expected: 6.0   (double, double)
        System.out.println(add(1, 2, 3));     // expected: 6     (three ints)

        // --- Overriding resolved at run time ---
        // A Shape[] can hold any subclass. The ACTUAL object decides which area() runs.
        Shape[] shapes = { new Circle(1), new Rectangle(2, 3) };
        for (Shape s : shapes) {
            System.out.printf("%.2f%n", s.area()); // expected: 3.14 then 6.00
        }

        // Deeper dive - HOW run-time overriding works, and its limits:
        // 1) Dynamic dispatch: each object carries a hidden pointer to its class's method table
        //    (a "vtable"). "s.area()" looks up area() in the ACTUAL object's table at run time, so
        //    the Circle/Rectangle version runs even though the reference type is Shape. This is what
        //    lets you add a new Shape subclass WITHOUT touching this loop - the key benefit of OOP.
        // 2) Only INSTANCE METHODS are polymorphic. FIELDS are not: a field access uses the
        //    reference type, not the object type (field "hiding"), so avoid same-named fields.
        // 3) STATIC methods are HIDDEN, not overridden: a static method resolves by the reference
        //    type at compile time. That is why @Override cannot be used on static methods.
        // 4) private/final methods cannot be overridden (they are resolved statically).
    }
}
