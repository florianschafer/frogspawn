package net.adeptropolis.nephila.graph.backend.primitives;

import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongSwapper;

public class BigLongs implements LongSwapper, LongComparator {

  static final int BIN_BITS = 17;
  private static final int BIN_MASK = (1 << BIN_BITS) - 1;
  private static final long GROWTH_FACTOR = 2L;

  private long[][] data = null;
  private long size = 0;

  public BigLongs(long initialCapacity) {
    resize(initialCapacity);
  }

  public static BigLongs of(long... values) {
    BigLongs longs = new BigLongs(values.length);
    for (int i = 0; i < values.length; i++) longs.set(i, values[i]);
    return longs;
  }

  public void resize(long capacity) {
    int currentbins = (data != null) ? data.length : 0;
    int requestedBins = Math.max(1, (int) (((capacity - 1) >> BIN_BITS) + 1));
    if (requestedBins == currentbins) return;
    long[][] newData = new long[requestedBins][];
    if (data != null) System.arraycopy(data, 0, newData, 0, Math.min(currentbins, requestedBins));
    for (int i = currentbins; i < requestedBins; i++) newData[i] = new long[1 << BIN_BITS];
    data = newData;
  }

  public long get(long idx) {
    return data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)];
  }

  public void set(long idx, long value) {
    int bin = (int) (idx >> BIN_BITS);
    if (bin >= data.length) resize(GROWTH_FACTOR * idx);
    if (idx >= size) size = idx + 1;
    data[bin][(int) (idx & BIN_MASK)] = value;
  }

  public long size() {
    return size;
  }

  public BigLongs sort() {
    LongMergeSort.mergeSort(0, size, this, this);
    return this;
  }

  int bins() {
    return data.length;
  }

  @Override
  public int compare(long idx1, long idx2) {
    return Long.compare(get(idx1), get(idx2));
  }

  @Override
  public void swap(long idx1, long idx2) {
    long val1 = get(idx1);
    long val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BigLongs)) return false;
    BigLongs other = (BigLongs) obj;
    if (size != other.size) return false;
    for (int i = 0; i < size; i++) if (get(i) != other.get(i)) return false;
    return true;
  }

}
