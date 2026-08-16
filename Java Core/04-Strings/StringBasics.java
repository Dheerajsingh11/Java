// Problem  : Understand how Java Strings behave - immutability, the string pool, and common methods.
// Approach : Demonstrate identity vs equality, immutability, and the frequently-used String API.
// Intuition: A String is immutable: every "modifying" method returns a NEW String, leaving the
//            original untouched. String literals are cached in a pool so identical literals share
//            one object.
// Time     : most methods O(length)   Space: new strings allocate O(length)
// Trade-off: Immutability makes Strings safe to share and use as map keys, but building them in a
//            loop wastes memory (each concat copies) - use StringBuilder for that (StringBuilderDemo.java).

public class StringBasics {
    public static void main(String[] args) {
        // ---- Identity vs equality ----
        String a = "hello";           // literal -> goes in the string pool
        String b = "hello";           // same literal -> SAME pooled object
        String c = new String("hello"); // 'new' forces a distinct object on the heap

        System.out.println(a == b);        // true  - same pooled reference
        System.out.println(a == c);        // false - different objects
        System.out.println(a.equals(c));   // true  - same CONTENTS (always use equals for text)

        // ---- Immutability ----
        String s = "abc";
        s.toUpperCase();                   // returns "ABC" but we ignore it -> s is unchanged
        System.out.println(s);             // abc
        s = s.toUpperCase();               // reassign to keep the new string
        System.out.println(s);             // ABC

        // ---- Common methods ----
        String t = "Hello, World";
        System.out.println(t.length());            // 12
        System.out.println(t.charAt(1));           // e
        System.out.println(t.indexOf("World"));    // 7
        System.out.println(t.substring(7));        // World
        System.out.println(t.substring(0, 5));     // Hello  (end index is EXCLUSIVE)
        System.out.println(t.replace('l', 'L'));   // HeLLo, WorLd
        System.out.println(t.toLowerCase());       // hello, world
        System.out.println("  trim me  ".trim());  // "trim me"
        System.out.println(String.join("-", "a", "b", "c")); // a-b-c
        System.out.println("a,b,c".split(",").length);       // 3

        // Edge: charAt/substring throw StringIndexOutOfBoundsException on bad indices - check length.
    }
}
