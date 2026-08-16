// Problem  : Write code that works with ANY type while staying type-safe at compile time.
// Approach : Show a generic class, a generic method, bounded type parameters, and wildcards.
// Intuition: Generics let you parameterize a class/method by a type (like <T>), so the compiler
//            checks types for you and you avoid casting. "One implementation, many types."
// Time     : n/a (compile-time feature; erased at runtime)   Space: n/a
// Trade-off: Type safety and no casts, but Java uses TYPE ERASURE - generic type info is removed at
//            runtime, so you cannot do `new T()` or `instanceof List<String>`. Wildcards add
//            flexibility for reading/writing collections of related types.

import java.util.List;

public class GenericsDemo {

    // ---- Generic class: Box holds a value of any type T ----
    static class Box<T> {
        private T value;
        void set(T value) { this.value = value; }
        T get() { return value; }             // returns T - no cast needed by the caller
    }

    // ---- Generic method: works for any T, infers T from the argument ----
    static <T> T firstOf(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    // ---- Bounded type: T must be Comparable so we can call compareTo ----
    static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    // ---- Wildcard: accept a list of ANY type and just read/print it ----
    static void printAll(List<?> list) {       // '?' = unknown type; can read as Object, cannot add
        for (Object o : list) System.out.print(o + " ");
        System.out.println();
    }

    public static void main(String[] args) {
        Box<String> sb = new Box<>();
        sb.set("hello");
        System.out.println(sb.get().toUpperCase()); // HELLO - no cast, compiler knows it's a String

        Box<Integer> ib = new Box<>();
        ib.set(42);
        System.out.println(ib.get() + 1);            // 43

        System.out.println(firstOf(List.of(10, 20, 30))); // 10
        System.out.println(max(3, 9));                    // 9
        System.out.println(max("apple", "banana"));       // banana (lexicographic)

        printAll(List.of(1, 2, 3));                       // 1 2 3
        printAll(List.of("a", "b"));                      // a b

        // Compile-time safety: sb.set(42) would NOT compile - Box<String> rejects an Integer.
    }
}
