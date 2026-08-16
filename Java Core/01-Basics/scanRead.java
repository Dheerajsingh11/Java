// Problem  : Read typed console input of several types using Scanner.
// Approach : Wrap System.in in a Scanner and use nextLine / nextInt / nextFloat / etc.
// Intuition: Scanner "scans" the input stream and parses tokens into the type you ask for, so you
//            do not convert strings yourself.
// Time     : O(k) per token (k = characters read)   Space: O(1)
// Trade-off: Scanner is convenient and type-aware but slower than BufferedReader because of the
//            parsing/regex work per token. For heavy competitive-programming input, prefer
//            BufferedReader (see buffRead.java). For everyday input, Scanner wins on readability.
// INPUT    : provide, on separate lines: a text line, an int, a float, a double, a boolean.
//            Example stdin:  hello\n 42\n 3.5\n 2.71828\n true

import java.util.Scanner;

public class scanRead {
    public static void main(String[] args) {
        // try-with-resources: the Scanner is auto-closed at the end of the block, even on error.
        try (Scanner s = new Scanner(System.in)) {
            String s1 = s.nextLine();       // reads the whole line up to the newline
            System.out.println("String : " + s1);

            int i = s.nextInt();            // parses the next token as int (throws if not an int)
            System.out.println("int    : " + i);

            float f = s.nextFloat();        // next token as float
            System.out.println("float  : " + f);

            double d = s.nextDouble();      // next token as double
            System.out.println("double : " + d);

            boolean b = s.nextBoolean();    // next token must be "true" or "false"
            System.out.println("boolean: " + b);
        }
        // Common gotcha: mixing nextInt() then nextLine() leaves the leftover newline in the buffer,
        // so the following nextLine() reads an empty string. Fix: add an extra s.nextLine() to
        // consume that newline, or read everything with nextLine() and parse manually.
    }
}
