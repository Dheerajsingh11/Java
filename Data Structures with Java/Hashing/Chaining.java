// Problem  : Handle hash collisions using SEPARATE CHAINING and observe how keys distribute.
// Approach : Each bucket holds a linked list ("chain"). Colliding keys are appended to the same chain,
//            so nothing is ever displaced.
// Intuition: Collisions are unavoidable - by the pigeonhole principle, mapping many keys into few
//            buckets guarantees repeats (see the Birthday Paradox note in Note.md). Chaining accepts
//            this and simply keeps a list per bucket, so a lookup scans one short chain.
// Time     : O(1 + alpha) average for insert/search/delete, alpha = n/BUCKET (the load factor).
//            Worst case O(n) when every key hashes to the same bucket.
// Space    : O(n + BUCKET)
// Trade-off: Chaining NEVER fills up (unlike open addressing, which fails when the array is full) and
//            deletion is trivial - just unlink the node, no tombstones needed. The costs are extra
//            node allocations and worse cache locality than a flat array. Java's own HashMap uses
//            chaining, and converts a chain into a balanced TREE once it grows past ~8 nodes, which
//            caps the worst case at O(log n) instead of O(n).
// NOTE      : This file declares its own myHash class, so compile/run it INDIVIDUALLY (it collides
//            with myHash.java if the whole folder is compiled at once).

import java.util.ArrayList;
import java.util.LinkedList;

class myHash {
    int BUCKET;                             // number of buckets
    ArrayList<LinkedList<Integer>> table;   // one chain (linked list) per bucket

    myHash(int b) {
        BUCKET = b;
        table = new ArrayList<LinkedList<Integer>>();
        for (int i = 0; i < BUCKET; i++) {
            table.add(new LinkedList<Integer>()); // every bucket starts as an empty chain
        }
    }

    void insert(int key) {
        int i = key % BUCKET;   // hash function decides WHICH chain this key belongs to
        table.get(i).add(key);  // append - O(1), and collisions just make this chain longer
    }

    boolean search(int key) {
        int i = key % BUCKET;               // jump straight to the one bucket that could hold it
        return table.get(i).contains(key);  // scan only that chain, not the whole table
    }

    void remove(int key) {
        int i = key % BUCKET;
        // (Integer) cast is REQUIRED: remove(int) removes by index, remove(Object) removes by value.
        table.get(i).remove((Integer) key);
    }
}

public class Chaining {
    public static void main(String[] args) {
        myHash hash = new myHash(10);
        // 2, 22 both hash to bucket 2; 10, 30 both hash to bucket 0 -> visible collisions/chains.
        hash.insert(2);
        hash.insert(10);
        hash.insert(30);
        hash.insert(15);
        hash.insert(22);
        hash.insert(27);

        System.out.println("search 22 -> " + hash.search(22)); // expected: true
        hash.remove(22);
        System.out.println("search 22 after remove -> " + hash.search(22)); // expected: false

        for (int i = 0; i < hash.BUCKET; i++) {
            System.out.println("Bucket " + i + ": " + hash.table.get(i));
        }
        // Bucket 0 holds [10, 30] and bucket 2 holds [2] - the chains ARE the collision handling.
    }
}

// ------------------------------- LOAD FACTOR -------------------------------
// alpha = n / m   (n = keys stored, m = number of buckets)
// It is the AVERAGE chain length, which is exactly what a lookup must scan - hence O(1 + alpha).
//  - alpha too HIGH  -> long chains -> lookups drift toward O(n).
//  - alpha too LOW   -> mostly empty buckets -> memory wasted.
// The usual fix is REHASHING: once alpha passes a threshold (Java's HashMap uses 0.75), allocate a
// bigger table and reinsert every key. That single resize costs O(n), but amortized over many
// insertions it keeps each operation effectively O(1).
