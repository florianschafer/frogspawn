package net.adeptropolis.nephila.graph.implementations.buffers;

class UnsafeDoubleBuffer extends UnsafeBuffer {

  UnsafeDoubleBuffer(long initialSize) {
    super(initialSize, 3);
  }

  void set(long idx, double value) {
    UNSAFE.putDouble(buffer + (idx << elementBits), value);
  }

  double get(long idx) {
    return UNSAFE.getDouble(buffer + (idx << elementBits));
  }

  void swap(long idx1, long idx2) {
    // TODO: May be optimized by using either CAS or getAndSetLong
    double val1 = get(idx1);
    double val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

}
