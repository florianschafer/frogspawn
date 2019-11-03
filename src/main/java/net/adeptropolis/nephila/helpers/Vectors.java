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
    Preconditions.checkArgument(v.length == w.length, String.format("Argument dimension mismatch: %d != %d", v.length, w.length));
    double prod = 0;
    for (int i = 0; i < v.length; i++) {
      prod += v[i] * w[i];
    }
    return prod;
  }

  // Normalize vector <x> into <multResult>
  // Also normalizes the signum of <x>, s.t. the first entry is always positive
  public static void normalize2(double[] vec) {
    double sig = Math.signum(vec[0]);
    double sum = 0;
    for (int i = 0; i < vec.length; i++) sum += vec[i] * vec[i];
    double norm = Math.sqrt(sum);
    for (int i = 0; i < vec.length; i++) vec[i] = sig * vec[i] / norm;
  }


}