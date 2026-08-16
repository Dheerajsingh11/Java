// Problem  : Maintain a collection of disjoint sets with fast "which set is x in?" and "merge two
//            sets" - the Union-Find data structure.
// Approach : Each set is a tree; every element points to a parent, and the root identifies the set.
//            Two optimizations - union by rank and path compression - keep the trees nearly flat.
// Intuition: find(x) walks to the root (the set's representative). union links one root under the
//            other. Without care these trees get tall (O(n) find); the two optimizations flatten
//            them so operations become almost constant time.
// Time     : ~O(alpha(n)) per op (inverse Ackermann, < 5 in practice)   Space: O(n)
// Trade-off: Near-constant-time merges/queries at trivial memory cost, but it only supports UNION
//            (not splitting a set back apart). The backbone of Kruskal's MST and connectivity queries.

public class DisjointSet {

    private final int[] parent; // parent[i] = i's parent; a root points to itself
    private final int[] rank;   // upper bound on a tree's height, used to union the shorter under taller

    DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i; // each element starts in its own singleton set
    }

    // Find the representative (root) of x, compressing the path so future finds are O(1)-ish.
    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // PATH COMPRESSION: point x straight at the root
        }
        return parent[x];
    }

    // Merge the sets containing a and b. Returns false if they were already together.
    boolean union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;                 // already in the same set -> no merge (would form a cycle)

        // UNION BY RANK: attach the shorter tree under the taller to keep height minimal.
        if (rank[ra] < rank[rb]) { int t = ra; ra = rb; rb = t; }
        parent[rb] = ra;
        if (rank[ra] == rank[rb]) rank[ra]++;       // equal heights -> the merged tree grows by one
        return true;
    }

    boolean connected(int a, int b) { return find(a) == find(b); }

    public static void main(String[] args) {
        DisjointSet ds = new DisjointSet(6);
        ds.union(0, 1);
        ds.union(1, 2);
        ds.union(3, 4);
        System.out.println("0 & 2 connected: " + ds.connected(0, 2)); // true (0-1-2)
        System.out.println("0 & 3 connected: " + ds.connected(0, 3)); // false (different sets)
        ds.union(2, 3);                                                // merge the two groups
        System.out.println("0 & 4 connected: " + ds.connected(0, 4)); // true now
        System.out.println("union(0,1) again: " + ds.union(0, 1));    // false (already together)
    }
}
