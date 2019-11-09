package net.adeptropolis.nephila.helpers;

import com.google.common.base.Preconditions;

public class Vectors {

  public static double L1Norm(double[] v) {
    // This is NOT exactly the official L1 norm. Assuming ∀i ∈ [0, vec.length): vec[i] >= 0
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
  // Also normalizes the sign of <x>, s.t. the first entry is always positive
  public static void normalize2(double[] v) {
    double sig = v[0] != 0 ? Math.signum(v[0]) : 1;
    double sum = 0;
    for (double value : v) sum += value * value;
    double norm = Math.sqrt(sum);
    for (int i = 0; i < v.length; i++) v[i] = sig * v[i] / norm;
  }


}