// Pattern  : COMMAND (Behavioural)
// Problem  : Turn a request into an OBJECT, so it can be stored, queued, logged, and undone.
// Approach : Each action implements a command interface with execute() and undo(); an invoker keeps
//            a history stack. Real domain: a text editor with unlimited undo/redo.
// Intuition: A method call is transient - once made, it cannot be inspected, delayed, or reversed.
//            Wrapping it in an object makes the ACTION itself first-class data: it can sit in a
//            queue, be written to a log, be replayed, or be undone. Undo is the clearest motivation:
//            it is essentially impossible without recording what was done and how to reverse it.
// Time     : O(1) execute/undo   Space: O(history size)
// Trade-off: One class per action, which is real boilerplate for simple operations (lambdas help).
//            Undo also requires each command to capture enough state to reverse itself, and that
//            state must be captured at the RIGHT moment - a common source of subtle bugs.
// Real use  : java.lang.Runnable, Swing Action, undo in every editor, database transaction logs,
//            job queues, the Memento-plus-Command combination in graphics software.

import java.util.ArrayDeque;
import java.util.Deque;

// ---- The RECEIVER: the object that actually does the work ----
class TextDocument {
    private final StringBuilder content = new StringBuilder();

    void insert(String text) { content.append(text); }
    void insertAt(int pos, String text) { content.insert(pos, text); }
    String deleteLast(int n) {
        int from = Math.max(0, content.length() - n);
        String removed = content.substring(from);
        content.delete(from, content.length());
        return removed;
    }
    void replaceAll(String from, String to) {
        String updated = content.toString().replace(from, to);
        content.setLength(0);
        content.append(updated);
    }
    int length() { return content.length(); }
    @Override public String toString() { return content.toString(); }
}

// ---- The COMMAND interface ----
interface EditCommand {
    void execute();
    void undo();
    String describe();
}

// ---- Concrete commands. Each captures what it needs to REVERSE itself. ----
class InsertCommand implements EditCommand {
    private final TextDocument doc;
    private final String text;

    InsertCommand(TextDocument doc, String text) { this.doc = doc; this.text = text; }

    public void execute() { doc.insert(text); }
    // Undo is straightforward here: remove exactly as many characters as we added.
    public void undo() { doc.deleteLast(text.length()); }
    public String describe() { return "insert \"" + text + "\""; }
}

class DeleteCommand implements EditCommand {
    private final TextDocument doc;
    private final int count;
    private String deleted;          // captured DURING execute - it is not known before

    DeleteCommand(TextDocument doc, int count) { this.doc = doc; this.count = count; }

    public void execute() { deleted = doc.deleteLast(count); }   // remember what was removed
    public void undo() { doc.insert(deleted); }                  // ...so it can be restored
    public String describe() { return "delete " + count + " chars"; }
}

class ReplaceCommand implements EditCommand {
    private final TextDocument doc;
    private final String from, to;
    private String snapshot;         // full snapshot: replace-all is not cleanly reversible

    ReplaceCommand(TextDocument doc, String from, String to) {
        this.doc = doc; this.from = from; this.to = to;
    }

    public void execute() {
        snapshot = doc.toString();   // capture BEFORE mutating - the timing matters
        doc.replaceAll(from, to);
    }
    public void undo() {
        // Reversing "replace all" by replacing back would be WRONG if the text already contained
        // the target string. Restoring a snapshot is correct - this is the Memento idea.
        doc.replaceAll(doc.toString(), snapshot);
    }
    public String describe() { return "replace \"" + from + "\" -> \"" + to + "\""; }
}

// ---- The INVOKER: runs commands and maintains undo/redo history ----
class EditorInvoker {
    private final Deque<EditCommand> undoStack = new ArrayDeque<>();
    private final Deque<EditCommand> redoStack = new ArrayDeque<>();

    void run(EditCommand command) {
        command.execute();
        undoStack.push(command);
        // A NEW action invalidates the redo branch - the same rule every real editor follows.
        redoStack.clear();
        System.out.println("    do   : " + command.describe());
    }

    void undo() {
        if (undoStack.isEmpty()) { System.out.println("    nothing to undo"); return; }
        EditCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        System.out.println("    undo : " + command.describe());
    }

    void redo() {
        if (redoStack.isEmpty()) { System.out.println("    nothing to redo"); return; }
        EditCommand command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        System.out.println("    redo : " + command.describe());
    }
}

public class CommandPattern {
    public static void main(String[] args) {

        TextDocument doc = new TextDocument();
        EditorInvoker editor = new EditorInvoker();

        System.out.println("Typing:");
        editor.run(new InsertCommand(doc, "Hello "));
        editor.run(new InsertCommand(doc, "World"));
        editor.run(new InsertCommand(doc, "!!!"));
        System.out.println("  document: \"" + doc + "\"");

        System.out.println("Undo twice:");
        editor.undo();
        editor.undo();
        System.out.println("  document: \"" + doc + "\"");

        System.out.println("Redo once:");
        editor.redo();
        System.out.println("  document: \"" + doc + "\"");

        System.out.println("Delete, then undo it (the text comes back):");
        editor.run(new DeleteCommand(doc, 5));
        System.out.println("  document: \"" + doc + "\"");
        editor.undo();
        System.out.println("  document: \"" + doc + "\"");

        System.out.println("Replace, then undo via snapshot:");
        editor.run(new ReplaceCommand(doc, "World", "Java"));
        System.out.println("  document: \"" + doc + "\"");
        editor.undo();
        System.out.println("  document: \"" + doc + "\"");

        System.out.println("A new action clears the redo branch:");
        editor.undo();
        editor.run(new InsertCommand(doc, " [new]"));
        editor.redo();
        System.out.println("  document: \"" + doc + "\"");
    }
}

/* ------------------------- WHY UNDO NEEDS OBJECTS -------------------------
 * A plain method call leaves no trace:
 *     doc.insert("Hello");     // how would you reverse this later?
 * To undo, something must remember WHAT was done and HOW to reverse it. That "something" is the
 * command object. Once actions are objects you also get, almost for free:
 *   - REDO         - keep the undone commands on a second stack
 *   - MACROS       - a command that holds a list of commands
 *   - QUEUING      - put commands on a queue and run them later or elsewhere
 *   - LOGGING      - write executed commands to disk; replay to rebuild state (event sourcing)
 *   - RETRY        - re-execute a failed command
 *
 * -------------------------- THE STATE-CAPTURE TRAP -------------------------
 * Each command must capture reversal state at the RIGHT time:
 *   InsertCommand  knows the length up front - nothing to capture.
 *   DeleteCommand  cannot know what it deleted until execute() runs - capture DURING.
 *   ReplaceCommand cannot cleanly invert a replace-all, so it snapshots BEFORE.
 * Getting this wrong produces an undo that "works" on simple input and corrupts on real input.
 * When reversal is genuinely hard, snapshot the state - that is the MEMENTO pattern, and combining
 * Command with Memento is standard practice in editors and drawing tools.
 *
 * ------------------------------ WHEN NOT TO USE ----------------------------
 * - No need for undo, queuing, logging or replay - just call the method.
 * - Actions are trivial and numerous; a class each is pure ceremony (use lambdas: Runnable IS the
 *   command interface, though it has no undo()).
 * - Undo state would be prohibitively large (snapshotting a huge document on every keystroke).
 * ---------------------------------------------------------------------------- */
