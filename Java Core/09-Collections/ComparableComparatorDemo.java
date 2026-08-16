// Problem  : Define how objects are ordered - one natural order, or many custom orders.
// Approach : Implement Comparable for the default order; supply Comparator objects for alternates.
// Intuition: Comparable is the object saying "here is MY natural order" (one, built in). Comparator
//            is an external rule "sort THESE by this key" (many, swappable) - decoupled from the class.
// Time     : sorting is O(n log n); each compare is O(1) here   Space: O(1) extra for the rules
// Trade-off: Comparable bakes in a single ordering (compareTo); Comparator keeps ordering out of the
//            class so you can sort the same data many ways. Use Comparable for the one obvious order,
//            Comparator for everything else.

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Implementing Comparable makes Player have a NATURAL order (here: by score ascending).
class Player implements Comparable<Player> {
    String name;
    int score;
    int age;

    Player(String name, int score, int age) {
        this.name = name; this.score = score; this.age = age;
    }

    @Override
    public int compareTo(Player other) {
        // Contract: return negative if this < other, 0 if equal, positive if this > other.
        // Use Integer.compare to avoid overflow bugs from "this.score - other.score".
        return Integer.compare(this.score, other.score);
    }

    @Override
    public String toString() {
        return name + "(score=" + score + ", age=" + age + ")";
    }
}

public class ComparableComparatorDemo {
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>(List.of(
                new Player("Asha", 30, 25),
                new Player("Bala", 10, 30),
                new Player("Cara", 30, 20)
        ));

        // Natural order (Comparable): ascending score.
        players.sort(null); // null tells sort to use the elements' compareTo
        System.out.println("by score asc : " + players);

        // Comparator: descending score, without touching the Player class.
        players.sort(Comparator.comparingInt((Player p) -> p.score).reversed());
        System.out.println("by score desc: " + players);

        // Multi-key: by score ascending, then break ties by age ascending (thenComparing).
        players.sort(Comparator.comparingInt((Player p) -> p.score)
                                .thenComparingInt(p -> p.age));
        System.out.println("score,age    : " + players); // Bala, then Cara(age20) before Asha(age25)

        // By name alphabetically.
        players.sort(Comparator.comparing(p -> p.name));
        System.out.println("by name      : " + players);

        // Edge: keep compareTo consistent with equals where possible; an inconsistent comparator
        // can make TreeSet/TreeMap "lose" elements that it considers equal but you do not.
    }
}
