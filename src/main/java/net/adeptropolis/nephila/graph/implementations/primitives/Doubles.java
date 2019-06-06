package net.adeptropolis.nephila.graph.implementations.primitives;

public interface Doubles {

  void resize(long newSize);

  long size();

  void free();

  default void swapEntries(long idx1, long idx2) {
    double val1 = get(idx1);
    double val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  double get(long idx);

  void set(long idx, double value);

  default int compareEntries(long idx1, long idx2) {
    return Double.compare(get(idx1), get(idx2));
  }

}
