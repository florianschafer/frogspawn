package net.adeptropolis.nephila.graph.implementations.buffers;

public class UnsafeIntBuffer extends UnsafeBuffer {

  UnsafeIntBuffer(long initialSize) {
    super(initialSize, 2);
  }

  void set(long idx, int value) {
    UNSAFE.putInt(buffer + (idx << elementBits), value);
  }

  public int get(long idx) {
    return UNSAFE.getInt(buffer + (idx << elementBits));
  }

  void swap(long idx1, long idx2) {
    // TODO: May be optimized by using either CAS or getAndSetLong
    int val1 = get(idx1);
    int val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

}
