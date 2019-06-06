package net.adeptropolis.nephila.graph.implementations.primitives.sorting;

import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.implementations.primitives.Ints;

public class IntSortHelper implements LongSwapper, LongComparator {

  private final Ints buffer;

  public IntSortHelper(Ints buffer) {
    this.buffer = buffer;
  }

  @Override
  public int compare(long i1, long i2) {
    return buffer.compareEntries(i1, i2);
  }

  @Override
  public void swap(long idx1, long idx2) {
    buffer.swapEntries(idx1, idx2);
  }

}
