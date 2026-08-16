// Problem  : Control who can see and use each class, field, and method.
// Approach : Demonstrate the four access levels and the practical rule for choosing between them.
// Intuition: Every member you expose becomes a promise you must keep. Access modifiers let you keep
//            the promise SMALL - publish only what callers need, so everything else stays free to
//            change without breaking anyone.
// Time     : n/a (compile-time only)   Space: n/a
// Trade-off: Tighter access means more freedom to refactor later but more friction now (you must add
//            accessors deliberately). The cost of being too open is permanent: once something is
//            public, every change risks breaking callers you cannot see.

public class AccessModifiers {

    public    int publicField    = 1;   // everyone, everywhere
    protected int protectedField = 2;   // same package + SUBCLASSES (even in other packages)
              int defaultField   = 3;   // no keyword = "package-private": same package only
    private   int privateField   = 4;   // this class only

    public    void publicMethod()    { System.out.println("public: usable anywhere"); }
    protected void protectedMethod() { System.out.println("protected: package + subclasses"); }
              void defaultMethod()   { System.out.println("package-private: same package only"); }
    private   void privateMethod()   { System.out.println("private: inside this class only"); }

    public static void main(String[] args) {
        AccessModifiers a = new AccessModifiers();
        // All four are reachable HERE because we are inside the declaring class.
        System.out.println(a.publicField + " " + a.protectedField + " "
                         + a.defaultField + " " + a.privateField);
        a.publicMethod();
        a.privateMethod();   // legal here; a caller in another class could not do this
    }
}

/* ---------------------------- THE VISIBILITY TABLE ----------------------------
 *
 * Modifier      | Same class | Same package | Subclass (other pkg) | Anywhere
 * --------------|------------|--------------|----------------------|----------
 * private       |    yes     |      no      |         no           |    no
 * (default)     |    yes     |     yes      |         no           |    no
 * protected     |    yes     |     yes      |        yes           |    no
 * public        |    yes     |     yes      |        yes           |   yes
 *
 * Note "default" (package-private) is the level you get by writing NO keyword. It is a real choice,
 * not an absence of one - it says "this is an implementation detail shared within this package".
 *
 * ------------------------------- HOW TO CHOOSE -------------------------------
 *
 * Start at private and widen ONLY when something actually needs access. Reasons:
 *
 *   - FIELDS should be private almost always. A public field is an unbreakable promise about your
 *     internal representation; a private field with an accessor lets you add validation, change the
 *     storage type, or compute the value later - without touching a single caller. This is
 *     encapsulation in practice (see Encapsulation.java).
 *
 *   - protected is narrower than it looks. It exposes a member to every future subclass, which
 *     means it is part of your API for inheritors and cannot be changed freely. Use it only for
 *     members you genuinely intend subclasses to use or override.
 *
 *   - public is a commitment. In a library it is effectively permanent: you cannot know who depends
 *     on it. "Can I make this private?" is worth asking about every public member.
 *
 * A top-level class may only be public or package-private (not private/protected). Interface
 * members are implicitly public - that is the point of an interface.
 *
 * ---------------------------------- PITFALLS ---------------------------------
 *
 *   - Making everything public "to avoid problems" - it guarantees the opposite later.
 *   - Returning a private mutable field directly (an array or List) leaks it: the caller can modify
 *     your internals through the reference. Return a copy or an unmodifiable view.
 *   - Assuming private means secure. It is a COMPILE-TIME check, not a security boundary -
 *     reflection can bypass it. Use it for design, not for secrets.
 * --------------------------------------------------------------------------- */
