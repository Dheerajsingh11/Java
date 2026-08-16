// Problem  : Show the difference between a class (blueprint) and an object (instance).
// Approach : Define one class with fields + behaviour, then create objects from it.
// Intuition: A class is a template; each object is a concrete thing built from that template
//            with its own copy of the fields.
// Time     : n/a   Space: n/a
// Trade-off: This is the foundation of OOP - modelling real things as objects instead of
//            scattering related data and functions apart.

// A class is a user-defined blueprint. Here "Car" describes what every car HAS (fields) and
// what every car can DO (methods). No memory for a car exists until we create an object.
class Car {
    // Fields (also called instance variables) - each object gets its OWN copy of these.
    String brand;
    int speed;

    // A method (behaviour). It acts on THIS object's fields.
    void accelerate(int delta) {
        speed += delta;              // change this particular car's speed
    }

    void describe() {
        System.out.println(brand + " is going at " + speed + " km/h");
    }
}

public class ClassAndObject {
    public static void main(String[] args) {
        // "new Car()" allocates a fresh object on the heap and returns a reference to it.
        Car a = new Car();          // object 1
        a.brand = "Toyota";         // set object 1's own fields
        a.speed = 0;

        Car b = new Car();          // object 2 - completely independent from object 1
        b.brand = "Honda";
        b.speed = 20;

        a.accelerate(30);           // changes ONLY object a
        a.describe();               // expected: Toyota is going at 30 km/h
        b.describe();               // expected: Honda is going at 20 km/h

        // Deeper dive - reference vs object (stack vs heap):
        // The variable 'a' lives on the STACK and holds only a REFERENCE (an address). The actual
        // Car object lives on the HEAP. So:
        Car c = a;                  // copies the REFERENCE, not the object -> a and c point to the SAME Car
        c.speed = 999;              // mutating through c is visible through a (same object)
        a.describe();               // expected: Toyota is going at 999 km/h  <- proves aliasing
        System.out.println(a == b); // false: different objects (compares references)
        System.out.println(a == c); // true : same object
        // An unassigned reference is 'null' (points to no object); calling a method on it throws
        // NullPointerException. Objects with no remaining references become eligible for garbage
        // collection - Java frees their memory automatically.
    }
}
