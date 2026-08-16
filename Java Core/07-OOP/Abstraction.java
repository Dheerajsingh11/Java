// Problem  : Define WHAT a type must do while hiding HOW, and forbid creating incomplete objects.
// Approach : Use an abstract class with an abstract method (no body) that subclasses must implement.
// Intuition: Some concepts (a "Payment") are too generic to instantiate directly - you only ever
//            have a concrete kind (CardPayment). "abstract" enforces that at compile time.
// Time     : n/a   Space: n/a
// Trade-off: Abstract classes can hold shared state + partial code (unlike interfaces), but a
//            class can extend only one. Use them for "is-a" families that share implementation.

// "abstract" here means: you cannot do "new Payment(...)". It only exists to be subclassed.
abstract class Payment {
    double amount;

    Payment(double amount) {
        this.amount = amount;           // abstract classes CAN have constructors and fields
    }

    // Abstract method: no body. Every concrete subclass is REQUIRED to provide one, or it too
    // must be declared abstract. This is the "what without how".
    abstract void pay();

    // Concrete method: shared behaviour all payments get for free.
    void receipt() {
        System.out.println("Paid " + amount + " successfully");
    }
}

class CardPayment extends Payment {
    CardPayment(double amount) { super(amount); }

    @Override
    void pay() {                        // supplies the "how"
        System.out.println("Charging card: " + amount);
    }
}

class UpiPayment extends Payment {
    UpiPayment(double amount) { super(amount); }

    @Override
    void pay() {
        System.out.println("Sending UPI request: " + amount);
    }
}

public class Abstraction {
    public static void main(String[] args) {
        // Payment p = new Payment(10);  // <- would NOT compile: cannot instantiate abstract class

        Payment[] payments = { new CardPayment(500), new UpiPayment(250) };
        for (Payment p : payments) {
            p.pay();        // subclass-specific
            p.receipt();    // shared
        }
        // expected:
        // Charging card: 500.0
        // Paid 500.0 successfully
        // Sending UPI request: 250.0
        // Paid 250.0 successfully
    }
}
