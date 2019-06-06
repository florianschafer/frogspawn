package net.adeptropolis.nephila.graph.implementations.primitives.unsafe;

import net.adeptropolis.nephila.graph.implementations.primitives.LongBuffer;

public class UnsafeLongs implements LongBuffer {

  private long buffer;
  private long size;

  public UnsafeLongs(long initialSize) {
    size = initialSize;
    buffer = Buffers.allocLongs(initialSize);
  }

  @Override
  public void resize(long newSize) {
    size = newSize;
    buffer = Buffers.resizeLongs(buffer, newSize);
  }

  @Override
  public long get(long idx) {
    return Buffers.getLong(buffer, idx);
  }

  @Override
  public void set(long idx, long value) {
    Buffers.setLong(buffer, idx, value);
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
