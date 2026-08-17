// Principle : OPEN-CLOSED (the O in SOLID)
// Statement : Software should be OPEN for extension but CLOSED for modification.
// Problem   : A `switch` over types must be edited every time a new type appears - and every edit
//             risks the branches that already work.
// Intuition : The goal is to make new behaviour ADD a file rather than MODIFY a tested one. That
//             matters because modified code must be re-reviewed and re-tested in full, while new
//             code cannot break what it does not touch. Polymorphism is the mechanism: the compiler
//             dispatches to the right implementation, so no conditional is needed.
// Benefit   : Adding a shape, a discount, an export format costs one new class and zero regressions.
// Trade-off : You must guess the AXIS of variation correctly. Abstracting the wrong dimension gives
//             you interfaces that need changing anyway - worse than the switch, because now the
//             change is spread across several files. Do not abstract until the second case arrives.

import java.util.List;

// ============================================================================
// BEFORE - a switch that grows with every new shape
// ============================================================================
class ShapeBefore {
    String type;
    double a, b;                         // meaning depends on `type` - already a smell
    ShapeBefore(String type, double a, double b) { this.type = type; this.a = a; this.b = b; }
}

class AreaCalculatorBefore {
    double area(ShapeBefore s) {
        // EVERY new shape means editing THIS method. It must then be re-tested in full, and a
        // mistake here breaks circles and rectangles that were previously correct.
        return switch (s.type) {
            case "CIRCLE"    -> Math.PI * s.a * s.a;
            case "RECTANGLE" -> s.a * s.b;
            case "TRIANGLE"  -> 0.5 * s.a * s.b;
            default -> throw new IllegalArgumentException("unknown shape: " + s.type);
        };
    }

    // Worse: the SAME switch reappears for every operation - perimeter(), draw(), scale()...
    double perimeter(ShapeBefore s) {
        return switch (s.type) {
            case "CIRCLE"    -> 2 * Math.PI * s.a;
            case "RECTANGLE" -> 2 * (s.a + s.b);
            case "TRIANGLE"  -> s.a + s.b + Math.hypot(s.a, s.b);
            default -> throw new IllegalArgumentException("unknown shape: " + s.type);
        };
    }
    // Adding a Pentagon means finding and updating EVERY switch. Miss one and it fails at run time.
}

// ============================================================================
// AFTER - each shape owns its own behaviour
// ============================================================================
interface Shape {
    double area();
    double perimeter();
    default String describe() { return getClass().getSimpleName(); }
}

record CircleShape(double radius) implements Shape {
    public double area()      { return Math.PI * radius * radius; }
    public double perimeter() { return 2 * Math.PI * radius; }
}

record RectangleShape(double width, double height) implements Shape {
    public double area()      { return width * height; }
    public double perimeter() { return 2 * (width + height); }
}

record TriangleShape(double base, double height) implements Shape {
    public double area()      { return 0.5 * base * height; }
    public double perimeter() { return base + height + Math.hypot(base, height); }
}

// EXTENSION: adding a shape touches NOTHING that already exists. This class is new; no existing
// file was modified, so no existing behaviour could have regressed.
record SquareShape(double side) implements Shape {
    public double area()      { return side * side; }
    public double perimeter() { return 4 * side; }
}

// The calculator is now CLOSED - it will never need editing again, whatever shapes appear.
class AreaCalculator {
    double totalArea(List<Shape> shapes) {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }
    void report(List<Shape> shapes) {
        for (Shape s : shapes) {
            System.out.printf("    %-14s area=%8.2f perimeter=%8.2f%n",
                    s.describe(), s.area(), s.perimeter());
        }
    }
}

public class O_OpenClosed {
    public static void main(String[] args) {

        System.out.println("BEFORE - every operation repeats the same switch:");
        AreaCalculatorBefore before = new AreaCalculatorBefore();
        ShapeBefore circle = new ShapeBefore("CIRCLE", 3, 0);
        System.out.printf("    circle area=%.2f perimeter=%.2f%n",
                before.area(circle), before.perimeter(circle));
        try {
            before.area(new ShapeBefore("PENTAGON", 4, 0));
        } catch (IllegalArgumentException e) {
            System.out.println("    adding PENTAGON fails at RUN time: " + e.getMessage());
        }

        System.out.println("AFTER - polymorphism replaces the switch:");
        AreaCalculator calc = new AreaCalculator();
        List<Shape> shapes = List.of(
                new CircleShape(3),
                new RectangleShape(4, 5),
                new TriangleShape(6, 2),
                new SquareShape(4));            // <- added WITHOUT touching AreaCalculator
        calc.report(shapes);
        System.out.printf("    total area = %.2f%n", calc.totalArea(shapes));

        System.out.println();
        System.out.println("SquareShape was added as a NEW file. AreaCalculator was not modified,");
        System.out.println("so nothing that previously worked could have broken.");
    }
}

/* ------------------------------ HOW TO SPOT IT ------------------------------
 * - A switch or if/else over a TYPE field, especially the same one repeated in several methods.
 * - `instanceof` chains.
 * - A `default` branch that throws "unknown type".
 * - A commit that adds a feature by editing five existing files instead of adding one.
 *
 * ------------------------------ THE HONEST CAVEAT ----------------------------
 * OCP is often oversold. You cannot be open to EVERY kind of change - abstracting one axis makes
 * change along a different axis harder. Note that in the AFTER version, adding a new OPERATION
 * (say `scale()`) now requires editing EVERY shape - the exact mirror of the problem we removed.
 *
 * That is the "expression problem": you can be open to new TYPES or to new OPERATIONS, rarely both.
 *   - Types change more often -> use polymorphism (this file).
 *   - Operations change more often -> a switch over a SEALED type is genuinely better, because the
 *     compiler then proves the switch is exhaustive. See
 *     Java Core/12-Advanced/SealedAndPatternMatching.java.
 *
 * ------------------------------- PRACTICAL RULE ------------------------------
 * Do not abstract on the first case. Write the switch. When the SECOND case arrives you will know
 * which axis actually varies, and can abstract that one with evidence rather than by guessing.
 * ------------------------------------------------------------------------------ */
