package net.adeptropolis.nephila.graph.implementations.buffers.unsafe;

import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;

public class UnsafeDoubleBuffer implements DoubleBuffer {

  private long buffer;
  private long size;

  public UnsafeDoubleBuffer(long initialSize) {
    size = initialSize;
    buffer = Buffers.allocDoubles(initialSize);
  }

  @Override
  public void resize(long newSize) {
    size = newSize;
    buffer = Buffers.resizeDoubles(buffer, newSize);
  }

  @Override
  public double get(long idx) {
    return Buffers.getDouble(buffer, idx);
  }

  @Override
  public void set(long idx, double value) {
    Buffers.setDouble(buffer, idx, value);
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
