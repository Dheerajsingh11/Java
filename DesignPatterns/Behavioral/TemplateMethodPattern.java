// Pattern  : TEMPLATE METHOD (Behavioural)
// Problem  : Several processes share the same overall SEQUENCE but differ in individual steps.
// Approach : A base class defines the skeleton in a `final` method and delegates the varying steps
//            to abstract methods. Real domain: exporting a report as CSV, JSON or HTML.
// Intuition: Every export must fetch data, format a header, format rows, format a footer, and write
//            the file - always in that order. Copying that sequence into three classes duplicates it
//            three times, and a fix to the ordering must then be applied three times. Template Method
//            writes the sequence ONCE and lets subclasses fill in only what actually differs.
// Time     : O(work of the steps)   Space: O(1)
// Trade-off: It uses INHERITANCE, so it is more rigid than composition: a subclass gets one parent,
//            the coupling is compile-time, and a subclass can accidentally break the algorithm if the
//            template is not `final`. Strategy achieves something similar with composition and is the
//            more flexible choice when the variation is a single step.
// Real use  : java.util.AbstractList and the other AbstractXxx collection skeletons,
//            javax.servlet.http.HttpServlet.service() dispatching to doGet/doPost,
//            JUnit's test lifecycle, Spring's JdbcTemplate, InputStream.read(byte[]).

import java.util.List;

record SalesRow(String region, int units, double revenue) { }

abstract class ReportExporter {

    // THE TEMPLATE METHOD. `final` is deliberate and important: it stops a subclass from overriding
    // the ALGORITHM itself. Subclasses may change the steps, never their order.
    final String export(List<SalesRow> data) {
        StringBuilder out = new StringBuilder();

        out.append(header());                          // step 1 - varies
        for (SalesRow row : data) {
            out.append(formatRow(row));                // step 2 - varies
        }
        out.append(footer(data));                      // step 3 - varies

        if (includeTimestamp()) {                      // HOOK - optional, with a default
            out.append(timestampLine());
        }
        return out.toString();
    }

    // ---- Steps every subclass MUST supply ----
    protected abstract String header();
    protected abstract String formatRow(SalesRow row);
    protected abstract String footer(List<SalesRow> data);

    // ---- HOOK: a step with a sensible default that subclasses MAY override.
    //      Hooks are what keep the base class from forcing irrelevant work on every subclass. ----
    protected boolean includeTimestamp() { return false; }

    // ---- A shared helper: common behaviour lives once, in the base class ----
    protected String timestampLine() { return "\n(generated at report-time)"; }

    protected double totalRevenue(List<SalesRow> data) {
        return data.stream().mapToDouble(SalesRow::revenue).sum();
    }
}

class CsvExporter extends ReportExporter {
    protected String header() { return "region,units,revenue\n"; }
    protected String formatRow(SalesRow r) {
        return r.region() + "," + r.units() + "," + r.revenue() + "\n";
    }
    protected String footer(List<SalesRow> data) {
        return "TOTAL,," + totalRevenue(data) + "\n";
    }
}

class JsonExporter extends ReportExporter {
    protected String header() { return "{\n  \"rows\": [\n"; }
    protected String formatRow(SalesRow r) {
        return String.format("    {\"region\":\"%s\",\"units\":%d,\"revenue\":%.1f},%n",
                r.region(), r.units(), r.revenue());
    }
    protected String footer(List<SalesRow> data) {
        return "  ],\n  \"total\": " + totalRevenue(data) + "\n}";
    }
    // This exporter opts INTO the optional step - the others do not.
    @Override protected boolean includeTimestamp() { return true; }
}

class HtmlExporter extends ReportExporter {
    protected String header() {
        return "<table>\n  <tr><th>Region</th><th>Units</th><th>Revenue</th></tr>\n";
    }
    protected String formatRow(SalesRow r) {
        return String.format("  <tr><td>%s</td><td>%d</td><td>%.1f</td></tr>%n",
                r.region(), r.units(), r.revenue());
    }
    protected String footer(List<SalesRow> data) {
        return "  <tr><td colspan=\"2\">TOTAL</td><td>" + totalRevenue(data) + "</td></tr>\n</table>";
    }
}

public class TemplateMethodPattern {
    public static void main(String[] args) {

        List<SalesRow> data = List.of(
                new SalesRow("North", 120, 24000),
                new SalesRow("South", 95, 19000),
                new SalesRow("East", 140, 28000));

        // Same skeleton, three different outputs. The caller does not know which steps varied.
        for (ReportExporter exporter : List.of(new CsvExporter(), new JsonExporter(), new HtmlExporter())) {
            System.out.println("--- " + exporter.getClass().getSimpleName() + " ---");
            System.out.println(exporter.export(data));
            System.out.println();
        }

        System.out.println("Note the JSON export ends with a timestamp and the others do not -");
        System.out.println("that is the optional HOOK, overridden by only one subclass.");
    }
}

/* --------------------------- WHY THE TEMPLATE IS final ---------------------------
 * The whole value of the pattern is that the SEQUENCE is fixed and correct in one place. If a
 * subclass could override export(), it could reorder the steps, skip the footer, or forget the
 * timestamp - reintroducing exactly the inconsistency the pattern exists to prevent. Marking it
 * `final` turns "please do not change the algorithm" from a comment into a compiler guarantee.
 *
 * ------------------------------- ABSTRACT vs HOOK ---------------------------------
 * ABSTRACT method  - the subclass MUST supply it; the algorithm cannot run without it.
 * HOOK             - has a default; the subclass MAY override it.
 * Hooks are what stop the base class from imposing work on subclasses that do not need it. Choosing
 * wrongly is a real design cost: too many abstract methods makes every subclass implement stubs;
 * too many hooks makes the algorithm's behaviour hard to predict.
 *
 * ---------------------- TEMPLATE METHOD vs STRATEGY -------------------------------
 * | | Template Method | Strategy |
 * | Mechanism | INHERITANCE - subclass overrides steps | COMPOSITION - holds an interface |
 * | Varies | several steps of one algorithm | the whole algorithm |
 * | Bound | compile time | RUN time, swappable |
 * | Reuse | shared code lives in the base class | strategies share nothing by default |
 *
 * Prefer Strategy when ONE thing varies and you want to swap it at run time. Prefer Template Method
 * when SEVERAL steps vary together and there is genuine shared code to inherit. The usual advice
 * "favour composition over inheritance" applies - this is one of the few patterns where inheritance
 * is the right tool, because the steps are not independently meaningful.
 *
 * -------------------------------- WHEN NOT TO USE ---------------------------------
 * - Only one step varies - use Strategy or pass a lambda.
 * - The subclasses share almost no code, so the base class holds only abstract methods; that is an
 *   interface, not a template.
 * - The "sequence" is not actually fixed and subclasses keep wanting to change it - the abstraction
 *   is wrong.
 * - Deep hierarchies of templates become very hard to follow (the "yo-yo problem": reading the code
 *   means jumping repeatedly between base and subclass).
 * ------------------------------------------------------------------------------------ */
