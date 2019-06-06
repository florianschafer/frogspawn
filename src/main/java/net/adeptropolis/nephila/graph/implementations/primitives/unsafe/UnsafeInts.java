package net.adeptropolis.nephila.graph.implementations.primitives.unsafe;

import net.adeptropolis.nephila.graph.implementations.primitives.Ints;

public class UnsafeInts implements Ints {

  private long buffer;
  private long size;

  public UnsafeInts(long initialSize) {
    size = initialSize;
    buffer = Buffers.allocInts(initialSize);
  }

  @Override
  public void resize(long newSize) {
    size = newSize;
    buffer = Buffers.resizeInts(buffer, newSize);
  }

  @Override
  public int get(long idx) {
    return Buffers.getInt(buffer, idx);
  }

  @Override
  public void set(long idx, int value) {
    Buffers.setInt(buffer, idx, value);
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public void free() {
    Buffers.free(buffer);
  }

}
