/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import net.adeptropolis.metis.helpers.Arr;

/**
 * Helper class for sorting aligned lists of vertices, weights and consistency scores by weight
 */

class WeightSortOps implements IntComparator, Swapper {

  private final int[] vertices;
  private final double[] weights;
  private final double[] scores;

  /**
   * Constructor
   *
   * @param vertices Vertices
   * @param weights  Vertex weights
   * @param scores   Vertex consistency scores
   */

  WeightSortOps(int[] vertices, double[] weights, double[] scores) {
    this.vertices = vertices;
    this.weights = weights;
    this.scores = scores;
  }

  /**
   * Simultaneously swap array elements
   *
   * @param i First index
   * @param j Second index
   */

  @Override
  public void swap(int i, int j) {
    Arr.swap(vertices, i, j);
    Arr.swap(weights, i, j);
    Arr.swap(scores, i, j);
  }

  /**
   * Compare two indices by their associated weight
   *
   * @param i First index
   * @param j Second index
   * @return -1 if <code>weights[j] &lt; weights[i]</code>, 0 if equal, 1 otherwise
   */

  @Override
  public int compare(int i, int j) {
    return Double.compare(weights[j], weights[i]);
  }

}
