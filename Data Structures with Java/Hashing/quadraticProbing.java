// Problem  : Resolve hash collisions with open addressing using QUADRATIC probing.
// Approach : On a collision at index h, try h+1^2, h+2^2, h+3^2, ... (mod size) until a free slot.
// Intuition: Linear probing (h+1, h+2, ...) piles collisions into long runs ("primary clustering").
//            Quadratic jumps spread probes out, reducing that clustering.
// Time     : average O(1) insert/search at low load factor; degrades as the table fills   Space: O(size)
// Trade-off: Fewer long clusters than linear probing, but suffers "secondary clustering" (keys with
//            the same start probe the same sequence) and may not visit every slot unless size is
//            chosen carefully (e.g. prime and load factor < 0.5). Compare with Chaining.java / linearProbing.java.

public class quadraticProbing {

    private final int[] table;
    private final boolean[] occupied;
    private final int size;

    quadraticProbing(int size) {
        this.size = size;
        table = new int[size];
        occupied = new boolean[size];
    }

    void insert(int key) {
        int h = key % size;
        for (int i = 0; i < size; i++) {
            int idx = (h + i * i) % size;   // quadratic step: +0, +1, +4, +9, ...
            if (!occupied[idx]) {
                table[idx] = key;
                occupied[idx] = true;
                System.out.println("inserted " + key + " at index " + idx);
                return;
            }
        }
        System.out.println("table full: could not insert " + key);
    }

    boolean search(int key) {
        int h = key % size;
        for (int i = 0; i < size; i++) {
            int idx = (h + i * i) % size;
            if (!occupied[idx]) return false;      // an empty slot on the probe path -> key absent
            if (table[idx] == key) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        quadraticProbing ht = new quadraticProbing(7); // prime size helps coverage
        for (int k : new int[]{ 10, 17, 24, 3 }) ht.insert(k); // 10,17,24 all hash to 3 -> collide
        System.out.println("search 24: " + ht.search(24)); // true
        System.out.println("search 99: " + ht.search(99)); // false
    }
}
