// Pattern  : FLYWEIGHT (Structural)
// Problem  : Support a very large number of similar objects without exhausting memory.
// Approach : Split state into INTRINSIC (shared, immutable) and EXTRINSIC (unique, passed in), then
//            share one instance of the intrinsic part. Real domain: characters in a text editor.
// Intuition: A 100,000-character document does not need 100,000 font objects. The glyph's font,
//            size and colour repeat constantly; only the POSITION differs per character. Store the
//            repeating part once and pass the varying part as an argument, and memory collapses from
//            "per object" to "per distinct combination".
// Time     : O(1) lookup from the factory cache   Space: O(distinct styles) instead of O(characters)
// Trade-off: The object becomes harder to use - callers must supply the extrinsic state on every
//            call, and the flyweight MUST be immutable or sharing corrupts every user at once. Only
//            worth it when the object count is genuinely large; below thousands, the complexity
//            outweighs the saving. This is a memory optimization, not a design clarification.
// Real use  : Java's Integer cache (-128..127), String interning, Boolean.valueOf,
//            java.awt.Font, glyph caches in text rendering, tile/sprite reuse in games.

import java.util.HashMap;
import java.util.Map;

// ---- THE FLYWEIGHT: intrinsic state only. IMMUTABLE, because it is shared by everyone. ----
final class CharacterStyle {
    private final String fontFamily;
    private final int fontSize;
    private final String colour;

    // Private: instances come only from the factory, so sharing cannot be bypassed.
    private CharacterStyle(String fontFamily, int fontSize, String colour) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.colour = colour;
        System.out.println("      (created style " + this + " - this is the expensive part)");
    }

    // The EXTRINSIC state (which character, and where) arrives as arguments - it is NOT stored.
    void render(char ch, int x, int y) {
        // In a real editor this would draw the glyph; here we just show the combination.
        // System.out.printf("  '%c' at (%d,%d) in %s%n", ch, x, y, this);
    }

    @Override public String toString() { return fontFamily + "-" + fontSize + "-" + colour; }

    // ---------------- THE FLYWEIGHT FACTORY: hands out shared instances ----------------
    private static final Map<String, CharacterStyle> CACHE = new HashMap<>();

    static CharacterStyle of(String fontFamily, int fontSize, String colour) {
        String key = fontFamily + "|" + fontSize + "|" + colour;
        // computeIfAbsent creates ONCE per distinct combination and returns the same object after.
        return CACHE.computeIfAbsent(key, k -> new CharacterStyle(fontFamily, fontSize, colour));
    }

    static int distinctStyles() { return CACHE.size(); }
}

// ---- The CONTEXT: one per character. Tiny, because the heavy part is shared. ----
class Glyph {
    private final char ch;                  // extrinsic
    private final int x, y;                 // extrinsic
    private final CharacterStyle style;     // SHARED reference, not a copy

    Glyph(char ch, int x, int y, CharacterStyle style) {
        this.ch = ch; this.x = x; this.y = y; this.style = style;
    }

    void draw() { style.render(ch, x, y); }
    CharacterStyle style() { return style; }
}

public class FlyweightPattern {
    public static void main(String[] args) {

        System.out.println("Typing a document - watch how few styles are actually created:");

        String text = "Hello World! This document is long but uses very few distinct styles.";
        Glyph[] document = new Glyph[text.length()];

        for (int i = 0; i < text.length(); i++) {
            // Most characters share one of three styles; only the position differs.
            CharacterStyle style;
            if (i < 5)              style = CharacterStyle.of("Arial", 14, "black");   // heading
            else if (text.charAt(i) == '!') style = CharacterStyle.of("Arial", 14, "red");    // emphasis
            else                    style = CharacterStyle.of("Arial", 12, "black");   // body

            document[i] = new Glyph(text.charAt(i), i * 8, 0, style);
        }

        for (Glyph g : document) g.draw();

        System.out.println();
        System.out.println("  characters in document : " + document.length);
        System.out.println("  CharacterStyle objects : " + CharacterStyle.distinctStyles());
        System.out.println("  -> " + document.length + " glyphs share just "
                + CharacterStyle.distinctStyles() + " style objects");

        // Proof that the style objects are genuinely THE SAME instance, not equal copies.
        System.out.println();
        System.out.println("Sharing check (== compares identity, not contents):");
        CharacterStyle a = CharacterStyle.of("Arial", 12, "black");
        CharacterStyle b = CharacterStyle.of("Arial", 12, "black");
        System.out.println("  same object? " + (a == b));           // true - reused from the cache
        System.out.println("  glyph 10 and glyph 20 share a style? "
                + (document[10].style() == document[20].style()));

        // ---- Java does this itself: the Integer cache is a flyweight ----
        System.out.println();
        System.out.println("Java's own flyweight - the Integer cache (-128..127):");
        Integer i1 = 127, i2 = 127, i3 = 128, i4 = 128;
        System.out.println("  127 == 127 : " + (i1 == i2) + "   (shared flyweight)");
        System.out.println("  128 == 128 : " + (i3 == i4) + "  (outside the cache, new objects)");
    }
}

/* ---------------------- INTRINSIC vs EXTRINSIC - the whole pattern ----------------------
 * INTRINSIC  state that is SHARED and does not vary between uses.
 *            Here: font family, size, colour. Stored inside the flyweight. Must be IMMUTABLE.
 * EXTRINSIC  state that is UNIQUE to each use.
 *            Here: which character, and its x/y position. Passed in as arguments, never stored.
 *
 * Getting the split right is the design work. Put something varying INSIDE the flyweight and you
 * either lose the sharing or corrupt every other user of that instance.
 *
 * ------------------------------- WHY IMMUTABILITY IS MANDATORY ---------------------------
 * The flyweight is shared by thousands of contexts. A single setter would let one caller change the
 * colour of every character in the document at once. That is why CharacterStyle is `final` with
 * `final` fields and no setters - see Java Core/07-OOP/ImmutableClass.java.
 *
 * ----------------------------------- WHEN NOT TO USE -------------------------------------
 * - Object counts in the hundreds - the memory saving does not repay the complexity.
 * - The state is mostly unique per object; there is nothing meaningful to share.
 * - The objects must be mutable or must have distinct identity (using them as map keys, or
 *   synchronizing on them - two "different" objects that are actually one shared instance would
 *   deadlock or collide).
 * - Modern JVMs are good at allocation; measure before optimizing. This pattern is far less commonly
 *   needed today than when memory was scarce.
 * ------------------------------------------------------------------------------------------ */
