/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import net.adeptropolis.nephila.helpers.Vectors;

/**
 * <p>Helper class that provides random initial vectors for the power iterations</p>
 */

public class RandomInitialVectors {

  private RandomInitialVectors() {
  }

  /**
   * Return a new vector <code>v</code> of a given size such that <code>||v|| = 1</code>
   *
   * @param size size of the new vector
   * @return the desired vector
   */

  public static double[] generate(int size) {
    double[] iv = new double[size];
    for (int i = 0; i < size; i++) {
      iv[i] = Math.random();
    }
    Vectors.normalize2Sig(iv);
    return iv;
  }

}
