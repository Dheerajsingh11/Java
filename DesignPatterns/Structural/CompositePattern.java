// Pattern  : COMPOSITE (Structural)
// Problem  : Treat individual objects and GROUPS of objects through the same interface.
// Approach : Leaf and container both implement one component interface; a container holds children
//            of that same interface and delegates to them. Real domain: a filesystem of files
//            and folders.
// Intuition: A folder's size is the sum of its contents; a file's size is just its own. If the
//            caller must ask "is this a file or a folder?" before it can act, that check spreads
//            through every operation and every new operation repeats it. Composite removes the
//            question entirely: both answer `size()`, and the recursion happens inside the tree.
// Time     : O(n) for a whole-tree operation, n = nodes   Space: O(depth) recursion
// Trade-off: The shared interface can end up too WIDE - a leaf is forced to expose `add(child)`,
//            which is meaningless for it. You then choose between throwing at run time (type safety
//            lost) or splitting the interface (uniformity lost). That tension is inherent to the
//            pattern; pick the side that suits your domain.
// Real use  : java.awt.Container / Component, the Swing and JavaFX scene graphs, the HTML DOM,
//            Composite validators, org chart and menu structures, abstract syntax trees.

import java.util.ArrayList;
import java.util.List;

// The COMPONENT: everything in the tree - file or folder - is one of these.
abstract class FileSystemNode {
    protected final String name;
    protected FileSystemNode(String name) { this.name = name; }

    abstract long size();                       // meaningful for BOTH leaf and composite
    abstract void print(String indent);
    abstract int countFiles();

    String getName() { return name; }
}

// The LEAF: has no children; answers directly.
class FileNode extends FileSystemNode {
    private final long bytes;

    FileNode(String name, long bytes) { super(name); this.bytes = bytes; }

    long size() { return bytes; }                                     // base case of the recursion
    int countFiles() { return 1; }
    void print(String indent) { System.out.println(indent + name + "  (" + bytes + "B)"); }
}

// The COMPOSITE: holds children of the SAME component type - which is what allows arbitrary nesting.
class FolderNode extends FileSystemNode {
    private final List<FileSystemNode> children = new ArrayList<>();

    FolderNode(String name) { super(name); }

    FolderNode add(FileSystemNode child) { children.add(child); return this; }
    void remove(FileSystemNode child)    { children.remove(child); }

    // The composite answers by DELEGATING to its children. It does not care whether each child is a
    // file or another folder - that is precisely the uniformity the pattern provides.
    long size() {
        long total = 0;
        for (FileSystemNode child : children) total += child.size();   // recursion happens here
        return total;
    }

    int countFiles() {
        int total = 0;
        for (FileSystemNode child : children) total += child.countFiles();
        return total;
    }

    void print(String indent) {
        System.out.println(indent + name + "/  (" + size() + "B total, " + countFiles() + " files)");
        for (FileSystemNode child : children) child.print(indent + "    ");
    }

    // A search that also treats both kinds uniformly.
    FileSystemNode find(String target) {
        if (name.equals(target)) return this;
        for (FileSystemNode child : children) {
            if (child.getName().equals(target)) return child;
            if (child instanceof FolderNode folder) {
                FileSystemNode found = folder.find(target);
                if (found != null) return found;
            }
        }
        return null;
    }
}

public class CompositePattern {
    public static void main(String[] args) {

        // Build a tree. Note that add() accepts files and folders identically.
        FolderNode root = new FolderNode("project");
        root.add(new FileNode("README.md", 2048))
            .add(new FileNode("pom.xml", 1024));

        FolderNode src = new FolderNode("src");
        FolderNode main = new FolderNode("main");
        main.add(new FileNode("App.java", 4096))
            .add(new FileNode("Service.java", 8192));
        FolderNode test = new FolderNode("test");
        test.add(new FileNode("AppTest.java", 3072));
        src.add(main).add(test);
        root.add(src);

        System.out.println("Tree:");
        root.print("  ");

        // THE KEY POINT: the same call works on a leaf and on a composite.
        System.out.println();
        System.out.println("Uniform treatment - identical calls, different node kinds:");
        FileSystemNode aFile = new FileNode("standalone.txt", 512);
        for (FileSystemNode node : List.of(aFile, root, src)) {
            System.out.printf("  %-10s size=%-7d files=%d%n",
                    node.getName(), node.size(), node.countFiles());
        }

        System.out.println();
        System.out.println("The client never asks 'is this a file or a folder?' - that check would");
        System.out.println("otherwise have to be repeated in every single operation.");

        FileSystemNode found = root.find("Service.java");
        System.out.println("find(\"Service.java\") -> " + found.getName() + " " + found.size() + "B");
    }
}

/* ------------------------- WHAT IT REPLACES -------------------------
 * Without Composite, every operation needs the same branch:
 *
 *     long size(Object node) {
 *         if (node instanceof File f)   return f.getBytes();
 *         if (node instanceof Folder d) { long t = 0; for (var c : d.getChildren()) t += size(c); return t; }
 *         throw new IllegalArgumentException();
 *     }
 *
 * That instanceof ladder must be written again for countFiles(), print(), search(), delete()... and
 * every new node type means revisiting all of them. Composite pushes the decision into POLYMORPHISM,
 * where the compiler dispatches it for free.
 *
 * --------------------------- THE DESIGN TENSION ----------------------
 * Where do add()/remove() belong?
 *   ON THE COMPONENT  - maximum uniformity, but a leaf must implement operations that make no sense
 *                       (usually by throwing UnsupportedOperationException at RUN time).
 *   ON THE COMPOSITE  - type-safe, but callers must downcast to add children, losing some uniformity.
 * This file puts them on the composite, which is the safer default. The GoF book presents both and
 * calls it an explicit trade-off rather than a solved problem.
 *
 * ------------------------------ WHEN NOT TO USE ----------------------
 * - The structure is genuinely FLAT - a list is simpler than a tree.
 * - Leaves and containers share almost no behaviour, so the common interface would be nearly empty.
 * - Very deep trees with recursive operations risk stack overflow - use an explicit stack or an
 *   iterative traversal (see Data Structures with Java/Trees/TreeTraversalsIterative.java).
 * ---------------------------------------------------------------------- */
