/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.labeling;

public class Labels {

  private final int[] vertices;
  private final double[] weights;
  private final double[] likelihoods;

  public Labels(int[] vertices, double[] weights, double[] likelihoods) {
    this.vertices = vertices;
    this.weights = weights;
    this.likelihoods = likelihoods;
  }

  public int[] getVertices() {
    return vertices;
  }

  public double[] getWeights() {
    return weights;
  }

  public double[] getLikelihoods() {
    return likelihoods;
  }
}
