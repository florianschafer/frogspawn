/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.helpers;

public class Arr {

  private Arr() {
  }

  public static void swap(int[] arr, int i, int j) {
    int tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  public static void swap(double[] arr, int i, int j) {
    double tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }

  public static int[] shrink(int[] arr, int size) {
    if (arr.length <= size) {
      return arr;
    }
    int[] shrunk = new int[size];
    System.arraycopy(arr, 0, shrunk, 0, size);
    return shrunk;
  }

  public static double[] shrink(double[] arr, int size) {
    if (arr.length <= size) {
      return arr;
    }
    double[] shrunk = new double[size];
    System.arraycopy(arr, 0, shrunk, 0, size);
    return shrunk;
  }

  public static float[] asFloats(double[] doubles) {
    float[] floats = new float[doubles.length];
    for (int i = 0; i < doubles.length; i++) {
      floats[i] = (float) doubles[i];
    }
    return floats;
  }


}
