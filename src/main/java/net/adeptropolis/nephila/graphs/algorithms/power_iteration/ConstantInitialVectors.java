package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import java.util.Arrays;

public class ConstantInitialVectors {

  public static double[] generate(int size) {
    double[] iv = new double[size];
    Arrays.fill(iv, 1.0 / Math.sqrt(size));
    return iv;
  }

}
