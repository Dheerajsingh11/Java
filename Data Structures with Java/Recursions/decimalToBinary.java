
public class decimalToBinary {

    public static void main(String[] args) {
        int n = 10;
        decToBin(n);
    }

    static void decToBin(int n) {
        if (n == 0) {
            return;
        }
        decToBin(n / 2);
        System.out.println(n % 2);
    }
}
