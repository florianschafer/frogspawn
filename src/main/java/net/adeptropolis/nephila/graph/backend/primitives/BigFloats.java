package net.adeptropolis.nephila.graph.backend.primitives;

import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongSwapper;

public class BigFloats implements LongSwapper, LongComparator {

  static final int BIN_BITS = 13;
  private static final int BIN_MASK = (1 << BIN_BITS) - 1;

  private float[][] data = null;
  private long size = 0;

  public BigFloats(long initialCapacity) {
    resize(initialCapacity);
  }

  public static BigFloats of(float... values) {
    BigFloats floats = new BigFloats(values.length);
    for (int i = 0; i < values.length; i++) floats.set(i, values[i]);
    return floats;
  }

  public void resize(long capacity) {
    int currentbins = (data != null) ? data.length : 0;
    int requestedBins = (int) (capacity >> BIN_BITS) + 1;
    if (requestedBins == currentbins) return;
    float[][] newData = new float[requestedBins][];
    if (data != null) System.arraycopy(data, 0, newData, 0, Math.min(currentbins, requestedBins));
    for (int i = currentbins; i < requestedBins; i++) newData[i] = new float[1 << BIN_BITS];
    data = newData;
  }

  public float get(long idx) {
    return data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)];
  }

  public void set(long idx, float value) {
    if (idx >= size) size = idx + 1;
    data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)] = value;
  }

  public long size() {
    return size;
  }

  public void sort(long size) {
    LongMergeSort.mergeSort(0, size, this, this);
  }

  @Override
  public int compare(long idx1, long idx2) {
    return Float.compare(get(idx1), get(idx2));
  }

  @Override
  public void swap(long idx1, long idx2) {
    float val1 = get(idx1);
    float val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BigFloats)) return false;
    BigFloats other = (BigFloats) obj;
    if (size != other.size) return false;
    for (int i = 0; i < size; i++) if (get(i) != other.get(i)) return false;
    return true;
  }

}