// Problem  : The smallest possible Java program - print one line of text.
// Approach : A single public class with a main method, the fixed entry point of every Java app.
// Intuition: Java runs by loading a class and calling its main method; everything else hangs off
//            that starting point.
// Time     : O(1)   Space: O(1)
// Trade-off: This is the minimum ceremony Java requires - there are no free-floating statements;
//            all code must live inside a class and (to run) be reachable from main.

// The class name MUST match the file name (HelloWorld -> HelloWorld.java) for a public class.
// The JVM uses the class name, not the file name, to find main - but javac enforces the match.
public class HelloWorld {

    // main is the program's entry point. Its signature is fixed and the JVM looks for it exactly:
    //   public  -> the JVM (outside this class) must be able to call it
    //   static  -> it runs WITHOUT first creating a HelloWorld object (there is none yet at start)
    //   void    -> it returns nothing to the JVM
    //   String[] args -> command-line arguments (e.g. "java HelloWorld a b" gives {"a","b"})
    public static void main(String[] args) {
        // println = "print line": writes the text, then moves to a new line.
        // System.out is the standard output stream (usually your console).
        System.out.println("Hello, World!"); // expected output: Hello, World!
    }
}
