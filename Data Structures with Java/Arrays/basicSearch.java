
class basicSearch {

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println(linearSearch(a, 5));
    }

    static int linearSearch(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == x) {
                return (i + 1);
            }
        }
        return -1;
    }
}
