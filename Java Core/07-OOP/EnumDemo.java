// Problem  : Represent a fixed, known set of constants safely (not with magic ints/strings).
// Approach : Define an enum with fields, a constructor, and a method; iterate its values.
// Intuition: An enum is a special class whose only instances are the ones you list. The compiler
//            guarantees a variable can only hold one of those - impossible with int codes.
// Time     : n/a   Space: n/a
// Trade-off: Enums are type-safe, self-documenting, and can carry data/behaviour, at the cost of
//            being fixed at compile time (you cannot add a new constant at run time).

// Each constant is actually a singleton object of this enum type.
enum Planet {
    // Constants call the enum constructor with their own data (mass-independent radius in km here).
    MERCURY(2440),
    EARTH(6371),
    JUPITER(69911);

    private final int radiusKm;         // enums can have fields...

    Planet(int radiusKm) {              // ...and a (implicitly private) constructor
        this.radiusKm = radiusKm;
    }

    int radius() {                      // ...and methods
        return radiusKm;
    }
}

public class EnumDemo {
    public static void main(String[] args) {
        Planet p = Planet.EARTH;

        // switch on an enum is clean and the compiler can warn about missing cases.
        switch (p) {
            case MERCURY -> System.out.println("Closest to the Sun");
            case EARTH   -> System.out.println("Home");
            case JUPITER -> System.out.println("Largest");
        }
        // expected: Home

        System.out.println(p.name() + " radius = " + p.radius() + " km"); // EARTH radius = 6371 km
        System.out.println("ordinal (position) = " + p.ordinal());        // expected: 1

        // values() returns every constant, in declaration order.
        for (Planet each : Planet.values()) {
            System.out.println(each + " -> " + each.radius());
        }
        // expected: MERCURY -> 2440 / EARTH -> 6371 / JUPITER -> 69911
    }
}
