// Program to count distinct elements in the array

import java.util.HashSet;

public class countDistinctEff {
    public static void main(String[] args) {
        int[] arr = { 1, 1, 2, 3, 2, 4, 5 };
        System.out.println(countDistinct(arr)); // calling function to count distinct elements in an Array
    }

    static int countDistinct(int[] arr) {
        HashSet<Integer> set = new HashSet<>(); // creating a Hashset because it already filters out duplicate elements
                                                // while adding the elements to it
        for (int num : arr) {
            set.add(num); // adding elements to Hashset
        }
        return set.size(); // returning the size of hashset because the duplicates are already filtered out
    }
}

// Time complexity: O(n), where n is the number of elements in the array.
// Auxiliary space: O(n)