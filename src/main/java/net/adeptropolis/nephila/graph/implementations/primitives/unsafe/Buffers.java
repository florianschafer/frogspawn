package net.adeptropolis.nephila.graph.implementations.primitives.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Buffers {

  // TODO: Swaps may be optimized by using either CAS or getAndSetLong

  private static final Unsafe UNSAFE = getUnsafe();

  private static final int INT_SHIFT = 2;
  private static final int LONG_SHIFT = 3;
  private static final int DOUBLE_SHIFT = 3;

  /* SHARED */
  /* ============================================= */

  public static void free(long buffer) {
    UNSAFE.freeMemory(buffer);
  }

  private static long alloc(long bytes) {
    return UNSAFE.allocateMemory(bytes);
  }

  /* INTEGERS */
  /* ============================================= */

  public static long allocInts(long size) {
    return alloc(size << INT_SHIFT);
  }

  public static long resizeInts(long buffer, long size) {
    return UNSAFE.reallocateMemory(buffer, size << INT_SHIFT);
  }

  public static int getInt(long buffer, long idx) {
    return UNSAFE.getInt(buffer + (idx << INT_SHIFT));
  }

  public static void setInt(long buffer, long idx, int value) {
    UNSAFE.putInt(buffer + (idx << INT_SHIFT), value);
  }

  public static void swapInts(long buffer, long idx1, long idx2) {
    int val1 = getInt(buffer, idx1);
    int val2 = getInt(buffer, idx2);
    setInt(buffer, idx1, val2);
    setInt(buffer, idx2, val1);
  }

  public static int[] toIntArray(long buffer, int size) {
    int[] arr = new int[size];
    for (int i = 0; i < size; i++) arr[i] = getInt(buffer, i);
    return arr;
  }

  public static int compareInts(long buffer, long idx1, long idx2) {
    return Integer.compare(getInt(buffer, idx1), getInt(buffer, idx2));
  }

  /* LONGS */
  /* ============================================= */

  public static long allocLongs(long size) {
    return alloc(size << LONG_SHIFT);
  }

  public static long resizeLongs(long buffer, long size) {
    return UNSAFE.reallocateMemory(buffer, size << LONG_SHIFT);
  }

  public static long getLong(long buffer, long idx) {
    return UNSAFE.getLong(buffer + (idx << LONG_SHIFT));
  }

  public static void setLong(long buffer, long idx, long value) {
    UNSAFE.putLong(buffer + (idx << LONG_SHIFT), value);
  }

  public static void swapLongs(long buffer, long idx1, long idx2) {
    long val1 = getLong(buffer, idx1);
    long val2 = getLong(buffer, idx2);
    setLong(buffer, idx1, val2);
    setLong(buffer, idx2, val1);
  }

  public static long[] toLongArray(long buffer, int size) {
    long[] arr = new long[size];
    for (int i = 0; i < size; i++) arr[i] = getLong(buffer, i);
    return arr;
  }

  public static int compareLongs(long buffer, long idx1, long idx2) {
    return Long.compare(getLong(buffer, idx1), getLong(buffer, idx2));
  }


  /* DOUBLES */
  /* ============================================= */

  public static long allocDoubles(long size) {
    return alloc(size << DOUBLE_SHIFT);
  }

  public static long resizeDoubles(long buffer, long size) {
    return UNSAFE.reallocateMemory(buffer, size << DOUBLE_SHIFT);
  }

  public static double getDouble(long buffer, long idx) {
    return UNSAFE.getDouble(buffer + (idx << DOUBLE_SHIFT));
  }

  public static void setDouble(long buffer, long idx, double value) {
    UNSAFE.putDouble(buffer + (idx << DOUBLE_SHIFT), value);
  }

  public static void swapDoubles(long buffer, long idx1, long idx2) {
    double val1 = getDouble(buffer, idx1);
    double val2 = getDouble(buffer, idx2);
    setDouble(buffer, idx1, val2);
    setDouble(buffer, idx2, val1);
  }

  public static double[] toDoubleArray(long buffer, int size) {
    double[] arr = new double[size];
    for (int i = 0; i < size; i++) arr[i] = getDouble(buffer, i);
    return arr;
  }

  public static int compareDoubles(long buffer, long idx1, long idx2) {
    return Double.compare(getDouble(buffer, idx1), getDouble(buffer, idx2));
  }


  /* ============================================= */

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
