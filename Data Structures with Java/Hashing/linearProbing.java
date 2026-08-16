// Problem  : Handle hash collisions using OPEN ADDRESSING with LINEAR PROBING (no linked lists).
// Approach : Store every key directly in one array. On a collision, walk forward one slot at a time
//            (wrapping around) until an empty slot is found.
// Intuition: Instead of chaining overflow into a separate list, we keep everything in a single flat
//            array and simply look for the next free seat. Searching repeats the same walk and stops
//            at the first truly EMPTY slot, since a key can never be stored past one.
// Time     : O(1) average at a low load factor; degrades toward O(n) as the table fills
// Space    : O(cap) - one flat array, no per-node objects
// Trade-off: Excellent cache locality (contiguous memory) and no allocation per key, but it suffers
//            PRIMARY CLUSTERING - consecutive occupied runs grow and merge, so probes get longer.
//            It also cannot exceed its capacity, and deletion needs a TOMBSTONE marker (see below).
//            Compare quadraticProbing.java and doubleHashing.java, which spread probes out better.
// NOTE      : Declares its own myHash class - compile/run this file INDIVIDUALLY.

// Slot encoding used throughout:
//   -1 = EMPTY  (never used)      -> search may stop here; insert may use it
//   -2 = DELETED ("tombstone")    -> search must CONTINUE past it; insert may reuse it
class myHash {
    int[] arr;
    int size, cap;

    myHash(int c) {
        cap = c;
        size = 0;
        arr = new int[cap];
        for (int i = 0; i < cap; i++) {
            arr[i] = -1;                 // mark every slot EMPTY to begin with
        }
    }

    int hash(int key) {
        return key % cap;                // map the key to a starting slot
    }

    boolean search(int key) {
        int h = hash(key);
        int i = h;
        // Stop at an EMPTY slot: if the key existed, probing would have placed it at or before this
        // point, so an empty slot proves absence.
        while (arr[i] != -1) {
            if (arr[i] == key) return true;
            i = (i + 1) % cap;           // LINEAR PROBE: step forward one, wrapping with modulo
            if (i == h) return false;    // came full circle -> table is full and key is absent
        }
        return false;
        // WHY tombstones matter: if delete wrote -1 instead of -2, this loop would stop early at the
        // hole and wrongly report "not found" for a key stored further along the same probe path.
    }

    boolean insert(int key) {
        if (size == cap) return false;                  // table full - open addressing cannot grow
        int i = hash(key);
        // Walk until we find a slot that is EMPTY (-1), a TOMBSTONE (-2, reusable), or the key itself.
        while (arr[i] != -1 && arr[i] != -2 && arr[i] != key) {
            i = (i + 1) % cap;
        }
        if (arr[i] == key) return false;                // no duplicates allowed
        arr[i] = key;
        size++;
        return true;
    }

    boolean delete(int key) {
        int h = hash(key);
        int i = h;
        while (arr[i] != -1) {
            if (arr[i] == key) {
                arr[i] = -2;             // TOMBSTONE, not -1: keeps later probe paths intact
                size--;
                return true;
            }
            i = (i + 1) % cap;
            if (i == h) return false;    // full circle -> not present
        }
        return false;
    }
}

public class linearProbing {
    public static void main(String[] args) {
        myHash mh = new myHash(7);
        mh.insert(49);   // 49 % 7 = 0
        mh.insert(30);   // 30 % 7 = 2
        mh.insert(56);   // 56 % 7 = 0 -> COLLIDES with 49, probes forward to slot 1

        System.out.println("search 30 : " + mh.search(30)); // expected: true
        System.out.println("search 56 : " + mh.search(56)); // expected: true (found after probing)
        mh.delete(30);
        System.out.println("search 30 after delete: " + mh.search(30)); // expected: false
        System.out.println("search 56 still found  : " + mh.search(56)); // expected: true (tombstone works)
    }
}
