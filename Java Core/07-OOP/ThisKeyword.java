// Problem  : Understand every common use of the "this" reference.
// Approach : One class demonstrating this.field, this(...) chaining, this as an argument, and
//            returning this for method chaining.
// Intuition: "this" is a reference to the CURRENT object - the specific instance a method is
//            running on. It lets an object talk about itself.
// Time     : n/a   Space: n/a
// Trade-off: "this" is optional when there is no name clash, but always required to (a) resolve
//            field-vs-parameter shadowing and (b) enable fluent chaining.

class Box {
    int w, h;

    Box() {
        // Use 1: call another constructor of this class (must be first statement).
        this(1, 1);
    }

    Box(int w, int h) {
        // Use 2: distinguish the field "this.w" from the parameter "w" (shadowing).
        this.w = w;
        this.h = h;
    }

    // Use 3: return "this" so calls can be chained: box.setW(2).setH(3)...
    Box setW(int w) {
        this.w = w;
        return this;                    // hand the same object back
    }

    Box setH(int h) {
        this.h = h;
        return this;
    }

    void printVia(Printer p) {
        // Use 4: pass the current object to another method/object.
        p.print(this);
    }

    int area() {
        return w * h;
    }
}

class Printer {
    void print(Box b) {
        System.out.println("Box " + b.w + "x" + b.h + " area=" + b.area());
    }
}

public class ThisKeyword {
    public static void main(String[] args) {
        Box b = new Box()               // -> 1x1 via this(1,1)
                .setW(4)                 // fluent chaining works because setW returns this
                .setH(5);
        b.printVia(new Printer());       // expected: Box 4x5 area=20
    }
}
