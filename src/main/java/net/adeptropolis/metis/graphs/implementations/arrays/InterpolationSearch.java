/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.implementations.arrays;

/**
 * Perform interpolation search on both primitive as well as big int arrays.
 * Note that the underlying data structure is <b>ASSUMED TO BE SORTED</b>.
 */

public class InterpolationSearch {

  private InterpolationSearch() {
  }

  /**
   * Search in BigInts
   *
   * @param ints Big integer array
   * @param key  Search key
   * @param low  Lower end of the search window. Inclusive.
   * @param high Higher end of the search window. Inclusive(!)
   * @return Index of the first item matching the key. -1 otherwise.
   */

  public static long search(BigInts ints, int key, long low, long high) {

    int lowVal = ints.get(low);

    if (lowVal == key) {
      return low;
    }

    int highVal = ints.get(high);

    while (key >= lowVal && key <= highVal) {

      long mid = (int) (low + (((key - lowVal) * (high - low)) / (highVal - lowVal)));
      int midVal = ints.get(mid);

      if (key > midVal) {
        low = mid + 1;
        lowVal = ints.get(low);
      } else if (key < midVal) {
        high = mid - 1;
        highVal = ints.get(high);
      } else {
        return mid;
      }

    }

    return -1;
  }

  /**
   * Search in int[]
   *
   * @param array Primitive int array to be sorted
   * @param key   Search key
   * @param low   Lower end of the search window. Inclusive.
   * @param high  Higher end of the search window. Inclusive(!)
   * @return Index of the first item matching the key. -1 otherwise.
   */

  public static int search(int[] array, int key, int low, int high) {

    int lowVal = array[low];

    if (lowVal == key) {
      return low;
    }

    int highVal = array[high];

    while (key >= lowVal && key <= highVal) {

      int mid = (int) (low + (((key - lowVal) * (long) (high - low)) / (highVal - lowVal)));
      int midVal = array[mid];

      if (key > midVal) {
        low = mid + 1;
        lowVal = array[low];
      } else if (key < midVal) {
        high = mid - 1;
        highVal = array[high];
      } else {
        return mid;
      }

    }

    return -1;
  }

}
