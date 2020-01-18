/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.labeling;

import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import net.adeptropolis.nephila.helpers.Arr;

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
