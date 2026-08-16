// Problem  : A fixed-capacity cache that evicts the LEAST RECENTLY USED entry when full, with both
//            get and put running in O(1).
// Approach : A HashMap for O(1) lookup, plus a DOUBLY linked list holding entries in recency order.
//            The map stores references to the list NODES, not just values.
// Intuition: Neither structure alone can do this. A HashMap finds a key instantly but has no notion
//            of order, so it cannot tell you what to evict. A linked list maintains order but takes
//            O(n) to find a key. Combining them gives both: the map answers "where is this node?"
//            in O(1), and because the list is DOUBLY linked, that node can be unlinked and moved to
//            the front in O(1) without any traversal.
// Time     : get O(1), put O(1)   Space: O(capacity)
// Trade-off: This is the definitive example of why doubly linked lists earn their extra pointer.
//            With a SINGLY linked list, removing a known node requires finding its predecessor -
//            an O(n) scan - which would destroy the O(1) guarantee entirely.

import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    // Doubly linked so a node can unlink ITSELF, knowing both neighbours.
    private static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();   // key -> the NODE holding it
    private final Node head, tail;                            // sentinels: head.next = most recent

    public LRUCache(int capacity) {
        this.capacity = capacity;
        // DUMMY head and tail remove every null check from the link/unlink code - the real entries
        // always sit strictly between them, so no node is ever at a boundary.
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    private void unlink(Node n) {           // O(1): the node knows both neighbours
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void pushFront(Node n) {        // O(1): becomes the most recently used
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }

    public int get(int key) {
        Node n = map.get(key);
        if (n == null) return -1;           // miss

        // A read counts as USE, so refresh its recency by moving it to the front.
        unlink(n);
        pushFront(n);
        return n.value;
    }

    public void put(int key, int value) {
        Node existing = map.get(key);

        if (existing != null) {             // update in place and refresh recency
            existing.value = value;
            unlink(existing);
            pushFront(existing);
            return;
        }

        if (map.size() == capacity) {
            // EVICT the least recently used - always the node just before the tail sentinel.
            // The node carries its own key, which is what lets us remove the right map entry in
            // O(1). Without storing the key in the node we would have to search the map.
            Node lru = tail.prev;
            unlink(lru);
            map.remove(lru.key);
        }

        Node fresh = new Node(key, value);
        map.put(key, fresh);
        pushFront(fresh);
    }

    private String order() {                // most recent -> least recent, for the demo
        StringBuilder sb = new StringBuilder();
        for (Node c = head.next; c != tail; c = c.next) sb.append(c.key).append("=").append(c.value).append(" ");
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(3);

        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300);
        System.out.println("after puts     : " + cache.order());   // 3=300 2=200 1=100

        System.out.println("get(1)         : " + cache.get(1));    // 100
        System.out.println("after get(1)   : " + cache.order());   // 1=100 3=300 2=200  <- 1 refreshed

        cache.put(4, 400);                                          // capacity full -> evict LRU (key 2)
        System.out.println("after put(4)   : " + cache.order());   // 4=400 1=100 3=300
        System.out.println("get(2) evicted : " + cache.get(2));    // -1

        cache.put(3, 333);                                          // update existing + refresh
        System.out.println("after put(3)   : " + cache.order());   // 3=333 4=400 1=100
    }
}

/* --------------------------- WHY BOTH STRUCTURES ---------------------------
 *                     HashMap alone        Linked list alone      Combined
 *   find a key          O(1)                 O(n)                  O(1)
 *   know what to evict  impossible           O(1)                  O(1)
 *   move to front       n/a                  O(n) to FIND it       O(1)
 *
 * The map's values are NODE REFERENCES, which is the crucial design decision: it converts "find
 * this node in the list" from an O(n) traversal into an O(1) map lookup. The doubly linked list
 * then makes the unlink itself O(1).
 *
 * ------------------------------- IN PRACTICE --------------------------------
 * Java offers this almost for free: LinkedHashMap with accessOrder = true maintains access order
 * internally, and overriding removeEldestEntry gives an LRU cache in a few lines. Implementing it
 * by hand is worth doing once, because the map-plus-list pattern recurs (LFU caches, ordered
 * indexes) and because it makes the reason for choosing a DOUBLY linked list concrete.
 *
 * Used in: CPU and disk caches, database buffer pools, Redis eviction policies, browser caches,
 * and memoization tables with bounded memory.
 * ---------------------------------------------------------------------------- */
