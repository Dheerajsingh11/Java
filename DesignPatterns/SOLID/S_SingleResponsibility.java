// Principle : SINGLE RESPONSIBILITY (the S in SOLID)
// Statement : A class should have ONE reason to change.
// Problem   : A class that persists, emails, validates and reports has FOUR reasons to change - and
//             a change to any one risks breaking the other three.
// Intuition : "Responsibility" is best read as "audience". The persistence code changes when the DBA
//             changes the schema. The email code changes when marketing rewrites the template. The
//             validation changes when compliance updates the rules. Those are three different people
//             asking for changes to the SAME file - which is exactly when merge conflicts, accidental
//             regressions and untestable code appear.
// Benefit   : Each class becomes small enough to understand, test in isolation, and reuse.
// Trade-off : More classes and more wiring. Taken to an extreme you get dozens of one-method classes
//             and the logic becomes hard to follow. The test is whether the reasons to change are
//             genuinely DIFFERENT, not whether the class is long.

import java.util.ArrayList;
import java.util.List;

// ============================================================================
// BEFORE - one class doing four unrelated jobs
// ============================================================================
class EmployeeBefore {
    private final String name;
    private final String email;
    private final double salary;

    EmployeeBefore(String name, String email, double salary) {
        this.name = name; this.email = email; this.salary = salary;
    }

    // JOB 1 - business rule. Changes when HR policy changes.
    double calculateTax() { return salary * 0.30; }

    // JOB 2 - persistence. Changes when the database or ORM changes.
    void saveToDatabase() {
        System.out.println("    INSERT INTO employees VALUES ('" + name + "', " + salary + ")");
    }

    // JOB 3 - notification. Changes when the email provider or template changes.
    void sendWelcomeEmail() {
        System.out.println("    SMTP -> " + email + " : Welcome aboard, " + name + "!");
    }

    // JOB 4 - reporting/formatting. Changes when the report format changes.
    String toCsvRow() { return name + "," + email + "," + salary; }

    // FOUR reasons to change. A test for calculateTax() now needs a database and an SMTP server,
    // because they live in the same class. That is the practical cost.
}

// ============================================================================
// AFTER - one responsibility per class
// ============================================================================

// 1. The DATA (and only rules intrinsic to it). Changes when the concept of an employee changes.
record Employee(String name, String email, double salary) { }

// 2. Business rules. Changes when tax policy changes.
class TaxCalculator {
    double calculate(Employee e) { return e.salary() * 0.30; }
}

// 3. Persistence. Changes when storage changes - and can be swapped for a fake in tests.
interface EmployeeRepository { void save(Employee e); }

class SqlEmployeeRepository implements EmployeeRepository {
    public void save(Employee e) {
        System.out.println("    INSERT INTO employees VALUES ('" + e.name() + "', " + e.salary() + ")");
    }
}

class InMemoryEmployeeRepository implements EmployeeRepository {   // trivially usable in a unit test
    final List<Employee> saved = new ArrayList<>();
    public void save(Employee e) { saved.add(e); }
}

// 4. Notification. Changes when the provider or template changes.
class EmployeeNotifier {
    void sendWelcome(Employee e) {
        System.out.println("    SMTP -> " + e.email() + " : Welcome aboard, " + e.name() + "!");
    }
}

// 5. Formatting. Changes when the export format changes.
class EmployeeCsvFormatter {
    String toRow(Employee e) { return e.name() + "," + e.email() + "," + e.salary(); }
}

// 6. Orchestration - the only class that knows the WORKFLOW. It coordinates, it does not implement.
class EmployeeOnboardingService {
    private final EmployeeRepository repository;
    private final EmployeeNotifier notifier;

    EmployeeOnboardingService(EmployeeRepository repository, EmployeeNotifier notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    void onboard(Employee e) {
        repository.save(e);
        notifier.sendWelcome(e);
    }
}

public class S_SingleResponsibility {
    public static void main(String[] args) {

        System.out.println("BEFORE - one class, four jobs:");
        EmployeeBefore before = new EmployeeBefore("Asha", "asha@corp.com", 90000);
        before.saveToDatabase();
        before.sendWelcomeEmail();
        System.out.println("    tax = " + before.calculateTax());
        System.out.println("    csv = " + before.toCsvRow());

        System.out.println("AFTER - each concern in its own class:");
        Employee asha = new Employee("Asha", "asha@corp.com", 90000);
        new EmployeeOnboardingService(new SqlEmployeeRepository(), new EmployeeNotifier()).onboard(asha);
        System.out.println("    tax = " + new TaxCalculator().calculate(asha));
        System.out.println("    csv = " + new EmployeeCsvFormatter().toRow(asha));

        System.out.println("THE PAYOFF - testing tax needs no database and no email server:");
        InMemoryEmployeeRepository fake = new InMemoryEmployeeRepository();
        new EmployeeOnboardingService(fake, new EmployeeNotifier()).onboard(asha);
        System.out.println("    saved to a fake repository: " + fake.saved.size() + " record(s)");
        System.out.println("    TaxCalculator can be tested completely on its own.");
    }
}

/* ------------------------- HOW TO SPOT A VIOLATION -------------------------
 * - The class name contains "And", or is vague ("Manager", "Processor", "Helper", "Util").
 * - You cannot describe what it does in one sentence without "and".
 * - Its imports span unrelated areas: java.sql AND javax.mail AND a formatting library.
 * - A unit test needs heavy setup for parts of the class the test does not care about.
 * - Different teams keep editing the same file for unrelated reasons.
 *
 * ---------------------------- THE COMMON MISREADING --------------------------
 * SRP is NOT "a class should do only one thing" or "keep classes short". A class may have many
 * methods and still have one responsibility. The real question is: WHO asks for changes? If the
 * answer is more than one role - DBA, compliance, marketing - the class is doing too much.
 *
 * ------------------------------ THE COUNTER-RISK -----------------------------
 * Over-applied, SRP produces an explosion of anaemic one-method classes where following a simple
 * operation means opening eight files. Split when the reasons to change are genuinely different -
 * not merely because a class has grown.
 * ----------------------------------------------------------------------------- */
