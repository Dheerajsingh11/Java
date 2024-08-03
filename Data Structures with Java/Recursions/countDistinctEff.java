// Program to count distinct elements in the array

import java.util.HashSet;

public class countDistinctEff {
    public static void main(String[] args) {
        int[] arr = { 1, 1, 2, 3, 2, 4, 5 };
        System.out.println(countDistinct(arr));
    }

    static int countDistinct(int[] arr) {
        HashSet<Integer> set = new HashSet<>();
        for (int num : arr) {
            set.add(num);
        }
        return set.size();
    }
}

// Time complexity: O(n), where n is the number of elements in the array.
// Auxiliary space: O(n)