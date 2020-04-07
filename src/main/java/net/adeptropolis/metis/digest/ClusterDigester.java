/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.helpers.Arr;

import java.util.function.Function;

/**
 * Digester using only the remainder of a cluster
 *
 * @see Digest
 */

public class ClusterDigester {

  public static final ClusterMemberComparator DESCENDING_WEIGHTS = (vertices, weights, scores, i, j) -> Double.compare(weights[j], weights[i]);
  public static final ClusterMemberComparator DESCENDING_SCORES = (vertices, weights, scores, i, j) -> Double.compare(scores[j], scores[i]);
  public static final Function<Double, ClusterMemberComparator> DESCENDING_COMBINED = weightExp -> (vertices, weights, scores, i, j) -> Double.compare(
          Math.pow(weights[j], weightExp) * scores[j],
          Math.pow(weights[i], weightExp) * scores[i]);

  private final ConsistencyMetric metric;
  private final int maxSize;
  private final boolean aggregate;
  private final ClusterMemberComparator comparator;

  /**
   * Constructor
   *
   * @param metric     Consistency metric to be used
   * @param maxSize    Maximum number of vertices
   * @param aggregate  <code>false</code> if only the cluster's remainder vertices should be considered,
   * @param comparator Indirect comparator for cluster member sorting
   */

  public ClusterDigester(ConsistencyMetric metric, int maxSize, boolean aggregate, ClusterMemberComparator comparator) {
    this.metric = metric;
    this.maxSize = maxSize;
    this.aggregate = aggregate;
    this.comparator = comparator;
  }

  /**
   * Create a cluster digest
   *
   * @param cluster Cluster from which to create a new digest
   * @return New cluster digest
   */

  public Digest create(Cluster cluster) {
    Graph graph = aggregate ? cluster.aggregateGraph() : cluster.remainderGraph();
    int[] vertices = graph.collectVertices();
    double[] weights = graph.weights();
    double[] consistencyScores = metric.compute(cluster.rootGraph(), graph);
    MemberSortOps.sort(vertices, weights, consistencyScores, comparator);
    if (maxSize > 0) {
      return subsetDigest(vertices, weights, consistencyScores, graph.order());
    }
    return new Digest(vertices, weights, consistencyScores, graph.order());
  }

  /**
   * Return a new size-limited digest
   *
   * @param vertices          Cluster vertices
   * @param weights           Vertex weights
   * @param consistencyScores Vertex consistency scores
   * @param totalSize         Total cluster size
   * @return New cluster digest
   */

  private Digest subsetDigest(int[] vertices, double[] weights, double[] consistencyScores, int totalSize) {
    return new Digest(
            Arr.shrink(vertices, maxSize),
            Arr.shrink(weights, maxSize),
            Arr.shrink(consistencyScores, maxSize),
            totalSize);
  }

}
