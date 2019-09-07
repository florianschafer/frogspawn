package net.adeptropolis.nephila.graph.backend.primitives;

import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongSwapper;

public class BigDoubles implements LongSwapper, LongComparator {

  static final int BIN_BITS = 13;
  private static final int BIN_MASK = (1 << BIN_BITS) - 1;

  private double[][] data = null;
  private long size = 0;

  public BigDoubles(long initialCapacity) {
    resize(initialCapacity);
  }

  public static BigDoubles of(double... values) {
    BigDoubles doubles = new BigDoubles(values.length);
    for (int i = 0; i < values.length; i++) doubles.set(i, values[i]);
    return doubles;
  }

  public void resize(long capacity) {
    int currentbins = (data != null) ? data.length : 0;
    int requestedBins = (int) ((capacity >> BIN_BITS) + 1);
    if (requestedBins == currentbins) return;
    double[][] newData = new double[requestedBins][];
    if (data != null) System.arraycopy(data, 0, newData, 0, Math.min(currentbins, requestedBins));
    for (int i = currentbins; i < requestedBins; i++) newData[i] = new double[1 << BIN_BITS];
    data = newData;
  }

  public double get(long idx) {
    return data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)];
  }

  public void set(long idx, double value) {
    if (idx >= size) size = idx + 1;
    data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)] = value;
  }

  public long size() {
    return size;
  }

  public BigDoubles sort() {
    LongMergeSort.mergeSort(0, size, this, this);
    return this;
  }

  @Override
  public int compare(long idx1, long idx2) {
    return Double.compare(get(idx1), get(idx2));
  }

  @Override
  public void swap(long idx1, long idx2) {
    double val1 = get(idx1);
    double val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BigDoubles)) return false;
    BigDoubles other = (BigDoubles) obj;
    if (size != other.size) return false;
    for (int i = 0; i < size; i++) if (get(i) != other.get(i)) return false;
    return true;
  }

}
