
public class largestElementNaive {

    public static void main(String[] args) {
        int arr[] = {10, 12, 8, 21};
        System.out.println(largestElement(arr));
    }

    static int largestElement(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (arr[j] > arr[i]) {
                    flag = false;
                    break;
                }
            }
            if (flag == true) {
                return arr[i];
            }
        }
        return -1;
    }
}

//Best case time complexity THETA(n)
//Worst case time complexity THETA(n^2)
//Space complexity THETA(1)

