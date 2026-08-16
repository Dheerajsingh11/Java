// Problem  : Reuse and extend an existing class instead of rewriting shared behaviour.
// Approach : A subclass "extends" a superclass, inherits its members, and uses "super" to reach
//            the parent's constructor and methods.
// Intuition: "is-a" relationship - a Dog IS-A Animal, so it should get Animal's behaviour for free
//            and only add or change what is special about a Dog.
// Time     : n/a   Space: n/a
// Trade-off: Inheritance removes duplication, but couples subclass to superclass; prefer it only
//            for genuine "is-a" relationships (composition is better for "has-a").

// Superclass (parent / base class).
class Animal {
    String name;

    Animal(String name) {
        this.name = name;
    }

    void eat() {
        System.out.println(name + " is eating");
    }

    void sound() {
        System.out.println(name + " makes a generic sound");
    }
}

// Subclass (child / derived class). "extends" pulls in Animal's fields and methods.
class Dog extends Animal {
    Dog(String name) {
        // "super(...)" calls the parent constructor. It must be the first line, because the parent
        // part of the object must be built before the child part.
        super(name);
    }

    // Add behaviour that only Dogs have.
    void fetch() {
        System.out.println(name + " fetches the ball");
    }

    // Override: replace the inherited version with a Dog-specific one.
    @Override
    void sound() {
        super.sound();                         // optionally reuse the parent's version...
        System.out.println(name + " barks");   // ...then extend it
    }
}

public class Inheritance {
    public static void main(String[] args) {
        Dog d = new Dog("Rex");
        d.eat();    // inherited from Animal        -> Rex is eating
        d.fetch();  // defined in Dog               -> Rex fetches the ball
        d.sound();  // overridden in Dog            -> Rex makes a generic sound / Rex barks

        // Deeper dive - upcasting and construction order:
        // A Dog IS-A Animal, so a Dog reference can be stored in an Animal variable (UPCASTING).
        // This is safe and implicit. The reference type is Animal, but the OBJECT is still a Dog,
        // so overridden methods still run the Dog version (dynamic dispatch - see Polymorphism.java).
        Animal asAnimal = d;        // upcast: no cast needed
        asAnimal.sound();           // still runs Dog.sound() because the object is a Dog
        // asAnimal.fetch();        // <- won't compile: the Animal TYPE has no fetch(); the compiler
        //                          //    checks against the reference type, not the runtime object.

        // Construction order: creating a Dog first runs Animal's constructor, THEN Dog's body.
        // The parent part of an object is always built before the child part (that is why super(...)
        // must be the first statement). Every class implicitly extends java.lang.Object.
    }
}
