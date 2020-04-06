/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import it.unimi.dsi.fastutil.Arrays;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.helpers.Arr;

/**
 * Digester using only the remainder of a cluster
 *
 * @see Digest
 */

public class TopWeightsRemainderClusterDigester implements ClusterDigester {

  private final ConsistencyMetric metric;
  private final int maxSize;

  /**
   * Constructor
   *
   * @param metric  Consistency metric to be used
   * @param maxSize Maximum number of vertices
   */

  public TopWeightsRemainderClusterDigester(ConsistencyMetric metric, int maxSize) {
    this.metric = metric;
    this.maxSize = maxSize;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Digest create(Cluster cluster) {
    Graph graph = cluster.remainderGraph();
    int[] vertices = graph.collectVertices();
    double[] weights = graph.weights();
    double[] consistencyScores = metric.compute(cluster.rootGraph(), graph);
    WeightSortOps altWeightSortOps = new WeightSortOps(vertices, weights, consistencyScores);
    Arrays.mergeSort(0, graph.order(), altWeightSortOps, altWeightSortOps);
    if (maxSize > 0) {
      return new Digest(
              Arr.shrink(vertices, maxSize),
              Arr.shrink(weights, maxSize),
              Arr.shrink(consistencyScores, maxSize),
              graph.order());
    } else {
      return new Digest(vertices, weights, consistencyScores, graph.order());
    }
  }

}
