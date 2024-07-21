import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class buffRead {
    public static void main(String[] args) throws IOException {
        // Creating buffered Reader object
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // InputStreamReader converts
                                                                                      // bytes to stream of characters
        String str = reader.readLine();
        int a = Integer.parseInt(reader.readLine());
        System.out.println("Input string: " + str);
        System.out.println("Input integer: " + a);
        reader.close();
    }
}
