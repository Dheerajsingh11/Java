import java.util.Scanner;

public class scanRead {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String s1 = s.nextLine();
        System.out.println("Input: " + s1);

        int i = s.nextInt();
        System.out.println("Input: " + i);

        float f = s.nextFloat();
        System.out.println("Input: " + f);

        double d = s.nextDouble();
        System.out.println("Input: " + d);

        boolean b = s.nextBoolean();
        System.out.println("Input: " + b);
        s.close();
    }
}

/*
 * Differences Between BufferedReader and Scanner:
 * ===============================================
 * 1. BufferedReader is a very basic way to read the input generally used to
 * read the stream of characters. It gives an edge over Scanner as it is faster
 * than Scanner because Scanner does lots of post-processing for parsing the
 * input; as seen in nextInt(), nextFloat()
 * 2. BufferedReader is more flexible as we can specify the size of stream input
 * to be read. (In general, it is there that BufferedReader reads larger input
 * than Scanner)
 * These two factors come into play when we are reading larger input. In
 * general, the Scanner Class serves the input.
 * 3. BufferedReader is preferred as it is synchronized. While dealing with
 * multiple threads it is preferred.
 * For decent input, and easy readability. The Scanner is preferred over
 * BufferedReader.
 */
