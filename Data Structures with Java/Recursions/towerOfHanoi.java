// Program to generate the tower of Hanoi
public class towerOfHanoi {
    public static void main(String[] args){
        TOH(3, 'A', 'B', 'C'); //function to generate the tower of Hanoi
    }

    static void TOH(int n, char source, char auxiliary, char target) {
        if (n == 1) { //Base case
            //if the remaining disk is 1 then move the last disk to the target
            System.out.println("Move disk " + n + " from rod " + source + " to rod " + target);
            return;
        }
        TOH(n-1, source, target, auxiliary); // TOH function call to move the n-1 disk to the auxiliary
        System.out.println("Move disk " + n + " from rod " + source + " to rod " + target);
        TOH(n-1, auxiliary, source, target); //TOH function call to move the n-1 disk from auxiliary to target
    }
}

// Time complexity: O(2^n)