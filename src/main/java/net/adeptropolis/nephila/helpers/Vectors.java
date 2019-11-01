package net.adeptropolis.nephila.helpers;

import com.google.common.base.Preconditions;

public class Vectors {

  public static double L1Norm(double[] v) {
    // This is NOT the official L1 norm. Assuming ∀i ∈ [0, vec.length): vec[i] >= 0
    double norm = 0;
    for (int i = 0; i < v.length; i++) {
      norm += v[i];
    }
    return norm;
  }

  public static double scalarProduct(double[] v, double[] w) {
    Preconditions.checkArgument(v.length == w.length, "Argument dimension mismatch");
    double prod = 0;
    for (int i = 0; i < v.length; i++) {
      prod += v[i] * w[i];
    }
    return prod;
  }

}