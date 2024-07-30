// Program to generate a Hash and chaining the keys according to the bucket size
import java.util.ArrayList;
import java.util.LinkedList;

// Chaining methods

class myHash { // Creating the Hash class
    int BUCKET; // The bucket size 
    ArrayList<LinkedList<Integer>> table; // Creating 2D array to store the keys
    myHash(int b){ //constructor to create the hash
        BUCKET = b;
        table = new ArrayList<LinkedList<Integer>>();
        for(int i = 0; i < BUCKET; i++){
            table.add(new LinkedList<Integer>());
        }
    }
    void insert(int key){ // Function to insert the element in the Hash
        int i = key % BUCKET;  // Hashing function to get the place where the keys needs to be inserted
        table.get(i).add(key); // Insert the element at the specified index
    }

    boolean search(int key){ // function to search the element in the Hash
        int i = key % BUCKET; // geting the index for the key using the hash function
        return table.get(i).contains(key); // returns true if the key is found in the hash table
    }

    void remove(int key){ // remove the key from the hash table
        int i = key % BUCKET; // getting the index for the key using the hash function
        table.get(i).remove((Integer)key); // removed the key from the hash table
    }
}

public class Chaining {
    public static void main(String[] args) {
        myHash hash = new myHash(10); // creating the object of the hash function with size 10
        hash.insert(2);
        hash.insert(10);
        hash.insert(30);
        hash.insert(15);
        hash.insert(22);
        hash.insert(27);
        hash.search(22);
        hash.remove(22);
        for (int i = 0; i < hash.BUCKET; i++) {
            System.out.println("Bucket " + i + ": " + hash.table.get(i));
        }
    }
}
 //  Load factore: a = n/m. a - load factor, n = number of keys to be inserted, m = number of slots in the hash table
 //  When load factor becomes greater than 0.7, we need to increase the size of the hash table.
 //  Increasing the size of the hash table increases the load factor and also increases the time complexity of insert, search, and delete operations.
 //  However, increasing the size of the hash table can lead to a higher memory usage.
 //  To optimize the load factor and memory usage, we can use a probing technique to handle collisions.
 // Time complexity: O(1 + a) - for search insert and delete operations