// Problem  : Build an optimal prefix-free binary code so that frequently used symbols get SHORT
//            codes - the basis of lossless compression.
// Approach : GREEDY with a min-heap. Repeatedly take the two LEAST frequent nodes, merge them under a
//            new parent whose frequency is their sum, and put it back. The last node left is the root.
// Intuition: In an optimal code, the two rarest symbols must sit DEEPEST in the tree (longest codes) -
//            if a rarer symbol had a shorter code than a common one, swapping them would reduce the
//            total. Since they are deepest, they can be made siblings, which is exactly what merging
//            them does. That is the exchange argument proving greedy is optimal here.
// Time     : O(n log n) - n merges, each with O(log n) heap operations   Space: O(n)
// Trade-off: Provably optimal among per-symbol prefix codes, and simple. Its limits: it needs the
//            frequencies up front (two passes, or a shipped table), and it cannot do better than one
//            whole bit per symbol - arithmetic coding beats it on highly skewed data by using
//            fractional bits.

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class huffmanCoding {

    static class Node {
        char ch;              // meaningful only in leaves
        int freq;
        Node left, right;

        Node(char ch, int freq) { this.ch = ch; this.freq = freq; }
        Node(Node l, Node r)    { this.ch = '\0'; this.freq = l.freq + r.freq; left = l; right = r; }
        boolean isLeaf()        { return left == null && right == null; }
    }

    static Node buildTree(Map<Character, Integer> freq) {
        // Min-heap keyed on frequency: the two cheapest nodes are always at hand.
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.freq, b.freq));
        for (Map.Entry<Character, Integer> e : freq.entrySet()) {
            pq.offer(new Node(e.getKey(), e.getValue()));
        }

        // Edge case: a single distinct symbol has no pair to merge, so give it a parent explicitly
        // or its code would be the empty string.
        if (pq.size() == 1) return new Node(pq.poll(), new Node('\0', 0));

        // THE GREEDY LOOP: merging the two rarest pushes them one level deeper, which is where the
        // rarest symbols belong. Each merge reduces the count by one, so n-1 merges leave one root.
        while (pq.size() > 1) {
            Node a = pq.poll();
            Node b = pq.poll();
            pq.offer(new Node(a, b));      // the parent's frequency is the sum
        }
        return pq.poll();
    }

    // Walk the tree assigning 0 for left, 1 for right. Codes live only in LEAVES, which is what
    // makes the code PREFIX-FREE.
    static void buildCodes(Node n, String code, Map<Character, String> out) {
        if (n == null) return;
        if (n.isLeaf()) { out.put(n.ch, code.isEmpty() ? "0" : code); return; }
        buildCodes(n.left,  code + "0", out);
        buildCodes(n.right, code + "1", out);
    }

    public static void main(String[] args) {
        String text = "abracadabra";

        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) freq.merge(c, 1, Integer::sum);
        System.out.println("frequencies : " + freq);

        Node root = buildTree(freq);
        Map<Character, String> codes = new HashMap<>();
        buildCodes(root, "", codes);

        System.out.println("codes       : " + codes);
        // 'a' occurs 5 times and gets the SHORTEST code; 'd' occurs once and gets a long one.

        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) encoded.append(codes.get(c));

        int fixedBits = text.length() * 8;               // naive: 8 bits per character
        int huffBits = encoded.length();
        System.out.println("encoded     : " + encoded);
        System.out.println("fixed 8-bit : " + fixedBits + " bits");
        System.out.println("huffman     : " + huffBits + " bits");
        System.out.printf ("saving      : %.1f%%%n", 100.0 * (fixedBits - huffBits) / fixedBits);

        // ---- Decoding: walk the tree bit by bit; a leaf emits a symbol and resets to the root ----
        StringBuilder decoded = new StringBuilder();
        Node cur = root;
        for (char bit : encoded.toString().toCharArray()) {
            cur = (bit == '0') ? cur.left : cur.right;
            if (cur.isLeaf()) { decoded.append(cur.ch); cur = root; }
        }
        System.out.println("decoded     : " + decoded + "  (matches: " + decoded.toString().equals(text) + ")");
    }
}

/* --------------------------- WHY PREFIX-FREE MATTERS ---------------------------
 * No code is a prefix of another, because symbols live only at LEAVES - once you reach a leaf you
 * cannot still be on the way to some other symbol. That is what makes the encoded stream decodable
 * WITHOUT separators: you walk the tree and know exactly when a symbol ends.
 *
 * If 'a' were 0 and 'b' were 01, the stream "01" would be ambiguous - "ab"? or just "b"? Prefix-
 * freeness removes that ambiguity entirely, which is why it is a requirement rather than a nicety.
 *
 * ------------------------------- WHERE IT IS USED -------------------------------
 * DEFLATE (ZIP, gzip, PNG) combines Huffman with LZ77; JPEG and MP3 use it for their coefficient
 * streams; HTTP/2's HPACK uses a fixed Huffman table for headers. It is one of the most widely
 * deployed algorithms in existence.
 *
 * ---------------------------------- LIMITATIONS ---------------------------------
 *   - Needs the frequency table: either two passes over the data, or ship the tree with the output.
 *   - Minimum one bit per symbol, so it cannot compress below that even for extremely skewed data -
 *     arithmetic and range coding can, by emitting fractional bits.
 *   - Optimal only for INDEPENDENT symbols; it captures no correlation between neighbours, which is
 *     why real formats pair it with a dictionary stage like LZ77.
 * -------------------------------------------------------------------------------- */
