// Pattern  : BRIDGE (Structural)
// Problem  : Two dimensions of variation multiply into a class explosion. Separate them so each can
//            change independently.
// Approach : Split the hierarchy in two - an ABSTRACTION (what) that HOLDS an IMPLEMENTOR (how) -
//            and let both vary. Real domain: message TYPES x delivery CHANNELS.
// Intuition: With 4 message types and 3 channels, inheritance needs 12 classes
//            (UrgentEmailMessage, UrgentSmsMessage, ReminderEmailMessage, ...). Add a channel and
//            you write 4 more; add a type and you write 3 more - the count is types x channels.
//            Bridge composes instead: 4 + 3 = 7 classes, and adding either dimension costs ONE class.
// Time     : O(1) delegation   Space: O(1)
// Trade-off: An extra indirection and a design you must recognize UP FRONT - Bridge is planned in,
//            unlike Adapter which is retrofitted. If only one dimension ever varies, it is
//            unnecessary structure. The payoff arrives the moment the second dimension appears.
// Real use  : JDBC (the Driver API is the abstraction, each vendor driver the implementor), SLF4J
//            (the logging facade bridges to Logback/Log4j), AWT peers, JavaFX/Swing look-and-feel.

// ================== THE IMPLEMENTOR side: HOW a message is delivered ==================
interface DeliveryChannel {
    void deliver(String recipient, String subject, String body);
    int maxBodyLength();
}

class EmailChannel implements DeliveryChannel {
    public void deliver(String to, String subject, String body) {
        System.out.println("      [EMAIL] to=" + to + " subject=\"" + subject + "\"");
        System.out.println("              " + body);
    }
    public int maxBodyLength() { return 10_000; }
}

class SmsChannel implements DeliveryChannel {
    public void deliver(String to, String subject, String body) {
        System.out.println("      [SMS] to=" + to + " : " + body);   // SMS has no subject line
    }
    public int maxBodyLength() { return 160; }
}

class SlackChannel implements DeliveryChannel {
    public void deliver(String to, String subject, String body) {
        System.out.println("      [SLACK] #" + to + " *" + subject + "*  " + body);
    }
    public int maxBodyLength() { return 4000; }
}

// ================== THE ABSTRACTION side: WHAT kind of message it is ==================
abstract class Message {
    // THE BRIDGE: the abstraction holds a reference to the implementor rather than inheriting it.
    // This single field is what decouples the two hierarchies.
    protected final DeliveryChannel channel;

    protected Message(DeliveryChannel channel) { this.channel = channel; }

    abstract void send(String recipient);

    // Shared helper: respect whatever limit the CHANNEL declares. The message type does not need to
    // know which channel it is using - only that channels have a limit.
    protected String fit(String body) {
        int max = channel.maxBodyLength();
        return body.length() <= max ? body : body.substring(0, max - 3) + "...";
    }
}

class UrgentMessage extends Message {
    private final String text;
    UrgentMessage(DeliveryChannel channel, String text) { super(channel); this.text = text; }

    void send(String recipient) {
        channel.deliver(recipient, "URGENT", fit("[!] " + text + " - respond immediately"));
    }
}

class ReminderMessage extends Message {
    private final String task;
    private final String due;
    ReminderMessage(DeliveryChannel channel, String task, String due) {
        super(channel); this.task = task; this.due = due;
    }
    void send(String recipient) {
        channel.deliver(recipient, "Reminder", fit("Don't forget: " + task + " (due " + due + ")"));
    }
}

class ReportMessage extends Message {
    private final String title;
    private final String data;
    ReportMessage(DeliveryChannel channel, String title, String data) {
        super(channel); this.title = title; this.data = data;
    }
    void send(String recipient) {
        channel.deliver(recipient, "Report: " + title,
                fit("Here is the " + title + " report. Figures: " + data
                    + ". This body is deliberately long so the channel limit is visible."));
    }
}

public class BridgePattern {
    public static void main(String[] args) {

        // ANY message type composes with ANY channel - the combinations are made at RUN time and
        // no class exists for the pairing.
        System.out.println("Same message type across three channels:");
        for (DeliveryChannel channel : new DeliveryChannel[]{
                new EmailChannel(), new SmsChannel(), new SlackChannel() }) {
            new UrgentMessage(channel, "Production database is down").send("ops-team");
        }

        System.out.println("Same channel across three message types:");
        DeliveryChannel sms = new SmsChannel();
        new UrgentMessage(sms, "Server on fire").send("+91-90000-11111");
        new ReminderMessage(sms, "submit timesheet", "Friday").send("+91-90000-11111");
        new ReportMessage(sms, "weekly sales", "1,240 units").send("+91-90000-11111");
        System.out.println("      ^ note the report was TRUNCATED to the SMS 160-char limit,");
        System.out.println("        without ReportMessage knowing anything about SMS.");

        System.out.println();
        System.out.println("Class count: 3 message types + 3 channels = 6 classes.");
        System.out.println("With inheritance it would be 3 x 3 = 9, and 4 x 4 = 16 after one more of each.");
    }
}

/* ---------------------------- THE EXPLOSION IT PREVENTS ----------------------------
 * Inheritance forces one class per COMBINATION:
 *
 *              Email            SMS              Slack
 *   Urgent     UrgentEmail      UrgentSms        UrgentSlack
 *   Reminder   ReminderEmail    ReminderSms      ReminderSlack
 *   Report     ReportEmail      ReportSms        ReportSlack
 *
 * That is types x channels classes, and adding ONE channel adds a whole column. Bridge turns the
 * multiplication into an addition: types + channels.
 *
 * ------------------------ BRIDGE vs STRATEGY vs ADAPTER ----------------------------
 * These look alike - all three hold a reference to an interface. The difference is PURPOSE:
 *   BRIDGE    two hierarchies that BOTH vary; planned from the start; structural.
 *   STRATEGY  one interchangeable ALGORITHM behind a stable interface; behavioural.
 *   ADAPTER   makes an EXISTING incompatible class fit; retrofitted, not designed in.
 * Structurally Bridge and Strategy are near-identical - Bridge is about structure and long-term
 * independent evolution, Strategy about swapping behaviour at run time.
 *
 * -------------------------------- WHEN NOT TO USE ----------------------------------
 * - Only ONE dimension varies - a simple interface or Strategy is enough.
 * - The two dimensions are not truly independent (some combinations are invalid), in which case an
 *   Abstract Factory that guarantees matching sets is the better fit.
 * - The class count is small and stable - the indirection costs more clarity than it saves.
 * ------------------------------------------------------------------------------------ */
