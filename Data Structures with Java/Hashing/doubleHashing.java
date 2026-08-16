// Problem  : Resolve hash collisions with open addressing using DOUBLE HASHING.
// Approach : Use a SECOND hash function to compute the probe STEP, so different keys follow different
//            probe sequences: index = (h1(key) + i * h2(key)) % size.
// Intuition: Linear/quadratic probing make many keys share the same probe path (clustering). Making
//            the step itself depend on the key spreads probes almost like random hashing - the best
//            open-addressing scheme in practice.
// Time     : average O(1) at low load factor   Space: O(size)
// Trade-off: Best collision distribution of the open-addressing methods, but needs a good second
//            hash that is NEVER 0 and is coprime with the table size (so all slots are reachable) -
//            commonly h2(key) = PRIME - (key % PRIME) with a prime table size.

public class doubleHashing {

    private final int[] table;
    private final boolean[] occupied;
    private final int size;
    private final int prime; // a prime smaller than size, used by the second hash

    doubleHashing(int size, int prime) {
        this.size = size;
        this.prime = prime;
        table = new int[size];
        occupied = new boolean[size];
    }

    private int h1(int key) { return key % size; }
    private int h2(int key) { return prime - (key % prime); } // in [1..prime], never 0

    void insert(int key) {
        int step = h2(key);
        for (int i = 0; i < size; i++) {
            int idx = (h1(key) + i * step) % size; // key-dependent step avoids shared clusters
            if (!occupied[idx]) {
                table[idx] = key;
                occupied[idx] = true;
                System.out.println("inserted " + key + " at index " + idx + " (step " + step + ")");
                return;
            }
        }
        System.out.println("table full: could not insert " + key);
    }

    boolean search(int key) {
        int step = h2(key);
        for (int i = 0; i < size; i++) {
            int idx = (h1(key) + i * step) % size;
            if (!occupied[idx]) return false;
            if (table[idx] == key) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        doubleHashing ht = new doubleHashing(7, 5); // size 7 (prime), second-hash prime 5
        for (int k : new int[]{ 10, 17, 24, 3 }) ht.insert(k); // collisions handled with varied steps
        System.out.println("search 17: " + ht.search(17)); // true
        System.out.println("search 50: " + ht.search(50)); // false
    }
}
