/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms.power_iteration;

import com.google.common.annotations.VisibleForTesting;
import net.adeptropolis.metis.helpers.Vectors;

import java.util.Random;

/**
 * <p>Helper class that provides random initial vectors for the power iterations</p>
 */

public class RandomInitialVectors {


  private static final int SEED = 42133742;
  private static Random random = new Random(SEED);

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
      iv[i] = random.nextDouble();
    }
    Vectors.normalize2Sig(iv);
    return iv;
  }

  /**
   * Reset <code>random</code> to the original seed. Only used in <code>RecursiveClusteringTest</code>
   * to check determinism.
   */

  @VisibleForTesting
  public static void resetRandom() {
    random.setSeed(SEED);
  }

}
