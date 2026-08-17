// Principle : LISKOV SUBSTITUTION (the L in SOLID)
// Statement : Objects of a subclass must be usable ANYWHERE the superclass is expected, without the
//             caller noticing the difference.
// Problem   : Inheritance that satisfies the COMPILER but breaks the CONTRACT. Code that works with
//             the parent silently misbehaves with the child.
// Intuition : "Is-a" in English is not the same as "is-a" in code. A square IS a rectangle to a
//             mathematician - but a Rectangle whose width can be set independently of its height is
//             a BEHAVIOURAL contract, and a Square cannot honour it. LSP is about behaviour, not
//             vocabulary: if a subclass must weaken a guarantee, it is not a subtype.
// Benefit   : Polymorphism becomes trustworthy. Without LSP, callers need instanceof checks and the
//             whole point of the abstraction evaporates.
// Trade-off : Honouring it sometimes means abandoning an inheritance relationship that feels natural.
//             Composition or a shared interface is usually the answer.

import java.util.List;

// ============================================================================
// BEFORE - the classic Square/Rectangle violation
// ============================================================================
class RectangleBefore {
    protected int width, height;

    void setWidth(int width)   { this.width = width; }
    void setHeight(int height) { this.height = height; }
    int area() { return width * height; }

    // IMPLIED CONTRACT: setting the width does NOT change the height. Every caller relies on it,
    // even though nothing in the code states it.
}

class SquareBefore extends RectangleBefore {
    // A square must keep its sides equal, so it is FORCED to break the parent's contract.
    @Override void setWidth(int width)   { this.width = width;  this.height = width; }
    @Override void setHeight(int height) { this.width = height; this.height = height; }
}

// ============================================================================
// AFTER - option 1: a common interface, no inheritance between them
// ============================================================================
interface ShapeArea { int area(); String name(); }

// IMMUTABLE, which removes the setter problem entirely: there is no way to violate an invariant
// that can never be changed after construction.
record ImmutableRectangle(int width, int height) implements ShapeArea {
    public int area() { return width * height; }
    public String name() { return "Rectangle " + width + "x" + height; }
    // Returning a NEW object keeps the type honest rather than mutating in place.
    ImmutableRectangle withWidth(int w) { return new ImmutableRectangle(w, height); }
}

record ImmutableSquare(int side) implements ShapeArea {
    public int area() { return side * side; }
    public String name() { return "Square " + side; }
    ImmutableSquare withSide(int s) { return new ImmutableSquare(s); }
}

// ============================================================================
// A SECOND, subtler violation: strengthening a precondition
// ============================================================================
class FileStorage {
    // CONTRACT: accepts any non-null name.
    void save(String filename, String content) {
        System.out.println("      saved " + filename);
    }
}

class RestrictedStorageBefore extends FileStorage {
    @Override
    void save(String filename, String content) {
        // VIOLATION: the subclass demands MORE than the parent promised. Code written against
        // FileStorage now throws when handed this subclass - it is not substitutable.
        if (!filename.endsWith(".txt")) {
            throw new IllegalArgumentException("only .txt files allowed");
        }
        super.save(filename, content);
    }
}

// FIX: express the restriction in the TYPE rather than by weakening an inherited promise.
interface Storage { boolean canStore(String filename); void save(String filename, String content); }

class AnyFileStorage implements Storage {
    public boolean canStore(String filename) { return true; }
    public void save(String f, String c) { System.out.println("      saved " + f); }
}

class TextOnlyStorage implements Storage {
    // The limitation is now VISIBLE and queryable, not a surprise thrown at run time.
    public boolean canStore(String filename) { return filename.endsWith(".txt"); }
    public void save(String f, String c) {
        if (!canStore(f)) throw new IllegalArgumentException("only .txt");
        System.out.println("      saved " + f);
    }
}

public class L_LiskovSubstitution {

    // Written against the PARENT type. It encodes the reasonable assumption that setting width
    // leaves height alone.
    static void resizeAndCheck(RectangleBefore rect) {
        rect.setWidth(5);
        rect.setHeight(4);
        System.out.println("      set 5x4 -> area " + rect.area()
                + (rect.area() == 20 ? "  (as expected)" : "  <-- BROKEN"));
    }

    public static void main(String[] args) {

        System.out.println("BEFORE - the same method, two different outcomes:");
        resizeAndCheck(new RectangleBefore());   // 20, correct
        resizeAndCheck(new SquareBefore());      // 16, because setHeight also changed the width

        System.out.println("    The caller did nothing wrong. The SUBCLASS broke the contract,");
        System.out.println("    which is precisely what LSP forbids.");

        System.out.println("AFTER - a shared interface, no false inheritance:");
        for (ShapeArea s : List.of(new ImmutableRectangle(5, 4), new ImmutableSquare(5))) {
            System.out.println("      " + s.name() + " area=" + s.area());
        }
        System.out.println("    Both are usable anywhere a ShapeArea is expected, with no surprises.");

        System.out.println("SECOND violation - a subclass demanding MORE than its parent:");
        FileStorage storage = new RestrictedStorageBefore();
        storage.save("notes.txt", "ok");
        try {
            storage.save("image.png", "data");   // legal for FileStorage, rejected by the subclass
        } catch (IllegalArgumentException e) {
            System.out.println("      [!] " + e.getMessage() + " - not substitutable");
        }

        System.out.println("    FIXED - the capability is part of the interface and can be asked:");
        for (Storage s : List.of(new AnyFileStorage(), new TextOnlyStorage())) {
            String file = "image.png";
            System.out.println("      " + s.getClass().getSimpleName()
                    + ".canStore(" + file + ") = " + s.canStore(file));
        }
    }
}

/* ------------------------------ THE RULES ------------------------------
 * A subclass must not:
 *   1. STRENGTHEN PRECONDITIONS  - demand more of the caller than the parent did
 *                                  (the .txt-only storage above).
 *   2. WEAKEN POSTCONDITIONS     - promise less than the parent guaranteed.
 *   3. BREAK INVARIANTS          - violate a rule the parent maintained
 *                                  (Square breaking "width and height are independent").
 *   4. THROW NEW EXCEPTION TYPES the caller cannot anticipate.
 *
 * A subclass MAY do the opposite of 1 and 2 - accept more, guarantee more. That is always safe,
 * because existing callers keep working.
 *
 * ------------------------------ HOW TO SPOT IT --------------------------
 * - An override that throws UnsupportedOperationException.
 * - Callers using instanceof to special-case a particular subclass - proof that substitution failed.
 * - An override that ignores a parameter, or silently does nothing.
 * - Tests that pass for the parent and fail for the child.
 * - A subclass overriding a setter to change ANOTHER field.
 *
 * Java's own libraries contain a famous example: Arrays.asList() returns a fixed-size List whose
 * add() throws UnsupportedOperationException - it is not fully substitutable for List. So does
 * Collections.unmodifiableList(). Widely used, still a violation, and a genuine source of bugs.
 *
 * -------------------------------- THE FIX -------------------------------
 * When a subclass cannot honour the contract, the relationship is wrong. Options:
 *   - a shared INTERFACE with no inheritance between the implementations (used above);
 *   - COMPOSITION - the "subclass" holds the other object instead of extending it;
 *   - IMMUTABILITY - no setters means no invariant to break;
 *   - make the capability EXPLICIT (canStore()) so callers can ask instead of being surprised.
 * ------------------------------------------------------------------------- */
