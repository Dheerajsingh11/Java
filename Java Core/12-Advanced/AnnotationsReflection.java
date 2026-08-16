// Problem  : Attach metadata to code (annotations) and inspect/invoke it at run time (reflection).
// Approach : Define a custom runtime annotation, then use reflection to discover and act on it -
//            the exact mechanism behind JUnit, Spring and Jackson.
// Intuition: Annotations are labels the compiler preserves but does not act on. Reflection is the
//            ability to ask a class about itself at run time. Together they let a framework operate
//            on YOUR classes without ever having seen them: "find every method marked @Test and run
//            it" needs no knowledge of your code at compile time.
// Time     : reflection is far slower than a direct call (lookup + security checks + no inlining)
// Space    : O(1)
// Trade-off: Enormous flexibility at the cost of compile-time safety and speed. Reflection turns
//            "method does not exist" from a compile error into a run-time exception, so use it for
//            frameworks and tooling - not for ordinary application logic.

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

// ---- Defining a custom annotation ----
// @Retention(RUNTIME) is essential: the DEFAULT is CLASS, which keeps the annotation in the .class
// file but DISCARDS it before run time - reflection would then never see it.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)              // where it may be applied
@interface Test {
    String name() default "";            // annotations may carry values
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotNull { }

class Calculator {
    @NotNull
    private String label = "calc";
    private int callCount = 0;           // no annotation

    @Test(name = "addition works")
    public void testAdd() { System.out.println("    2 + 3 = " + (2 + 3)); }

    @Test(name = "division works")
    public void testDivide() { System.out.println("    10 / 2 = " + (10 / 2)); }

    public void notATest() { System.out.println("    should NOT run"); }
}

public class AnnotationsReflection {
    public static void main(String[] args) throws Exception {

        Class<?> clazz = Calculator.class;      // the run-time description of the class
        Object instance = clazz.getDeclaredConstructor().newInstance();

        // ---- A miniature test runner: find annotated methods and invoke them ----
        // This is essentially what JUnit does. Note it never mentions Calculator by name.
        System.out.println("Running tests found by reflection:");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                Test t = m.getAnnotation(Test.class);
                System.out.println("  [" + t.name() + "]");
                m.invoke(instance);              // call it dynamically
            }
        }

        // ---- A miniature validator: inspect annotated fields ----
        System.out.println("Fields marked @NotNull:");
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(NotNull.class)) {
                f.setAccessible(true);           // bypass private - reflection can do this
                System.out.println("  " + f.getName() + " = " + f.get(instance));
            }
        }

        // ---- General reflection: what can I ask a class? ----
        System.out.println("class name : " + clazz.getSimpleName());
        System.out.println("methods    : " + clazz.getDeclaredMethods().length);
        System.out.println("fields     : " + clazz.getDeclaredFields().length);
    }
}

/* ------------------------------- BUILT-IN ANNOTATIONS -------------------------------
 *   @Override            compiler checks you really are overriding - catches typos and signature
 *                        mistakes. Always use it.
 *   @Deprecated          marks something as obsolete; callers get a warning.
 *   @SuppressWarnings    silences a specific warning. Use narrowly, never blanket-applied.
 *   @FunctionalInterface compiler checks exactly one abstract method (see 11-Functional).
 *
 * ---------------------------------- RETENTION POLICY --------------------------------
 *   SOURCE   discarded by the compiler        @Override - only useful at compile time
 *   CLASS    kept in the .class file, not loaded at run time  (the DEFAULT)
 *   RUNTIME  visible to reflection            required for anything a framework must discover
 * Choosing CLASS when you meant RUNTIME is the classic "why can't my framework see my annotation?"
 *
 * -------------------------------- WHERE THIS IS USED --------------------------------
 *   JUnit    @Test, @BeforeEach          - finds and runs your test methods
 *   Spring   @Component, @Autowired      - discovers beans and injects dependencies
 *   Jackson  @JsonProperty               - maps fields to JSON names
 *   JPA      @Entity, @Id, @Column       - maps objects to database tables
 *   Lombok   @Getter, @Builder           - generates code at compile time
 * Every one of these works by reading annotations reflectively at startup.
 *
 * ----------------------------------- WHEN NOT TO USE ---------------------------------
 *   - ORDINARY APPLICATION LOGIC. If you can call the method directly, do. Reflection is slower and
 *     moves errors from compile time to run time.
 *   - PERFORMANCE-SENSITIVE PATHS. Reflective calls resist JIT inlining; the gap can be large.
 *   - BYPASSING ENCAPSULATION. setAccessible(true) can reach private state, but doing so in your own
 *     code means the design is wrong - and newer JDKs increasingly restrict it for library internals.
 * ------------------------------------------------------------------------------------ */
