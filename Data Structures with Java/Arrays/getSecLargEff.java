public class getSecLargEff {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        System.out.println(secondLargest(arr));
    }

    static int secondLargest(int arr[]) {
        int res = -1, largest = 0;
        for (int i = 1; i <= arr.length - 1; i++) {
            if (arr[i] > arr[largest]) {
                res = largest;
                largest = i;
            } else if (arr[i] != arr[largest]) {
                if (res == -1 || arr[i] > arr[res]) {
                    res = i;
                }
            }
        }
        return (res + 1);
    }
}
