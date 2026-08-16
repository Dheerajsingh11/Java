// Problem  : Handle runtime errors gracefully with try/catch/finally and understand checked vs
//            unchecked exceptions.
// Approach : Show catching, multiple catch blocks, finally, throwing, and a custom exception + try-
//            with-resources.
// Intuition: An exception is an object describing "something went wrong". Throwing it unwinds the
//            stack until a matching catch handles it, so error-handling code is separated from the
//            normal flow instead of littering it with checks.
// Time     : n/a   Space: n/a
// Trade-off: Exceptions cleanly separate error handling from logic, but should signal EXCEPTIONAL
//            conditions - do not use them for ordinary control flow (they are relatively expensive).

public class ExceptionsDemo {

    // A custom CHECKED exception (extends Exception) - callers must handle or declare it.
    static class InsufficientFundsException extends Exception {
        InsufficientFundsException(String msg) { super(msg); }
    }

    static void withdraw(int balance, int amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Tried to withdraw " + amount + " from " + balance);
        }
        System.out.println("Withdrew " + amount);
    }

    public static void main(String[] args) {
        // ---- try / multi-catch / finally ----
        try {
            int[] a = { 1, 2, 3 };
            System.out.println(a[5]);              // throws ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught index error: " + e.getMessage());
        } catch (Exception e) {                    // broader catch must come AFTER specific ones
            System.out.println("Caught something else");
        } finally {
            System.out.println("finally ALWAYS runs (cleanup)"); // runs whether or not an exception occurred
        }

        // ---- Unchecked (RuntimeException) example ----
        try {
            int x = 10 / 0;                        // ArithmeticException (unchecked)
        } catch (ArithmeticException e) {
            System.out.println("Math error: " + e.getMessage()); // / by zero
        }

        // ---- Custom checked exception ----
        try {
            withdraw(100, 250);
        } catch (InsufficientFundsException e) {
            System.out.println("Bank error: " + e.getMessage());
        }

        // ---- try-with-resources: auto-closes an AutoCloseable, even on error ----
        try (AutoCloseable res = () -> System.out.println("resource closed")) {
            System.out.println("using the resource");
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
        // Output order: "using the resource" then "resource closed" (close happens automatically).
    }
}
