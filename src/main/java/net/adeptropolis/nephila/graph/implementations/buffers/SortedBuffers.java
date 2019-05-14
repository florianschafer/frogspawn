package net.adeptropolis.nephila.graph.implementations.buffers;

import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.implementations.buffers.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.implementations.buffers.sorting.LongSwapper;

public class SortedBuffers {

  /* INTEGERS */
  /* ============================================= */

  public static void sortInts(long buffer, long size) {
    IntSortHelper helper = new IntSortHelper(buffer);
    LongMergeSort.mergeSort(0, size, helper, helper);
  }

  public static long searchInt(long buffer, long size, int key) {
    return searchInt(buffer, size, key, 0L);
  }

  public static long searchInt(long buffer, long size, int key, long low) {

    long mid;
    long high = size - 1;

    int lowVal = Buffers.getInt(buffer, low);
    int highVal = Buffers.getInt(buffer, high);
    int midVal;

    while ((highVal != lowVal) && (key >= lowVal) && (key <= highVal)) {
//      System.out.println("iter: " + key);
      mid = low + ((key - lowVal) * (high - low) / (highVal - lowVal));
      midVal = Buffers.getInt(buffer, mid);
      if (key > midVal) {
        low = mid + 1;
        lowVal = Buffers.getInt(buffer, low);
      } else if (key < midVal) {
        high = mid - 1;
        highVal = Buffers.getInt(buffer, high);
      } else {
        return mid;
      }
    }

    if (key == lowVal)
      return low;
    else
      return -1;

  }

  private static class IntSortHelper implements LongSwapper, LongComparator {
    private final long buffer;
    private IntSortHelper(long buffer) { this.buffer = buffer; }
    @Override public int compare(long i1, long i2) { return Buffers.compareInts(buffer, i1, i2); }
    @Override public void swap(long idx1, long idx2) { Buffers.swapInts(buffer, idx1, idx2); }
  }

}
