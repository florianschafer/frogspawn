package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import java.util.Arrays;

/**
 * <p>Helper class that provides constant-value initial vectors for the power iterations</p>
 */

public class ConstantInitialVectors {

  /**
   * Return a new vector <code>v</code> of a given size such that <code>||v|| = 1</code>
   *
   * @param size
   * @return
   */

  public static double[] generate(int size) {
    double[] iv = new double[size];
    Arrays.fill(iv, 1.0 / Math.sqrt(size));
    return iv;
  }

}
