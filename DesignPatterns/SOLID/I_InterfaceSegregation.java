// Principle : INTERFACE SEGREGATION (the I in SOLID)
// Statement : No client should be forced to depend on methods it does not use.
// Problem   : A "fat" interface forces implementers to write empty or throwing methods for
//             behaviour they do not have.
// Intuition : An interface is a PROMISE. A fat interface makes every implementer promise things it
//             cannot deliver, so they fake it - an empty body, or an exception. Both are lies: the
//             empty body silently does nothing, and the exception turns a compile-time contract into
//             a run-time failure. Splitting the interface lets each class promise only what it can
//             actually do.
// Benefit   : Implementers stay honest; callers depend only on what they need; a change to one
//             capability does not force unrelated classes to recompile or adapt.
// Trade-off : More interfaces to name and track. Split by CAPABILITY, not by method - one interface
//             per method is its own kind of mess.

import java.util.List;

// ============================================================================
// BEFORE - one fat interface every worker must implement
// ============================================================================
interface WorkerBefore {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void submitTimesheet();
}

class HumanWorkerBefore implements WorkerBefore {
    public void work()            { System.out.println("      human: writing code"); }
    public void eat()             { System.out.println("      human: lunch"); }
    public void sleep()           { System.out.println("      human: 8 hours"); }
    public void attendMeeting()   { System.out.println("      human: in standup"); }
    public void submitTimesheet() { System.out.println("      human: submitted"); }
}

class RobotWorkerBefore implements WorkerBefore {
    public void work() { System.out.println("      robot: assembling"); }

    // FORCED to implement things a robot cannot do. Two bad options, both used in real codebases:
    public void eat()   { /* empty - silently does nothing, so callers are misled */ }
    public void sleep() { throw new UnsupportedOperationException("robots do not sleep"); }
    public void attendMeeting()   { /* empty */ }
    public void submitTimesheet() { /* empty */ }

    // Worse still: adding a method to WorkerBefore breaks EVERY implementer, including those that
    // have no use for it.
}

// ============================================================================
// AFTER - small interfaces, composed as needed
// ============================================================================
interface Workable  { void work(); }
interface Feedable  { void eat(); }
interface Restable  { void sleep(); }
interface Reportable { void submitTimesheet(); }

// Each class implements ONLY what it genuinely does - no empty methods, no exceptions.
class HumanWorker implements Workable, Feedable, Restable, Reportable {
    private final String name;
    HumanWorker(String name) { this.name = name; }
    public void work()            { System.out.println("      " + name + ": writing code"); }
    public void eat()             { System.out.println("      " + name + ": lunch"); }
    public void sleep()           { System.out.println("      " + name + ": 8 hours"); }
    public void submitTimesheet() { System.out.println("      " + name + ": timesheet submitted"); }
}

class RobotWorker implements Workable {
    private final String id;
    RobotWorker(String id) { this.id = id; }
    public void work() { System.out.println("      " + id + ": assembling"); }
    // Nothing else. The type now tells the truth about what a robot can do.
}

// A contractor works and reports, but is not fed or rested by the company.
class ContractorWorker implements Workable, Reportable {
    private final String name;
    ContractorWorker(String name) { this.name = name; }
    public void work()            { System.out.println("      " + name + ": consulting"); }
    public void submitTimesheet() { System.out.println("      " + name + ": invoice submitted"); }
}

// Callers depend on the NARROWEST interface they need. This method accepts robots, humans and
// contractors alike, because all it requires is the ability to work.
class Factory {
    void runShift(List<Workable> workers) {
        for (Workable w : workers) w.work();
    }
    // This one cannot even be CALLED with a robot - the compiler prevents the mistake that the
    // fat interface would only have revealed at run time.
    void collectTimesheets(List<Reportable> staff) {
        for (Reportable r : staff) r.submitTimesheet();
    }
}

public class I_InterfaceSegregation {
    public static void main(String[] args) {

        System.out.println("BEFORE - a robot forced to implement eat() and sleep():");
        WorkerBefore robot = new RobotWorkerBefore();
        robot.work();
        robot.eat();      // silently does nothing - the caller cannot tell
        System.out.println("      eat() did nothing at all - a silent lie");
        try {
            robot.sleep();
        } catch (UnsupportedOperationException e) {
            System.out.println("      sleep() -> " + e.getMessage() + "  (run-time failure)");
        }

        System.out.println("AFTER - each type implements only what it can do:");
        Factory factory = new Factory();

        List<Workable> shift = List.of(
                new HumanWorker("Asha"), new RobotWorker("R2-D2"), new ContractorWorker("Bala"));
        System.out.println("    everyone can work:");
        factory.runShift(shift);

        System.out.println("    only those who report submit timesheets:");
        factory.collectTimesheets(List.of(new HumanWorker("Asha"), new ContractorWorker("Bala")));

        System.out.println();
        System.out.println("    factory.collectTimesheets(List.of(new RobotWorker(...)))");
        System.out.println("    would not COMPILE - the mistake is caught before it can run.");
    }
}

/* ------------------------------ HOW TO SPOT IT ------------------------------
 * - Empty method bodies in an implementation.
 * - Methods that throw UnsupportedOperationException.
 * - Implementations whose methods ignore their parameters.
 * - Adding one method to an interface forces edits in many unrelated classes.
 * - An interface named "...Service" or "...Manager" with a dozen loosely related methods.
 *
 * ------------------------- THE CONNECTION TO LISKOV --------------------------
 * A fat interface almost guarantees an LSP violation. RobotWorkerBefore is not substitutable for
 * WorkerBefore, because sleep() throws where the interface implied it would work. Segregating the
 * interface fixes both problems at once - the robot is no longer promising something it cannot do.
 *
 * ----------------------------- HOW FINE IS TOO FINE --------------------------
 * Split by CAPABILITY, not by method. `Workable` is a capability; splitting further into
 * `Startable`, `Stoppable` and `Pausable` when they are always implemented together just adds
 * names. A useful test: would any class ever implement one of these WITHOUT the others? If not,
 * they belong together.
 *
 * ------------------------------- IN THE JDK ----------------------------------
 * Java's own interfaces are mostly well segregated: Comparable, Iterable, Closeable, Runnable,
 * Serializable are each ONE capability, which is exactly why they compose so freely. The
 * counter-example is java.util.List, whose optional add()/remove() throw for immutable
 * implementations - a fat-interface compromise the JDK made for compatibility.
 * ------------------------------------------------------------------------------ */
