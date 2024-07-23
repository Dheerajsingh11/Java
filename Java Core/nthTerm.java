
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class nthTerm {

    public static void main(String[] args) throws IOException {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(System.in))) {
            int a = Integer.parseInt(bf.readLine());
            int n = Integer.parseInt(bf.readLine());
            int d = Integer.parseInt(bf.readLine());
            int res = (a + ((n - 1) * d));
            System.out.println("\n" + res);
        }
    }
}
