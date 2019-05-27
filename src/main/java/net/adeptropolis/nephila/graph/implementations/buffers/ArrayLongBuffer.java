package net.adeptropolis.nephila.graph.implementations.buffers;

public class ArrayLongBuffer implements LongBuffer {

  static final int BIN_BITS = 22; // 4M
  private static final int BIN_MASK = (1 << BIN_BITS) - 1;

  private long[][] data = null;
  private long size;

  public ArrayLongBuffer(long initialSize) {
    resize(initialSize);
  }

  @Override
  public void resize(long capacity) {
    size = capacity;
    int currentbins = (data != null) ? data.length : 0;
    int requestedBins = (int) (capacity >> BIN_BITS) + 1;
    if (requestedBins == currentbins) return;
    long[][] newData = new long[requestedBins][];
    if (data != null) {
      System.arraycopy(data, 0, newData, 0, Math.min(currentbins, requestedBins));
    }
    for (int i = currentbins; i < requestedBins; i++) newData[i] = new long[1 << BIN_BITS];
    data = newData;
  }

  @Override
  public long get(long idx) {
    return data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)];
  }

  @Override
  public void set(long idx, long value) {
    data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)] = value;
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public void free() {
    data = null;
  }

}
