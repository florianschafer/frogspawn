/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.helpers;

/**
 * Provides some basic array operations
 */

public class Arr {

  private Arr() {
  }

  /**
   * Swap two elements of an <code>int</code> array
   *
   * @param arr An array
   * @param i   Left index
   * @param j   Right index
   */

  public static void swap(int[] arr, int i, int j) {
    int tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Swap two elements of a <code>double</code> array
   *
   * @param arr An array
   * @param i   Left index
   * @param j   Right index
   */

  public static void swap(double[] arr, int i, int j) {
    double tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  /**
   * Shrink an <code>int</code> array down to a given size.
   *
   * @param arr  Original array
   * @param size Requested size
   * @return A copy of <code>arr</code> with at most <code>size</code> elements. If the size of <code>arr</code> is already
   * smaller or equal to the requested size, return the original array instead.
   */

  public static int[] shrink(int[] arr, int size) {
    if (arr.length <= size) {
      return arr;
    }
    int[] shrunk = new int[size];
    System.arraycopy(arr, 0, shrunk, 0, size);
    return shrunk;
  }

  /**
   * Shrink an <code>double</code> array down to a given size.
   *
   * @param arr  Original array
   * @param size Requested size
   * @return A copy of <code>arr</code> with at most <code>size</code> elements. If the size of <code>arr</code> is already
   * smaller or equal to the requested size, return the original array instead.
   */

  public static double[] shrink(double[] arr, int size) {
    if (arr.length <= size) {
      return arr;
    }
    double[] shrunk = new double[size];
    System.arraycopy(arr, 0, shrunk, 0, size);
    return shrunk;
  }

}
