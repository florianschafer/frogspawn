/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.implementations.arrays;

import com.google.common.base.Preconditions;
import net.adeptropolis.nephila.graphs.implementations.arrays.LongMergeSort.SortOps;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * A big (i.e. long-indexed) array of ints.
 *
 * @author Florian Schaefer
 * @author florian@adeptropolis.net
 * @version 1.0
 * @since 1.0
 */

public class BigInts implements SortOps {

  static final int BIN_BITS = 17;
  private static final int BIN_MASK = (1 << BIN_BITS) - 1;
  private static final long GROWTH_FACTOR = 2L;

  private int[][] data = null;
  private long size = 0;

  /**
   * Constructor
   *
   * @param initialCapacity Initial storage capacity
   */

  public BigInts(long initialCapacity) {
    resize(initialCapacity);
  }

  /**
   * Create a new BigInts instance from a given list of ints
   *
   * @param values Any number of ints
   * @return new BigInts instance
   */

  public static BigInts of(int... values) {
    BigInts ints = new BigInts(values.length);
    for (int i = 0; i < values.length; i++) ints.set(i, values[i]);
    return ints;
  }

  /**
   * Resize to a given capacity
   *
   * @param capacity Requested storage capacity
   */

  public void resize(long capacity) {
    int currentbins = (data != null) ? data.length : 0;
    int requestedBins = Math.max(1, (int) (((capacity - 1) >> BIN_BITS) + 1));
    if (requestedBins == currentbins) return;
    int[][] newData = new int[requestedBins][];
    if (data != null) System.arraycopy(data, 0, newData, 0, Math.min(currentbins, requestedBins));
    for (int i = currentbins; i < requestedBins; i++) newData[i] = new int[1 << BIN_BITS];
    if (currentbins > requestedBins) size = capacity;
    data = newData;
  }

  /**
   * Retrieve value
   *
   * @param idx Index
   * @return Value at index idx
   */

  public int get(long idx) {
    return data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)];
  }

  /**
   * Set value
   *
   * @param idx   Index
   * @param value Value
   */

  public void set(long idx, int value) {
    int bin = (int) (idx >> BIN_BITS);
    if (bin >= data.length) resize(GROWTH_FACTOR * idx);
    if (idx >= size) size = idx + 1;
    data[bin][(int) (idx & BIN_MASK)] = value;
  }

  /**
   * Return size
   *
   * @return Largest stored index + 1
   */

  public long size() {
    return size;
  }

  /**
   * Sort (in-place)
   *
   * @return this
   */

  public BigInts sort() {
    LongMergeSort.mergeSort(0, size, this);
    return this;
  }

  /**
   * Bin count
   *
   * @return currently used number of storage bins
   */

  int bins() {
    return data.length;
  }

  /**
   * Compare two elements
   *
   * @param idx1 Index
   * @param idx2 Index
   * @return Result of comparing the element at idx1 with the one at idx2
   */

  @Override
  public int compare(long idx1, long idx2) {
    return Integer.compare(get(idx1), get(idx2));
  }


  /**
   * Swap values between two indices
   *
   * @param idx1 Index
   * @param idx2 Index
   */

  @Override
  public void swap(long idx1, long idx2) {
    int val1 = get(idx1);
    int val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BigInts)) return false;
    BigInts other = (BigInts) obj;
    if (size != other.size) return false;
    for (int i = 0; i < size; i++) if (get(i) != other.get(i)) return false;
    return true;
  }

  @Override
  public String toString() {
    return LongStream.range(0, size()).mapToObj(i -> String.valueOf(get(i))).collect(Collectors.joining(", "));
  }

  public int[] toArray() {
    Preconditions.checkState(size <= Integer.MAX_VALUE);
    int[] arr = new int[Math.toIntExact(size())];
    for (int i = 0; i < size(); i++) {
      arr[i] = get(i);
    }
    return arr;
  }

}
