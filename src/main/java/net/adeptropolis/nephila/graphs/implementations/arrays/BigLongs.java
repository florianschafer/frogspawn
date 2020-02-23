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
 * A big (i.e. long-indexed) array of longs.
 *
 * @author Florian Schaefer
 * @author florian@adeptropolis.net
 * @version 1.0
 * @since 1.0
 */

public class BigLongs implements SortOps {

  static final int BIN_BITS = 17;
  private static final int BIN_MASK = (1 << BIN_BITS) - 1;
  private static final long GROWTH_FACTOR = 2L;

  private long[][] data = null;
  private long size = 0;

  /**
   * Constructor
   *
   * @param initialCapacity Initial storage capacity
   */

  public BigLongs(long initialCapacity) {
    resize(initialCapacity);
  }

  /**
   * Create a new BigLongs instance from a given list of longs
   *
   * @param values Any number of longs
   * @return new BigLongs instance
   */

  public static BigLongs of(long... values) {
    BigLongs longs = new BigLongs(values.length);
    for (int i = 0; i < values.length; i++) longs.set(i, values[i]);
    return longs;
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
    long[][] newData = new long[requestedBins][];
    if (data != null) System.arraycopy(data, 0, newData, 0, Math.min(currentbins, requestedBins));
    for (int i = currentbins; i < requestedBins; i++) newData[i] = new long[1 << BIN_BITS];
    if (currentbins > requestedBins) size = capacity;
    data = newData;
  }

  /**
   * Retrieve value
   *
   * @param idx Index
   * @return Value at index idx
   */

  public long get(long idx) {
    return data[(int) (idx >> BIN_BITS)][(int) (idx & BIN_MASK)];
  }

  /**
   * Set value
   *
   * @param idx   Index
   * @param value Value
   */

  public void set(long idx, long value) {
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

  public BigLongs sort() {
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
    return Long.compare(get(idx1), get(idx2));
  }

  /**
   * Swap values between two indices
   *
   * @param idx1 Index
   * @param idx2 Index
   */

  @Override
  public void swap(long idx1, long idx2) {
    long val1 = get(idx1);
    long val2 = get(idx2);
    set(idx1, val2);
    set(idx2, val1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BigLongs)) return false;
    BigLongs other = (BigLongs) obj;
    if (size != other.size) return false;
    for (int i = 0; i < size; i++) if (get(i) != other.get(i)) return false;
    return true;
  }

  @Override
  public String toString() {
    return LongStream.range(0, size()).mapToObj(i -> String.valueOf(get(i))).collect(Collectors.joining(", "));
  }

  public long[] toArray() {
    Preconditions.checkState(size <= Integer.MAX_VALUE);
    long[] arr = new long[Math.toIntExact(size())];
    for (int i = 0; i < size(); i++) {
      arr[i] = get(i);
    }
    return arr;
  }


}
