// Problem  : Protect an object's internal state so it can never become invalid.
// Approach : Make fields private and expose controlled access through getters/setters that
//            validate input.
// Intuition: If outside code can write any value directly, it can break the object's rules.
//            Hiding the field and guarding the setter keeps the object always-valid.
// Time     : n/a   Space: n/a
// Trade-off: A little extra boilerplate (getter/setter) buys you a single, enforceable place to
//            validate and later change the internal representation without breaking callers.

class BankAccount {
    // "private" means this field is invisible outside the class. Nobody can do account.balance = -999.
    private double balance;

    BankAccount(double opening) {
        if (opening < 0) opening = 0;   // enforce the rule even at construction
        this.balance = opening;
    }

    // Getter: read-only view of the private field.
    double getBalance() {
        return balance;
    }

    // Controlled mutation: the only way to change balance is through rules we control here.
    void deposit(double amount) {
        if (amount <= 0) {              // guard clause protects the invariant "balance >= 0"
            System.out.println("Deposit must be positive");
            return;
        }
        balance += amount;
    }

    boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {   // cannot overdraw
            System.out.println("Invalid withdrawal of " + amount);
            return false;
        }
        balance -= amount;
        return true;
    }
}

public class Encapsulation {
    public static void main(String[] args) {
        BankAccount acc = new BankAccount(100);
        acc.deposit(50);
        acc.withdraw(30);
        acc.withdraw(1000);                     // rejected - protected by encapsulation
        // acc.balance = -5;                     // <- would NOT compile: balance is private
        System.out.println("Balance: " + acc.getBalance()); // expected: Balance: 120.0
    }
}
