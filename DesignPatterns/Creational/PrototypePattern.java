// Pattern  : PROTOTYPE (Creational)
// Problem  : Create new objects by COPYING an existing one, rather than constructing from scratch.
// Approach : A prototype exposes a copy() method; new instances are clones that can then be tweaked.
//            Real domain: document templates (invoice, contract) cloned per customer.
// Intuition: Some objects are expensive to build - they load a template from disk, hit a database,
//            or run heavy setup. If you need 500 slightly different copies, doing that work 500 times
//            is waste. Build ONE fully configured prototype, then clone it and adjust the few fields
//            that differ. It also lets you copy an object whose exact type you do not know.
// Time     : shallow copy O(fields); deep copy O(size of the object graph)
// Space    : O(size of the copy)
// Trade-off: THE DEEP-vs-SHALLOW DISTINCTION IS THE WHOLE PATTERN, and getting it wrong produces
//            bugs where two "independent" objects silently share mutable state. Java's Cloneable /
//            Object.clone() is widely considered broken (see the note at the bottom); a plain copy
//            constructor or a copy() method is clearer and safer.
// Real use  : Object.clone(), ArrayList.clone(), prototype-scoped Spring beans, cloning configuration
//            objects, game engines spawning entities from a template.

import java.util.ArrayList;
import java.util.List;

class DocumentTemplate {
    private String title;
    private String bodyTemplate;
    private List<String> sections;            // MUTABLE - this is where copying gets interesting

    DocumentTemplate(String title, String bodyTemplate, List<String> sections) {
        System.out.println("  (expensive: loading template from disk...)");
        this.title = title;
        this.bodyTemplate = bodyTemplate;
        this.sections = new ArrayList<>(sections);
    }

    // Private no-load constructor, so the copies below skip the expensive setup.
    private DocumentTemplate() { }

    // ---- SHALLOW COPY: copies each field's VALUE. For a reference field that copies the POINTER,
    //      so both objects end up sharing the SAME list. This is the genuine hazard - the two
    //      objects look independent but are not. ----
    DocumentTemplate shallowCopy() {
        DocumentTemplate copy = new DocumentTemplate();
        copy.title = this.title;
        copy.bodyTemplate = this.bodyTemplate;
        copy.sections = this.sections;        // <-- SHARED reference, NOT a new list
        return copy;
    }

    // ---- DEEP COPY: duplicates the mutable parts too, so the objects are genuinely independent.
    //      This is the version you almost always want. ----
    DocumentTemplate copy() {
        // Note: no "loading from disk" message - the expensive work is skipped entirely, which is
        // the performance argument for the pattern.
        DocumentTemplate copy = new DocumentTemplate();
        copy.title = this.title;
        copy.bodyTemplate = this.bodyTemplate;
        copy.sections = new ArrayList<>(this.sections);    // NEW list -> independent
        return copy;
    }

    DocumentTemplate withTitle(String title) { this.title = title; return this; }
    void addSection(String s) { sections.add(s); }

    @Override
    public String toString() { return title + " " + sections; }
}

// A prototype REGISTRY: keep one configured instance per kind, hand out clones on demand.
class TemplateRegistry {
    private final java.util.Map<String, DocumentTemplate> prototypes = new java.util.HashMap<>();

    void register(String key, DocumentTemplate prototype) { prototypes.put(key, prototype); }

    DocumentTemplate create(String key) {
        DocumentTemplate proto = prototypes.get(key);
        if (proto == null) throw new IllegalArgumentException("no template: " + key);
        return proto.copy();       // callers get an independent copy, never the prototype itself
    }
}

public class PrototypePattern {
    public static void main(String[] args) {

        System.out.println("Building the prototype once:");
        DocumentTemplate invoice = new DocumentTemplate(
                "Invoice Template", "Dear {name}, your invoice is attached.",
                List.of("Header", "LineItems", "Total"));

        // ---- The hazard: a shallow copy shares the list, so mutating the COPY corrupts the ORIGINAL ----
        System.out.println("SHALLOW copy - watch the original change too:");
        DocumentTemplate shallow = invoice.shallowCopy();
        shallow.addSection("SharedSection");          // added to the COPY only...
        System.out.println("  copy    : " + shallow);
        System.out.println("  original: " + invoice + "   <-- CORRUPTED: it shares the same list");

        // ---- Deep copy: cheap (no disk load) and fully independent ----
        System.out.println("DEEP copy (no expensive load message above):");
        DocumentTemplate forAcme = invoice.copy().withTitle("Invoice - ACME Ltd");
        forAcme.addSection("AcmeTerms");
        System.out.println("  clone   : " + forAcme);
        System.out.println("  original: " + invoice + "   <- unchanged, genuinely independent");

        // ---- Registry: build once, clone many ----
        System.out.println("Registry - one prototype, many customers:");
        TemplateRegistry registry = new TemplateRegistry();
        registry.register("invoice", invoice);
        for (String customer : new String[]{ "ACME", "Globex", "Initech" }) {
            DocumentTemplate doc = registry.create("invoice").withTitle("Invoice - " + customer);
            System.out.println("  " + doc);
        }
        System.out.println("  prototype still: " + invoice);
    }
}

/* ---------------------------- SHALLOW vs DEEP ----------------------------
 * SHALLOW - copies each field's value. For primitives that is the value; for references it is the
 *           POINTER, so both objects share the same underlying object.
 *           Safe only when every field is primitive or IMMUTABLE (String, Integer, LocalDate).
 * DEEP    - recursively copies the mutable objects too, giving full independence.
 *           Costs more, and needs care with cycles in the object graph.
 *
 * The bug this prevents is nasty precisely because it is invisible: two objects look separate,
 * tests pass on primitives, and then a mutation to one silently changes the other in production.
 *
 * ----------------------- WHY NOT Cloneable / Object.clone() -----------------
 * Java's built-in cloning is widely regarded as a design mistake:
 *   - `Cloneable` is a MARKER interface with no clone() method on it, so it declares nothing.
 *   - Object.clone() is protected, and throws CloneNotSupportedException you must catch.
 *   - It performs a SHALLOW copy by default, which is rarely what you want.
 *   - It bypasses constructors, so invariants and final fields are not handled normally.
 * Prefer a COPY CONSTRUCTOR or a static factory (`DocumentTemplate.copyOf(other)`), as above.
 *
 * ------------------------------ WHEN NOT TO USE ----------------------------
 * - Objects are cheap to construct - just call the constructor.
 * - The object is IMMUTABLE - there is nothing to copy; share the same instance safely.
 * - The object graph is deep or cyclic, making a correct deep copy harder than rebuilding.
 * --------------------------------------------------------------------------- */
