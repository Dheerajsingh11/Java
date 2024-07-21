
public class largestElementEff {

    public static void main(String[] args) {
        int arr[] = {10, 12, 8, 21};
        System.out.println(largestElement(arr));
    }

    static int largestElement(int[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
}

//Time complexity THETA(number of elements)
