/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import it.unimi.dsi.fastutil.Arrays;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.helpers.Arr;

/**
 * Digester using all aggregated remainder vertices from a cluster and its full subtree
 *
 * @see Digest
 */

public class TopWeightsAggregateClusterDigester implements ClusterDigester {

  private final ConsistencyMetric metric;
  private final int maxSize;

  /**
   * Constructor
   *
   * @param metric  Consistency metric to be used
   * @param maxSize Maxumum number of vertices
   */

  public TopWeightsAggregateClusterDigester(ConsistencyMetric metric, int maxSize) {
    this.metric = metric;
    this.maxSize = maxSize;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Digest create(Cluster cluster) {
    Graph graph = cluster.aggregateGraph();
    int[] vertices = graph.collectVertices();
    double[] weights = graph.weights();
    double[] consistencyScores = metric.compute(cluster.rootGraph(), graph);
    WeightSortOps weightSortOps = new WeightSortOps(vertices, weights, consistencyScores);
    Arrays.mergeSort(0, graph.order(), weightSortOps, weightSortOps);
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
