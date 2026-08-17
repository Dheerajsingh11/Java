// Pattern  : MEDIATOR (Behavioural)
// Problem  : Many objects must interact, and wiring them directly to each other creates a tangle.
// Approach : Peers talk only to a mediator, which coordinates them. Real domain: an air-traffic
//            control tower, and a chat room.
// Intuition: With n components talking directly, the number of possible connections grows as
//            n(n-1)/2 - 10 components can have 45 relationships. Every component then knows several
//            others, so nothing can be reused or tested alone. A mediator turns that mesh into a
//            STAR: each peer knows only the mediator, and the interaction rules live in one place
//            instead of being smeared across every participant.
// Time     : O(n) per broadcast   Space: O(n) for the registry
// Trade-off: The mediator ABSORBS the complexity it removed from the peers, and can grow into a god
//            object that knows everything. That is the pattern's central risk: you have not deleted
//            the coupling, you have centralized it - which is an improvement only while the mediator
//            stays focused.
// Real use  : java.util.Timer, ExecutorService (tasks never reference each other), Spring's
//            ApplicationContext, MVC controllers coordinating view and model, message brokers,
//            air-traffic control (the canonical example), UI dialogs coordinating their widgets.

import java.util.ArrayList;
import java.util.List;

// ---- The mediator interface ----
interface ChatMediator {
    void register(ChatUser user);
    void send(String message, ChatUser sender);
    void sendPrivate(String message, ChatUser sender, String recipientName);
}

// ---- The COLLEAGUE: knows only the mediator, never another user ----
abstract class ChatUser {
    protected final ChatMediator mediator;
    protected final String name;

    protected ChatUser(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }

    String getName() { return name; }

    void say(String message) {
        System.out.println("  " + name + " says: " + message);
        mediator.send(message, this);          // does NOT call other users directly
    }

    void whisper(String message, String to) {
        System.out.println("  " + name + " whispers to " + to + ": " + message);
        mediator.sendPrivate(message, this, to);
    }

    abstract void receive(String message, String from);
}

class RegularUser extends ChatUser {
    RegularUser(ChatMediator mediator, String name) { super(mediator, name); }
    void receive(String message, String from) {
        System.out.println("      " + name + " <- [" + from + "] " + message);
    }
}

// A colleague with different behaviour - the mediator does not care.
class BotUser extends ChatUser {
    BotUser(ChatMediator mediator, String name) { super(mediator, name); }
    void receive(String message, String from) {
        System.out.println("      " + name + " <- [" + from + "] " + message);
        if (message.toLowerCase().contains("help")) {
            // The bot replies THROUGH the mediator, like everyone else.
            System.out.println("      " + name + " auto-replies");
            mediator.send("Try the docs at /help", this);
        }
    }
}

// ---- THE MEDIATOR: all interaction rules live here ----
class ChatRoom implements ChatMediator {
    private final List<ChatUser> users = new ArrayList<>();
    private final List<String> banned = List.of("spam", "scam");

    public void register(ChatUser user) {
        users.add(user);
        System.out.println("  (" + user.getName() + " joined - " + users.size() + " in room)");
    }

    public void send(String message, ChatUser sender) {
        // POLICY LIVES HERE, not in the users. Adding moderation, logging, rate limiting or
        // translation means changing this class only - no user class is touched.
        for (String word : banned) {
            if (message.toLowerCase().contains(word)) {
                System.out.println("      [moderated] message from " + sender.getName() + " blocked");
                return;
            }
        }
        for (ChatUser user : users) {
            if (user != sender) {                  // do not echo back to the sender
                user.receive(message, sender.getName());
            }
        }
    }

    public void sendPrivate(String message, ChatUser sender, String recipientName) {
        // The mediator resolves NAMES to objects, so senders never hold references to recipients.
        users.stream()
             .filter(u -> u.getName().equals(recipientName))
             .findFirst()
             .ifPresentOrElse(
                 u -> u.receive("(private) " + message, sender.getName()),
                 () -> System.out.println("      [!] no such user: " + recipientName));
    }
}

public class MediatorPattern {
    public static void main(String[] args) {

        ChatRoom room = new ChatRoom();

        ChatUser asha = new RegularUser(room, "Asha");
        ChatUser bala = new RegularUser(room, "Bala");
        ChatUser cara = new RegularUser(room, "Cara");
        ChatUser bot  = new BotUser(room, "HelpBot");

        room.register(asha);
        room.register(bala);
        room.register(cara);
        room.register(bot);

        System.out.println("Broadcast - Asha holds no reference to Bala or Cara:");
        asha.say("Morning everyone");

        System.out.println("Private message - resolved by NAME through the mediator:");
        bala.whisper("lunch at 1?", "Cara");

        System.out.println("Moderation policy lives in the mediator, not in any user:");
        cara.say("buy cheap spam here");

        System.out.println("A bot reacts and replies through the same mediator:");
        asha.say("I need help with the API");

        System.out.println("Unknown recipient handled centrally:");
        bala.whisper("hello?", "Nobody");
    }
}

/* ---------------------------- THE COUPLING IT REMOVES ----------------------------
 * DIRECT WIRING - every user holds references to every other:
 *     class User { List<User> others; void say(String m) { for (User u : others) u.receive(m); } }
 * With n users that is up to n(n-1)/2 relationships. Consequences:
 *   - A user cannot be tested without constructing the others.
 *   - Adding moderation means editing EVERY user class.
 *   - Adding a user type means updating everyone who might talk to it.
 *
 * MEDIATOR - each user knows ONE object. n relationships instead of n^2, and the interaction rules
 * sit in a single class that can be tested on its own.
 *
 * -------------------------------- THE REAL RISK ----------------------------------
 * The mediator becomes a GOD OBJECT. It knows every participant and every rule, so it grows with
 * each new interaction until it is the most complex class in the system. Mitigations:
 *   - Keep it focused on COORDINATION; behaviour belongs in the colleagues.
 *   - Split large mediators by concern (a moderation mediator, a routing mediator).
 *   - If the peers only need to be NOTIFIED rather than coordinated, use Observer instead - it is
 *     lighter and the subject holds no rules about who does what.
 *
 * ----------------------------- MEDIATOR vs OBSERVER ------------------------------
 * MEDIATOR  peers communicate THROUGH a coordinator that knows them and applies rules.
 *           Two-way: it routes, filters, and may transform. Peers may be very different.
 * OBSERVER  a subject BROADCASTS to listeners it knows nothing about.
 *           One-way: fire and forget, no coordination logic.
 * Use Mediator when interaction needs RULES; Observer when it only needs NOTIFICATION.
 *
 * ------------------------------- WHEN NOT TO USE ---------------------------------
 * - Only two or three objects interact - direct references are simpler and clearer.
 * - The interaction is one-way notification - Observer is the lighter fit.
 * - The mediator would just forward calls without adding coordination; it is then pure indirection.
 * ----------------------------------------------------------------------------------- */
