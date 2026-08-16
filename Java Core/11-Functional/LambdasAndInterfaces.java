// Problem  : Pass BEHAVIOUR as data using lambdas, functional interfaces, and method references.
// Approach : Show a custom functional interface, the built-in ones, lambda syntax, and method refs.
// Intuition: A lambda is a compact anonymous function. A "functional interface" (exactly one abstract
//            method) is the TYPE a lambda fills, so you can store and pass functions as objects.
// Time     : n/a   Space: n/a
// Trade-off: Lambdas make code concise and enable the Streams API and callbacks, replacing verbose
//            anonymous classes. Overuse (deeply nested lambdas) can hurt readability - keep them small.

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdasAndInterfaces {

    // A custom functional interface: exactly ONE abstract method -> a lambda target.
    @FunctionalInterface
    interface Operation {
        int apply(int a, int b);
    }

    static int compute(int a, int b, Operation op) { // takes behaviour as a parameter
        return op.apply(a, b);
    }

    public static void main(String[] args) {
        // Lambda implementing our interface (replaces a whole anonymous class).
        Operation add = (a, b) -> a + b;
        Operation mul = (a, b) -> a * b;
        System.out.println(compute(3, 4, add)); // 7
        System.out.println(compute(3, 4, mul)); // 12

        // ---- Built-in functional interfaces (java.util.function) ----
        Predicate<Integer> isEven = n -> n % 2 == 0;      // T -> boolean
        Function<String, Integer> length = s -> s.length();// T -> R
        BiFunction<Integer, Integer, Integer> sum = (a, b) -> a + b; // (T,U) -> R
        Supplier<String> greet = () -> "hi";               // () -> R

        System.out.println(isEven.test(10));   // true
        System.out.println(length.apply("hello")); // 5
        System.out.println(sum.apply(2, 3));   // 5
        System.out.println(greet.get());       // hi

        // ---- Method references: shorthand for a lambda that just calls one method ----
        Function<String, Integer> parse = Integer::parseInt;      // static method ref
        Function<String, String> upper = String::toUpperCase;     // instance method of the argument
        System.out.println(parse.apply("123") + 1); // 124
        System.out.println(upper.apply("abc"));      // ABC

        // Composition: chain functions with andThen / compose.
        Function<Integer, Integer> plus1 = x -> x + 1;
        Function<Integer, Integer> times2 = x -> x * 2;
        System.out.println(plus1.andThen(times2).apply(3)); // (3+1)*2 = 8
    }
}
