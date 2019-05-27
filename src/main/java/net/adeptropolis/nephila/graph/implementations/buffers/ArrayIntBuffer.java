package net.adeptropolis.nephila.graph.implementations.buffers;

public class ArrayIntBuffer implements IntBuffer {

  static final int BIN_BITS = 22; // 4M
  private static final int BIN_MASK = (1 << BIN_BITS) - 1;

  private int[][] data = null;
  private long size;

  public ArrayIntBuffer(long initialSize) {
    resize(initialSize);
  }

  @Override
  public void resize(long capacity) {
    size = capacity;
    int currentbins = (data != null) ? data.length : 0;
    int requestedBins = (int) (capacity >> BIN_BITS) + 1;
    if (requestedBins == currentbins) return;
    int[][] newData = new int[requestedBins][];
    if (data != null) {
      System.arraycopy(data, 0, newData, 0, Math.min(currentbins, requestedBins));
    }
    for (int i = currentbins; i < requestedBins; i++) newData[i] = new int[1 << BIN_BITS];
    data = newData;
  }

  @Override
  public int get(long idx) {
    return data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)];
  }

  @Override
  public void set(long idx, int value) {
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
