// Problem  : Tower of Hanoi - move n disks from a source rod to a target rod using one auxiliary rod,
//            never placing a larger disk on top of a smaller one. Print the moves.
// Approach : Classic divide & conquer recursion in three steps: move the top n-1 disks out of the way
//            onto the auxiliary rod, move the largest disk to the target, then move those n-1 back on top.
// Intuition: The bottom disk can only move when everything above it is elsewhere. So the problem
//            "move n disks" reduces to solving "move n-1 disks" twice around a single trivial move.
//            We never think about the whole stack at once - recursion handles the bookkeeping.
// Time     : O(2^n) - T(n) = 2*T(n-1) + 1, which solves to exactly 2^n - 1 moves (provably minimal)
// Space    : O(n) - recursion depth equals the number of disks
// Trade-off: The exponential cost is INHERENT to the puzzle, not a weakness of the approach - 2^n - 1
//            moves is the proven optimum. This is the canonical example of a problem where recursion
//            is vastly clearer than any iterative formulation.

public class towerOfHanoi {
    public static void main(String[] args) {
        int n = 3;
        TOH(n, 'A', 'B', 'C');   // source A, auxiliary B, target C
        System.out.println("Total moves for " + n + " disks = " + ((1 << n) - 1)); // 2^n - 1 = 7
    }

    // Move n disks from 'source' to 'target', using 'auxiliary' as temporary storage.
    static void TOH(int n, char source, char auxiliary, char target) {
        // BASE CASE: a single disk can be moved directly - no disks sit on top of it.
        if (n == 1) {
            System.out.println("Move disk 1 from rod " + source + " to rod " + target);
            return;
        }

        // STEP 1: relocate the top n-1 disks to the AUXILIARY rod, freeing the largest disk.
        // Note the swap: the target becomes the temporary holding rod for this sub-problem.
        TOH(n - 1, source, target, auxiliary);

        // STEP 2: the largest disk is now exposed - move it straight to the target.
        System.out.println("Move disk " + n + " from rod " + source + " to rod " + target);

        // STEP 3: bring the n-1 disks from the auxiliary rod onto the target, on top of disk n.
        // They are all smaller than disk n, so stacking them there is always legal.
        TOH(n - 1, auxiliary, source, target);
    }
    // Why 2^n - 1: each level doubles the previous work and adds one move -
    // T(1)=1, T(2)=3, T(3)=7, T(4)=15 ... T(n) = 2^n - 1. With 64 disks that is ~1.8x10^19 moves,
    // the origin of the legend that finishing the puzzle would take longer than the age of the universe.
}
