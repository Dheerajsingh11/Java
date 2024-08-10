package Questions;

import java.util.Arrays;

// Program to find the maximum number guests we can meet if the time is given
public class meetingMaxGuest {
    public static void main(String[] args) {
        int arr[] = { 800, 700, 600, 500 };
        int dep[] = { 840, 820, 830, 530 };
        System.out.println("Maximum guests: " + maxGuests(arr, dep, arr.length));
    }

    static int maxGuests(int arr[], int dep[], int n) {
        Arrays.sort(arr);
        Arrays.sort(dep);
        int i = 1, j = 0, res = 1, curr = 1;
        while (i < n && j < n) {
            if (arr[i] <= dep[j]) {
                curr++;
                i++;
            } else {
                curr--;
                j++;
            }
            res = Math.max(res, curr);
        }
        return res;
    }
}
// Time complexity: O(nlog(n))
// Auxiliary space: O(1)