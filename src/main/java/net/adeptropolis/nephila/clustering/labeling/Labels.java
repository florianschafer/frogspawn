/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.clustering.labeling;

public class Labels {

  private final int[] vertices;
  private final double[] weights;
  private final double[] likelihoods;
  private final int aggregateSize;

  Labels(int[] vertices, double[] weights, double[] likelihoods, int aggregateSize) {
    this.vertices = vertices;
    this.weights = weights;
    this.likelihoods = likelihoods;
    this.aggregateSize = aggregateSize;
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

  public int size() {
    return vertices.length;
  }

  public int aggregateSize() {
    return aggregateSize;
  }
}
