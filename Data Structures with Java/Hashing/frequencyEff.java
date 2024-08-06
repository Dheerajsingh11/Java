// Program to count frequency of each element in array
import java.util.HashMap;
import java.util.Map;

// Program to count frequency of each element in array
public class frequencyEff {
    public static void main(String[] args){
        int[] arr = {1, 2, 2, 3, 3, 3, 4, 4, 4, 4};
        int n = arr.length;
        countFreq(arr, n);
    }

    static void countFreq(int[] arr, int n){
        HashMap<Integer, Integer> h = new HashMap<>();
        for(int x: arr){
            h.put(x, h.getOrDefault(x, 0)+1);
        }
        for(Map.Entry<Integer, Integer> e : h.entrySet()){
            System.out.println(e.getKey() +" "+ e.getValue());
        }
    }
}

//Time Complexity: THETA(n)
//Auxiliary space: O(n)
