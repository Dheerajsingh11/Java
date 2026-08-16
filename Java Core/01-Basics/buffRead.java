// Problem  : Read console input quickly using BufferedReader.
// Approach : Wrap System.in in an InputStreamReader (bytes -> chars) then a BufferedReader (buffered
//            line reading), and parse the strings ourselves.
// Intuition: BufferedReader reads big chunks into a buffer and hands out lines, avoiding a system
//            call per character. It returns raw strings, so WE convert to numbers.
// Time     : O(k) per line (k = line length)   Space: O(buffer)
// Trade-off: Faster and more flexible than Scanner (great for large input), but you must parse
//            types yourself (Integer.parseInt) and handle IOException. Use it when input volume
//            is large; use Scanner (scanRead.java) when convenience matters more than speed.
// INPUT    : line 1 = any text, line 2 = an integer.  Example stdin:  hello\n 42

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class buffRead {
    // "throws IOException": readLine can fail on an I/O problem; we declare it rather than catch it.
    public static void main(String[] args) throws IOException {
        // Layering: System.in (bytes) -> InputStreamReader (decode to chars) -> BufferedReader (buffer + readLine).
        // try-with-resources closes the reader automatically.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String str = reader.readLine();                 // reads one full line as a String
            int a = Integer.parseInt(reader.readLine());    // read the next line, then parse to int
            // Edge: parseInt throws NumberFormatException if the line is not a valid integer, and
            // readLine returns null at end-of-input (parseInt(null) would then throw) - real code
            // should null-check before parsing.

            System.out.println("Input string : " + str);
            System.out.println("Input integer: " + a);
        }
    }
}
