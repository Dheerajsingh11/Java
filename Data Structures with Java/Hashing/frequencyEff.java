
// Program to count frequency of each element in array
import java.util.HashMap;
import java.util.Map;

// Program to count frequency of each element in array
public class frequencyEff {
    public static void main(String[] args) {
        int[] arr = { 1, 2, 2, 3, 3, 3, 4, 4, 4, 4 };
        int n = arr.length;
        countFreq(arr, n); // call function to count frequency of each element in Array
    }

    static void countFreq(int[] arr, int n) {
        HashMap<Integer, Integer> h = new HashMap<>();
        for (int x : arr) {
            h.put(x, h.getOrDefault(x, 0) + 1); // make key value pair, where key is element in array and value is
                                                // count. getOrDefault function returns if the value is present in the
                                                // Hash or return the second value i.e 0 in our case
        }
        for (Map.Entry<Integer, Integer> e : h.entrySet()) { // loop into HashMap
            System.out.println(e.getKey() + " " + e.getValue()); // Enter the key value pair
        }
    }
}

// Time Complexity: THETA(n)
// Auxiliary space: O(n)
