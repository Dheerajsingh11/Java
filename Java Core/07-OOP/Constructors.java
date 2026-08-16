// Problem  : Initialize objects safely at creation time using constructors.
// Approach : Show default, parameterized, and overloaded constructors plus constructor chaining.
// Intuition: A constructor runs automatically when "new" is called, guaranteeing an object is
//            never left in a half-built state.
// Time     : n/a   Space: n/a
// Trade-off: Setting fields via a constructor is safer than setting them one-by-one afterwards,
//            because the object is valid the moment it exists.

class Student {
    String name;
    int rollNo;

    // No-argument (default-style) constructor. If you write NO constructor at all, Java gives you
    // an invisible one like this. Once you write any constructor, that free one disappears.
    Student() {
        // "this(...)" calls another constructor of the SAME class (constructor chaining).
        // It must be the first statement. This avoids duplicating initialization logic.
        this("Unknown", -1);
    }

    // Parameterized constructor - lets the caller supply initial values.
    Student(String name, int rollNo) {
        // "this.name" is the field; "name" is the parameter. "this" disambiguates the two when
        // they share a name (this is the most common reason to use "this").
        this.name = name;
        this.rollNo = rollNo;
    }

    void print() {
        System.out.println("Student{name=" + name + ", rollNo=" + rollNo + "}");
    }
}

public class Constructors {
    public static void main(String[] args) {
        Student s1 = new Student();                 // uses the no-arg constructor -> chains to the other
        Student s2 = new Student("Asha", 42);       // uses the parameterized constructor

        s1.print(); // expected: Student{name=Unknown, rollNo=-1}
        s2.print(); // expected: Student{name=Asha, rollNo=42}
    }
}
