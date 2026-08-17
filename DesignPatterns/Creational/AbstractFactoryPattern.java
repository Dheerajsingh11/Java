// Pattern  : ABSTRACT FACTORY (Creational)
// Problem  : Create FAMILIES of related objects that must be used together, without naming their
//            concrete classes.
// Approach : A factory interface declares one creation method per product in the family; each
//            concrete factory produces a matching set. Real domain: notification channels whose
//            formatter, sender and retry policy must belong to the same family.
// Intuition: Factory Method answers "give me A payment method". Abstract Factory answers "give me a
//            whole CONSISTENT SET". The point is preventing MISMATCHED combinations: an SMS sender
//            paired with an HTML formatter would produce markup in a 160-character text message.
//            By making the factory produce the entire family, an inconsistent mix becomes
//            impossible to express.
// Time     : O(1) per creation   Space: O(1)
// Trade-off: The heaviest creational pattern - one interface plus one class per product per family,
//            so adding a new PRODUCT type means editing every factory. It pays off only when the
//            products genuinely must match. If they are independent, use separate factories.
// Real use  : javax.xml.parsers.DocumentBuilderFactory, java.sql.Connection (creates Statement,
//            PreparedStatement, CallableStatement all bound to the same driver), Swing look-and-feel.

// ----------------------- the product interfaces (the "family") -----------------------
interface MessageFormatter { String format(String subject, String body); }
interface MessageSender    { String send(String to, String content); }
interface RetryPolicy      { String describe(); }

// ----------------------------- FAMILY 1: EMAIL -----------------------------
class EmailFormatter implements MessageFormatter {
    public String format(String subject, String body) {
        return "<html><h1>" + subject + "</h1><p>" + body + "</p></html>";   // rich HTML is fine
    }
}
class EmailSender implements MessageSender {
    public String send(String to, String content) {
        return "SMTP -> " + to + " : " + content.substring(0, Math.min(45, content.length())) + "...";
    }
}
class EmailRetryPolicy implements RetryPolicy {
    public String describe() { return "retry 3x over 10 minutes (email is not time-critical)"; }
}

// ------------------------------ FAMILY 2: SMS ------------------------------
class SmsFormatter implements MessageFormatter {
    public String format(String subject, String body) {
        String text = subject + ": " + body;
        return text.length() > 160 ? text.substring(0, 157) + "..." : text;   // hard 160-char limit
    }
}
class SmsSender implements MessageSender {
    public String send(String to, String content) { return "SMS gateway -> " + to + " : " + content; }
}
class SmsRetryPolicy implements RetryPolicy {
    public String describe() { return "retry 1x immediately (SMS costs money per attempt)"; }
}

// ---------------------------- FAMILY 3: PUSH ------------------------------
class PushFormatter implements MessageFormatter {
    public String format(String subject, String body) {
        return "{\"title\":\"" + subject + "\",\"message\":\"" + body + "\"}";  // JSON payload
    }
}
class PushSender implements MessageSender {
    public String send(String to, String content) { return "FCM -> device " + to + " : " + content; }
}
class PushRetryPolicy implements RetryPolicy {
    public String describe() { return "retry 5x with backoff (push is cheap and unreliable)"; }
}

// ------------------------------ THE ABSTRACT FACTORY ------------------------------
// One method per product. A factory ALWAYS returns a mutually consistent set.
interface NotificationFactory {
    MessageFormatter createFormatter();
    MessageSender createSender();
    RetryPolicy createRetryPolicy();
}

class EmailFactory implements NotificationFactory {
    public MessageFormatter createFormatter() { return new EmailFormatter(); }
    public MessageSender createSender()       { return new EmailSender(); }
    public RetryPolicy createRetryPolicy()    { return new EmailRetryPolicy(); }
}
class SmsFactory implements NotificationFactory {
    public MessageFormatter createFormatter() { return new SmsFormatter(); }
    public MessageSender createSender()       { return new SmsSender(); }
    public RetryPolicy createRetryPolicy()    { return new SmsRetryPolicy(); }
}
class PushFactory implements NotificationFactory {
    public MessageFormatter createFormatter() { return new PushFormatter(); }
    public MessageSender createSender()       { return new PushSender(); }
    public RetryPolicy createRetryPolicy()    { return new PushRetryPolicy(); }
}

public class AbstractFactoryPattern {

    // The client works ONLY with the interfaces. It cannot accidentally mix an SMS formatter with
    // an email sender, because it never chooses them individually - the factory supplies the set.
    static void notifyUser(NotificationFactory factory, String recipient) {
        MessageFormatter formatter = factory.createFormatter();
        MessageSender sender = factory.createSender();
        RetryPolicy retry = factory.createRetryPolicy();

        String content = formatter.format("Order shipped", "Your order #4821 is on its way today.");
        System.out.println("  " + sender.send(recipient, content));
        System.out.println("    policy: " + retry.describe());
    }

    public static void main(String[] args) {
        System.out.println("EMAIL family:");
        notifyUser(new EmailFactory(), "asha@example.com");

        System.out.println("SMS family:");
        notifyUser(new SmsFactory(), "+91-90000-11111");

        System.out.println("PUSH family:");
        notifyUser(new PushFactory(), "device-abc-123");

        // Selecting the whole family at run time - one decision, guaranteed-consistent parts.
        String channel = "SMS";
        NotificationFactory chosen = switch (channel) {
            case "EMAIL" -> new EmailFactory();
            case "SMS"   -> new SmsFactory();
            case "PUSH"  -> new PushFactory();
            default -> throw new IllegalArgumentException("unknown channel " + channel);
        };
        System.out.println("chosen at run time (" + channel + "):");
        notifyUser(chosen, "+91-90000-22222");
    }
}

/* ------------------------ THE MISMATCH IT PREVENTS ------------------------
 * Without this pattern a caller could write:
 *     MessageFormatter f = new EmailFormatter();     // produces HTML
 *     MessageSender s = new SmsSender();             // 160-char text channel
 * The code compiles and runs, and ships HTML markup inside a text message. Abstract Factory makes
 * that combination UNEXPRESSIBLE: you receive a formatter and a sender together, or not at all.
 *
 * -------------------- FACTORY METHOD vs ABSTRACT FACTORY -------------------
 * | | Factory Method | Abstract Factory |
 * | Creates | ONE product | a FAMILY of related products |
 * | Adding a new VARIANT (e.g. WhatsApp) | easy - one class | easy - one factory class |
 * | Adding a new PRODUCT (e.g. an Auditor) | easy | HARD - every factory must change |
 *
 * That last row is the pattern's main weakness, and the reason not to reach for it early.
 *
 * ------------------------------- WHEN NOT TO USE ---------------------------
 * - The products are INDEPENDENT and any combination is valid - separate factories are simpler.
 * - There is only one family; you are paying for variability that does not exist.
 * - A DI framework already wires consistent sets of collaborators for you (Spring profiles do
 *   exactly this job with far less ceremony).
 * --------------------------------------------------------------------------- */
