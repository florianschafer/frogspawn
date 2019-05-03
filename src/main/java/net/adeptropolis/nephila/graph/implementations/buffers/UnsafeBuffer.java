package net.adeptropolis.nephila.graph.implementations.buffers;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

class UnsafeBuffer {

  static final Unsafe UNSAFE = getUnsafe();

  final int elementBits;
  long buffer;

  UnsafeBuffer(long initialSize, int elementBits) {
    this.elementBits = elementBits;
    this.buffer = UNSAFE.allocateMemory(initialSize << elementBits);
  }

  void resize(long newSize) {
    buffer = UNSAFE.reallocateMemory(buffer, newSize << elementBits);
  }

  void free() {
    UNSAFE.freeMemory(buffer);
  }

  private static Unsafe getUnsafe() {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      return (Unsafe) f.get(null);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to create Unsafe instance", e);
    }
  }

}
