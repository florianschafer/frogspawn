/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.helpers;

import com.google.common.base.Preconditions;

/**
 * Provides some basic vector operations
 */

public class Vectors {

  private Vectors() {
  }

  /**
   * Compute the Euclidean norm of a vector
   *
   * @param v A vector
   * @return Norm of <code>v</code>
   */

  public static double norm2(double[] v) {
    double norm = 0;
    for (double value : v) {
      norm += value * value;
    }
    return Math.sqrt(norm);
  }

  /**
   * Compute the scalar product of two vectors.
   *
   * @param v left argument
   * @param w right argument
   * @return <code>v ⋅ w</code>
   */

  public static double scalarProduct(double[] v, double[] w) {
    Preconditions.checkArgument(v.length == w.length, String.format("Argument dimension mismatch: %d != %d", v.length, w.length));
    double prod = 0;
    for (int i = 0; i < v.length; i++) {
      prod += v[i] * w[i];
    }
    return prod;
  }

  /**
   * In-place vector normalization using the regular 2-norm. In addition, this method will modify the sign of the resulting
   * vector s.t. the sign of the first entry is always positive.
   *
   * @param v A vector
   */

  public static void normalize2Sig(double[] v) {
    double sig = v[0] != 0 ? Math.signum(v[0]) : 1;
    double sum = 0;
    for (double value : v) sum += value * value;
    double norm = Math.sqrt(sum);
    for (int i = 0; i < v.length; i++) v[i] = sig * v[i] / norm;
  }


}