package net.adeptropolis.nephila.graph.implementations.buffers;

public interface DoubleBuffer {

  void resize(long newSize);

  double get(long idx);

  void set(long idx, double value);

  long size();

  void free();

  default void swapEntries(long idx1, long idx2) {
    double val1 = get(idx1);
    double val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  default int compareEntries(long idx1, long idx2) {
    return Double.compare(get(idx1), get(idx2));
  }

}
