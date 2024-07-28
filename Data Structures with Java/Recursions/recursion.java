// Basic recursion function
public class recursion {

    static void fun1(int n) {
        if (n == 0) {
            return; // base case to stop recursion
        }
        System.out.println("Fun1");
        fun1(n - 1); // recursion call
    }

    public static void main(String[] args) {
        System.out.println("Main");
        fun1(5); // calling the fun1
        System.out.println("After Fun1");
    }
}
