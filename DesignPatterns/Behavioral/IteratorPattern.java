// Pattern  : ITERATOR (Behavioural)
// Problem  : Traverse a collection's elements without exposing how it stores them.
// Approach : An iterator object holds the traversal position and exposes hasNext()/next(). Real
//            domain: a custom playlist supporting normal, shuffled and filtered traversal.
// Intuition: If callers loop with an index, they are depending on the collection being ARRAY-BACKED.
//            Change it to a linked list or a tree and every loop breaks. An iterator hides the
//            structure behind two methods, so the same loop works over anything - which is exactly
//            why for-each works identically for ArrayList, HashSet and TreeMap.
// Time     : O(1) per next(), O(n) for a full traversal   Space: O(1) for the cursor
// Trade-off: An extra object per traversal, and iterators can become invalid if the collection
//            changes underneath them (hence fail-fast checking). Java gives you this pattern for
//            free via Iterable, so writing one by hand is only worth it for CUSTOM traversal orders -
//            which is what this file demonstrates.
// Real use  : java.util.Iterator, every for-each loop, Scanner, ResultSet, Files.lines(),
//            Stream's internal iteration.

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

record Song(String title, String artist, int seconds) { }

// Implementing Iterable is what makes a class usable in a for-each loop. That single method is the
// entire contract.
class Playlist implements Iterable<Song> {
    private final List<Song> songs = new ArrayList<>();

    Playlist add(Song song) { songs.add(song); return this; }
    int size() { return songs.size(); }

    // ---- The DEFAULT traversal: front to back ----
    @Override
    public Iterator<Song> iterator() {
        return new Iterator<Song>() {
            private int cursor = 0;                 // the position lives in the ITERATOR, not the list

            public boolean hasNext() { return cursor < songs.size(); }

            public Song next() {
                if (!hasNext()) throw new NoSuchElementException();  // required by the contract
                return songs.get(cursor++);
            }
        };
    }

    // ---- A SECOND traversal order over the SAME data ----
    // This is the real reason to hand-write an iterator: multiple orders, one collection.
    Iterable<Song> reversed() {
        return () -> new Iterator<Song>() {
            private int cursor = songs.size() - 1;
            public boolean hasNext() { return cursor >= 0; }
            public Song next() {
                if (!hasNext()) throw new NoSuchElementException();
                return songs.get(cursor--);
            }
        };
    }

    // ---- A FILTERING traversal: skips elements without copying the collection ----
    Iterable<Song> longerThan(int seconds) {
        return () -> new Iterator<Song>() {
            private int cursor = 0;

            // The work happens in hasNext(): advance past anything that does not qualify, so next()
            // always has something ready. Doing the filtering in next() instead is the classic bug -
            // hasNext() would then return true for an element that gets skipped.
            public boolean hasNext() {
                while (cursor < songs.size() && songs.get(cursor).seconds() <= seconds) cursor++;
                return cursor < songs.size();
            }

            public Song next() {
                if (!hasNext()) throw new NoSuchElementException();
                return songs.get(cursor++);
            }
        };
    }

    // ---- An INFINITE traversal: loops forever. Perfectly legal - hasNext() simply never returns
    //      false. The CALLER decides when to stop. ----
    Iterator<Song> repeating() {
        return new Iterator<Song>() {
            private int cursor = 0;
            public boolean hasNext() { return !songs.isEmpty(); }
            public Song next() {
                Song song = songs.get(cursor % songs.size());
                cursor++;
                return song;
            }
        };
    }
}

public class IteratorPattern {
    public static void main(String[] args) {

        Playlist playlist = new Playlist()
                .add(new Song("Bohemian Rhapsody", "Queen", 355))
                .add(new Song("Come As You Are", "Nirvana", 219))
                .add(new Song("Stairway to Heaven", "Led Zeppelin", 482))
                .add(new Song("Smells Like Teen Spirit", "Nirvana", 301));

        // for-each works because Playlist implements Iterable - the caller never touches an index.
        System.out.println("1. default order (for-each):");
        for (Song s : playlist) System.out.println("     " + s.title() + " (" + s.seconds() + "s)");

        System.out.println("2. reversed - same data, different iterator:");
        for (Song s : playlist.reversed()) System.out.println("     " + s.title());

        System.out.println("3. filtered (> 300s) - no copy of the collection is made:");
        for (Song s : playlist.longerThan(300)) System.out.println("     " + s.title());

        System.out.println("4. infinite repeat - the CALLER decides when to stop:");
        Iterator<Song> loop = playlist.repeating();
        for (int i = 0; i < 6; i++) System.out.println("     " + loop.next().title());

        System.out.println("5. the explicit iterator - what for-each does underneath:");
        Iterator<Song> it = playlist.iterator();
        while (it.hasNext()) System.out.println("     " + it.next().artist());

        System.out.println("6. next() past the end throws, as the contract requires:");
        Iterator<Song> exhausted = playlist.iterator();
        while (exhausted.hasNext()) exhausted.next();
        try {
            exhausted.next();
        } catch (NoSuchElementException e) {
            System.out.println("     NoSuchElementException, correctly");
        }
    }
}

/* --------------------------- WHY NOT JUST USE AN INDEX ---------------------------
 *     for (int i = 0; i < playlist.size(); i++) playlist.get(i);
 *
 * Three problems:
 *   1. It assumes INDEXED access. On a linked list get(i) is O(n), making the loop O(n^2) - a real
 *      and common performance bug (see Data Structures with Java/Linked List/Note.md).
 *   2. It assumes the collection HAS an order and exposes positions. A HashSet or a tree does not.
 *   3. It fixes ONE traversal order. Reversed, filtered and infinite traversals all need new methods
 *      on the collection instead of separate iterator objects.
 *
 * ------------------------------- THE CONTRACT ------------------------------------
 *   hasNext()  must be side-effect free and repeatable - calling it twice must not consume anything.
 *   next()     must throw NoSuchElementException when exhausted, not return null.
 *   remove()   optional; the default throws UnsupportedOperationException.
 * Filtering iterators must do their skipping in hasNext(), or hasNext() will promise an element that
 * next() then skips.
 *
 * ---------------------------- FAIL-FAST AND ConcurrentModificationException -------
 * Java's collection iterators cache a modCount and check it on each next(). Modifying the collection
 * directly during iteration is detected and throws immediately, rather than silently skipping
 * elements. Use Iterator.remove() or removeIf() instead - see Java Core/09-Collections/IteratorDemo.java.
 *
 * ------------------------------- WHEN NOT TO USE ---------------------------------
 * - A plain array or List with one natural order - for-each already gives you this.
 * - You need random access or index arithmetic - an index loop is the right tool.
 * - Streams express the transformation more clearly (filter/map/collect) - internal iteration
 *   usually reads better than a hand-written filtering iterator.
 * ------------------------------------------------------------------------------------ */
