package net.adeptropolis.nephila.graph.implementations.buffers;

import net.adeptropolis.nephila.graph.implementations.buffers.sorting.IntSortHelper;
import net.adeptropolis.nephila.graph.implementations.buffers.sorting.LongMergeSort;

public interface IntBuffer {

  void resize(long newSize);

  int get(long idx);

  void set(long idx, int value);

  long size();

  void free();

  default void sort(long size) {
    IntSortHelper helper = new IntSortHelper(this);
    LongMergeSort.mergeSort(0, size, helper, helper);
  }

  default void swapEntries(long idx1, long idx2) {
    int val1 = get(idx1);
    int val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  default int compareEntries(long idx1, long idx2) {
    return Integer.compare(get(idx1), get(idx2));
  }

  default long searchSorted(int key, long size) {
    return searchSorted(key, 0L, size - 1);
  }

  default long searchSorted(int key, long low, long high) {

    long mid;

    int lowVal = get(low);
    int highVal = get(high);
    int midVal;

    while ((highVal != lowVal) && (key >= lowVal) && (key <= highVal)) {
      mid = low + ((key - lowVal) * (high - low) / (highVal - lowVal));
      midVal = get(mid);
      if (key > midVal) {
        low = mid + 1;
        lowVal = get(low);
      } else if (key < midVal) {
        high = mid - 1;
        highVal = get(high);
      } else {
        return mid;
      }
    }

    if (key == lowVal)
      return low;
    else
      return -1;

  }

}
