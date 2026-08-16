// Problem  : Build a minimal hash table from scratch that supports insert, search, and remove.
// Approach : Separate CHAINING - an array of buckets, each holding a linked list of the keys that
//            hashed to it. The hash function maps a key to a bucket index with key % BUCKET.
// Intuition: A hash function converts a key into an array index, giving direct O(1) access instead of
//            scanning. Two different keys can land in the same bucket (a COLLISION); chaining simply
//            stores both in that bucket's list, so lookups scan only that short list, not the table.
// Time     : O(1 + alpha) average for insert/search/remove, where alpha = n/BUCKET is the load factor.
//            Worst case O(n) if every key collides into one bucket.
// Space    : O(n + BUCKET)
// Trade-off: Chaining degrades gracefully (it never "fills up" like open addressing) and handles
//            deletion cleanly, at the cost of extra node objects and poor cache locality. Compare
//            linearProbing.java / quadraticProbing.java / doubleHashing.java for the open-addressing
//            alternatives, which store everything in one array but need tombstones for deletion.
// NOTE     : This file previously contained decompiler output (FernFlower, with var1/var2 names).
//            It has been rewritten as readable, documented source.

import java.util.ArrayList;
import java.util.LinkedList;

class myHash {
    int BUCKET;                              // number of buckets (the table size)
    ArrayList<LinkedList<Integer>> table;    // table[i] holds every key that hashed to bucket i

    myHash(int b) {
        BUCKET = b;
        table = new ArrayList<>();
        // Every bucket must start as an EMPTY list, not null, or insert would throw NPE.
        for (int i = 0; i < BUCKET; i++) {
            table.add(new LinkedList<>());
        }
    }

    // The hash function: fold any key into a valid bucket index 0..BUCKET-1.
    // Using a PRIME bucket count spreads keys more evenly, because a composite size makes keys
    // sharing a factor with it cluster into a few buckets.
    private int hashFunction(int key) {
        return key % BUCKET;
    }

    void insert(int key) {
        table.get(hashFunction(key)).add(key);   // append to that bucket's chain: O(1)
    }

    boolean search(int key) {
        // Only ONE bucket is scanned, so the cost is the length of that chain, not the table size.
        return table.get(hashFunction(key)).contains(key);
    }

    void remove(int key) {
        // Cast to Integer is essential: remove(int) would delete by INDEX, remove(Object) by VALUE.
        table.get(hashFunction(key)).remove((Integer) key);
    }

    void print() {
        for (int i = 0; i < BUCKET; i++) {
            System.out.println("Bucket " + i + ": " + table.get(i));
        }
    }

    public static void main(String[] args) {
        myHash h = new myHash(7);
        for (int k : new int[]{ 10, 17, 24, 3, 5 }) h.insert(k); // 10,17,24 all hash to 3 -> one chain
        h.print();
        System.out.println("search 17 : " + h.search(17));  // expected: true
        System.out.println("search 99 : " + h.search(99));  // expected: false
        h.remove(17);
        System.out.println("after remove, search 17: " + h.search(17)); // expected: false
    }
}
