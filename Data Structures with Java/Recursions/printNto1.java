
public class printNto1 {

    public static void main(String[] args) {
        int n = 5;
        prrintNto1(n);
    }

    static void prrintNto1(int n) {
        if (n == 0) {
            return;
        }
        System.out.println(n);
        prrintNto1(n - 1);
    }
}
