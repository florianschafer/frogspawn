/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

@Deprecated
public class ClusterMetrics {

  private final int[] sortedVertices;
  private final double[] intraClusterWeights;
  private final double[] clusterLikelihood;

  public ClusterMetrics(int[] sortedVertices, double[] intraClusterWeights, double[] clusterLikelihood) {
    this.sortedVertices = sortedVertices;
    this.intraClusterWeights = intraClusterWeights;
    this.clusterLikelihood = clusterLikelihood;
  }

  public int[] getSortedVertices() {
    return sortedVertices;
  }

  public double[] getIntraClusterWeights() {
    return intraClusterWeights;
  }

}
