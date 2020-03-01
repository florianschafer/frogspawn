/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.labeling;

import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import net.adeptropolis.metis.helpers.Arr;

class WeightSortOps implements IntComparator, Swapper {

  private final int[] vertices;
  private final double[] weights;
  private final double[] likelihoods;

  WeightSortOps(int[] vertices, double[] weights, double[] likelihoods) {
    this.vertices = vertices;
    this.weights = weights;
    this.likelihoods = likelihoods;
  }

  @Override
  public void swap(int i, int j) {
    Arr.swap(vertices, i, j);
    Arr.swap(weights, i, j);
    Arr.swap(likelihoods, i, j);
  }

  @Override
  public int compare(int i, int j) {
    return Double.compare(weights[j], weights[i]);
  }

}
