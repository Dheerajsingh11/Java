// Problem  : Store a dynamic set of strings with fast insert, exact search, and prefix search.
// Approach : A trie (prefix tree) - each edge is a character, each root-to-node path spells a prefix,
//            and a boolean marks nodes that complete a full word.
// Intuition: Words sharing a prefix share the same path, so lookups cost only the WORD LENGTH, not
//            the number of stored words. "Does any word start with 'app'?" is just following 3 edges.
// Time     : insert/search/startsWith O(L) where L = key length   Space: O(total characters * alphabet)
// Trade-off: Blazing prefix queries and autocomplete, but memory-hungry (children arrays/maps per
//            node). Use a HashMap of children for large alphabets to save space.

public class Trie {

    static class Node {
        Node[] children = new Node[26]; // one slot per lowercase letter a..z
        boolean isWord;                 // true if a word ENDS at this node
    }

    private final Node root = new Node();

    void insert(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';                        // map 'a'->0 ... 'z'->25
            if (cur.children[i] == null) {
                cur.children[i] = new Node();       // create the edge if missing
            }
            cur = cur.children[i];                  // descend one character
        }
        cur.isWord = true;                          // mark the final node as a complete word
    }

    // Walk the path for 'key'; return the node it ends at, or null if the path breaks.
    private Node walk(String key) {
        Node cur = root;
        for (char c : key.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) return null; // no such prefix in the trie
            cur = cur.children[i];
        }
        return cur;
    }

    // Exact match: the path must exist AND end on a word-marked node.
    boolean search(String word) {
        Node n = walk(word);
        return n != null && n.isWord;
    }

    // Prefix match: only the path needs to exist (word marker not required).
    boolean startsWith(String prefix) {
        return walk(prefix) != null;
    }

    public static void main(String[] args) {
        Trie t = new Trie();
        for (String w : new String[]{ "app", "apple", "bat" }) t.insert(w);

        System.out.println("search app    : " + t.search("app"));     // true  (inserted)
        System.out.println("search appl   : " + t.search("appl"));    // false (prefix, not a word)
        System.out.println("startsWith app: " + t.startsWith("app")); // true
        System.out.println("startsWith ba : " + t.startsWith("ba"));  // true
        System.out.println("startsWith cd : " + t.startsWith("cd"));  // false
    }
}
