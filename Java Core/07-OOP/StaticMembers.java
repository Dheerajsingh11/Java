// Problem  : Share data/behaviour across ALL objects of a class, and run setup once.
// Approach : Show static fields, static methods, and a static initializer block, contrasted with
//            instance members.
// Intuition: "static" belongs to the CLASS itself, not to any single object. There is exactly one
//            copy, shared by every instance and reachable without creating one.
// Time     : n/a   Space: n/a
// Trade-off: Static is great for counters, constants, and utilities, but overusing it creates
//            global state that is hard to test and reason about. Prefer instance state by default.

class Counter {
    // Instance field: each object has its own "id".
    int id;

    // Static field: ONE shared counter for the whole class. Increments across all objects.
    static int count = 0;

    // Static initializer block: runs ONCE when the class is first loaded, before any object.
    static {
        System.out.println("Counter class loaded");
    }

    Counter() {
        count++;                        // bump the shared total...
        this.id = count;                // ...and record this object's own number
    }

    // Static method: callable as Counter.total() without any object. It can only touch static
    // members directly (it has no "this").
    static int total() {
        return count;
    }
}

public class StaticMembers {
    // Constants are the classic static use: "static final" = one shared, unchangeable value.
    static final double PI = 3.14159;

    public static void main(String[] args) {
        Counter a = new Counter();      // count -> 1, a.id = 1
        Counter b = new Counter();      // count -> 2, b.id = 2
        Counter c = new Counter();      // count -> 3, c.id = 3

        System.out.println("a.id=" + a.id + " b.id=" + b.id + " c.id=" + c.id);
        // expected: a.id=1 b.id=2 c.id=3

        System.out.println("Total objects: " + Counter.total()); // expected: Total objects: 3
        System.out.println("PI = " + PI);                        // expected: PI = 3.14159

        // Deeper dive - the rules that catch people out:
        // 1) A static method has NO 'this', so it cannot touch instance fields/methods directly -
        //    it would not know WHICH object. (That is why main is static: no object exists at start.)
        // 2) An instance method CAN read static members freely (there is only one shared copy).
        // 3) Static methods are HIDDEN, not overridden - resolved by the reference type, so they are
        //    not polymorphic (see Polymorphism.java).
        // 4) Initialization order when a class first loads: static fields + static blocks run once,
        //    top to bottom; instance fields + instance blocks + constructor run per object.
        // 5) Overusing static creates global mutable state that is hard to test - reach for it for
        //    genuine constants, counters, and stateless utility methods (e.g. Math.max).
    }
}
