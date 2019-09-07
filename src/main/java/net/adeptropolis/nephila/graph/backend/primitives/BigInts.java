package net.adeptropolis.nephila.graph.backend.primitives;

import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongSwapper;

public class BigInts implements LongSwapper, LongComparator {

  static final int BIN_BITS = 17;
  private static final int BIN_MASK = (1 << BIN_BITS) - 1;
  private static final long GROWTH_FACTOR = 2L;

  private int[][] data = null;
  private long size = 0;

  public BigInts(long initialCapacity) {
    resize(initialCapacity);
  }

  public static BigInts of(int... values) {
    BigInts ints = new BigInts(values.length);
    for (int i = 0; i < values.length; i++) ints.set(i, values[i]);
    return ints;
  }

  public void resize(long capacity) {
    int currentbins = (data != null) ? data.length : 0;
    int requestedBins = Math.max(1, (int) (((capacity - 1) >> BIN_BITS) + 1));
    if (requestedBins == currentbins) return;
    int[][] newData = new int[requestedBins][];
    if (data != null) System.arraycopy(data, 0, newData, 0, Math.min(currentbins, requestedBins));
    for (int i = currentbins; i < requestedBins; i++) newData[i] = new int[1 << BIN_BITS];
    data = newData;
  }

  public int get(long idx) {
    return data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)];
  }

  public void set(long idx, int value) {
    int bin = (int) (idx >> BIN_BITS);
    if (bin >= data.length) resize(GROWTH_FACTOR * idx);
    if (idx >= size) size = idx + 1;
    data[bin][(int) (idx & BIN_MASK)] = value;
  }

  public long size() {
    return size;
  }

  public BigInts sort() {
    LongMergeSort.mergeSort(0, size, this, this);
    return this;
  }

  int bins() {
    return data.length;
  }

  @Override
  public int compare(long idx1, long idx2) {
    return Integer.compare(get(idx1), get(idx2));
  }

  @Override
  public void swap(long idx1, long idx2) {
    int val1 = get(idx1);
    int val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BigInts)) return false;
    BigInts other = (BigInts) obj;
    if (size != other.size) return false;
    for (int i = 0; i < size; i++) if (get(i) != other.get(i)) return false;
    return true;
  }
}
