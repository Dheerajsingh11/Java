// Problem  : Build a class whose instances can NEVER change after construction.
// Approach : Apply the five rules for immutability, including defensive copying of mutable fields.
// Intuition: If state cannot change, whole categories of bug become impossible: no thread can see a
//            half-updated object, no caller can corrupt your internals, and a cached hash can never
//            go stale. Immutability trades a little allocation for a large amount of certainty.
// Time     : construction copies mutable inputs O(n)   Space: O(n) for the copies
// Trade-off: Every "change" allocates a new object, which costs memory and GC pressure - a real
//            concern only in hot loops (this is exactly why StringBuilder exists alongside String).
//            In return you get thread safety for free and objects that are always valid.

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// RULE 1: make the class final, so nobody can subclass it and add mutable state or override methods.
final class ImmutablePerson {

    // RULE 2: all fields private AND final - they cannot be reassigned after construction.
    private final String name;              // String is itself immutable - safe to store directly
    private final int age;
    private final List<String> hobbies;     // MUTABLE type - needs defensive copying

    ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;

        // RULE 3: DEFENSIVE COPY ON THE WAY IN.
        // Without this, the caller keeps a reference to the same list and can mutate our state
        // afterwards - the object would only *look* immutable.
        this.hobbies = new ArrayList<>(hobbies);
    }

    public String getName() { return name; }
    public int getAge()     { return age; }

    // RULE 4: DEFENSIVE COPY (or an unmodifiable view) ON THE WAY OUT.
    // Returning the internal list directly would let a caller do getHobbies().add(...) and modify us.
    public List<String> getHobbies() {
        return Collections.unmodifiableList(hobbies);
    }

    // RULE 5: no setters. "Changing" a value returns a NEW object instead of mutating this one -
    // exactly how String.toUpperCase() and LocalDate.plusDays() behave.
    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(name, newAge, hobbies);
    }

    @Override
    public String toString() { return name + " (" + age + ") " + hobbies; }
}

public class ImmutableClass {
    public static void main(String[] args) {
        List<String> hobbies = new ArrayList<>(List.of("chess", "cycling"));
        ImmutablePerson p = new ImmutablePerson("Asha", 30, hobbies);

        // Attack 1: mutate the list we passed in. Defensive copy on the way IN defeats it.
        hobbies.add("skydiving");
        System.out.println(p);                        // expected: Asha (30) [chess, cycling]

        // Attack 2: mutate the list we got back. The unmodifiable view defeats it.
        try {
            p.getHobbies().add("hacking");
        } catch (UnsupportedOperationException e) {
            System.out.println("blocked: cannot modify the returned list");
        }

        // "Changing" produces a new object; the original is untouched.
        ImmutablePerson older = p.withAge(31);
        System.out.println("original: " + p);         // Asha (30) [chess, cycling]
        System.out.println("derived : " + older);     // Asha (31) [chess, cycling]
    }
}

/* ------------------------------ WHY BOTHER ------------------------------
 * - THREAD SAFETY FOR FREE. An object that never changes cannot be seen half-updated, so it needs
 *   no synchronization at all. This is the single biggest practical benefit.
 * - SAFE AS A MAP KEY. A mutable key whose fields change after insertion gets a different hash and
 *   becomes unreachable (see EqualsAndHashCode.java). Immutable keys cannot do this.
 * - ALWAYS VALID. Validate once in the constructor and the object is correct forever - no setter can
 *   later put it into a bad state.
 * - FREELY SHAREABLE. No need to copy "just in case" a caller modifies it.
 *
 * Java's own immutable types: String, Integer and the other wrappers, BigDecimal, LocalDate and the
 * whole java.time API, and records (which give you rules 1, 2 and 5 automatically - though a record
 * holding a List still needs defensive copying, since the record only makes the REFERENCE final).
 *
 * WHEN NOT TO: large objects modified frequently in a tight loop (the allocation cost dominates -
 * use a builder), or genuine entities whose identity persists while their state evolves.
 * ----------------------------------------------------------------------- */
