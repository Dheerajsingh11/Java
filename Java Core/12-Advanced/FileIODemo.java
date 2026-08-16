// Problem  : Read and write text files using modern Java NIO, with proper resource handling.
// Approach : Use java.nio.file.Files for simple whole-file reads/writes, and try-with-resources for
//            buffered line-by-line I/O.
// Intuition: Files.write/readAllLines cover the common "small file" case in one call. For large files
//            or streaming, a buffered reader/writer processes line by line without loading everything.
// Time     : O(size of file)   Space: O(size) for whole-file reads; O(line) for buffered streaming
// Trade-off: NIO (java.nio.file) is cleaner and more capable than the old java.io File API. Always
//            use try-with-resources so streams close even on error (avoids file-handle leaks).
// NOTE     : writes/reads a temp file in the system temp directory; requires filesystem access to run.

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileIODemo {
    public static void main(String[] args) throws IOException {
        Path file = Files.createTempFile("demo", ".txt"); // a temp file we can safely write to

        // ---- Write: simplest whole-file API ----
        List<String> lines = List.of("first line", "second line", "third line");
        Files.write(file, lines); // creates/overwrites and writes all lines

        // ---- Read: whole file into a List ----
        List<String> read = Files.readAllLines(file);
        System.out.println("lines read: " + read.size()); // 3
        System.out.println("first     : " + read.get(0)); // first line

        // ---- Buffered, line-by-line (for large files) with try-with-resources ----
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            int n = 0;
            while ((line = br.readLine()) != null) { // null signals end of file
                n++;
            }
            System.out.println("counted (buffered): " + n); // 3
        } // br auto-closed here, even if an exception occurred

        Files.deleteIfExists(file); // clean up the temp file
        System.out.println("done");
    }
}
