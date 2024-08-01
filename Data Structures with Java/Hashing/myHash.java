// Source code is decompiled from a .class file using FernFlower decompiler.
import java.util.ArrayList;
import java.util.LinkedList;

class myHash {
   int BUCKET;
   ArrayList<LinkedList<Integer>> table;

   myHash(int var1) {
      this.BUCKET = var1;
      this.table = new ArrayList();

      for(int var2 = 0; var2 < this.BUCKET; ++var2) {
         this.table.add(new LinkedList());
      }

   }

   void insert(int var1) {
      int var2 = var1 % this.BUCKET;
      ((LinkedList)this.table.get(var2)).add(var1);
   }

   boolean search(int var1) {
      int var2 = var1 % this.BUCKET;
      return ((LinkedList)this.table.get(var2)).contains(var1);
   }

   void remove(int var1) {
      int var2 = var1 % this.BUCKET;
      ((LinkedList)this.table.get(var2)).remove(var1);
   }
}
