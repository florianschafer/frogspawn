package net.adeptropolis.nephila.graph.implementations.buffers;

import java.util.List;
import java.util.stream.IntStream;

public class UnsafeLongBuffer extends UnsafeBuffer {

  public UnsafeLongBuffer(long initialSize) {
    super(initialSize, 3);
  }

  public void set(long idx, long value) {
    UNSAFE.putLong(buffer + (idx << elementBits), value);
  }

  public long get(long idx) {
    return UNSAFE.getLong(buffer + (idx << elementBits));
  }

}
