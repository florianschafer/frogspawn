package net.adeptropolis.nephila.graph.implementations.primitives;

public interface Longs {

  void resize(long newSize);

  long size();

  void free();

  default void swapEntries(long idx1, long idx2) {
    long val1 = get(idx1);
    long val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  long get(long idx);

  void set(long idx, long value);

  default int compareEntries(long idx1, long idx2) {
    return Long.compare(get(idx1), get(idx2));
  }

}
