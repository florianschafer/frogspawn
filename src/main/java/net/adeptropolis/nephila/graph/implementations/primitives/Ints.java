package net.adeptropolis.nephila.graph.implementations.primitives;

import net.adeptropolis.nephila.graph.implementations.primitives.sorting.IntSortHelper;
import net.adeptropolis.nephila.graph.implementations.primitives.sorting.LongMergeSort;

public interface Ints {

  void resize(long newSize);

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

  int get(long idx);

  void set(long idx, int value);

  default int compareEntries(long idx1, long idx2) {
    return Integer.compare(get(idx1), get(idx2));
  }

}
