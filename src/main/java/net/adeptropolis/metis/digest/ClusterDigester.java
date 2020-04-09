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

  public static final ClusterMemberRanking WEIGHT_RANKING = (vertexId, weight, score) -> weight;
  public static final ClusterMemberRanking SCORE_RANKING = (vertexId, weight, score) -> score;
  public static final Function<Double, ClusterMemberRanking> COMBINED_RANKING
          = weightExp -> (vertexId, weight, score) -> Math.pow(weight, weightExp) * score;

  private final ConsistencyMetric metric;
  private final int maxSize;
  private final boolean aggregate;
  private final ClusterMemberRanking comparator;

  /**
   * Constructor
   *
   * @param metric     Consistency metric to be used
   * @param maxSize    Maximum number of vertices
   * @param aggregate  <code>false</code> if only the cluster's remainder vertices should be considered,
   * @param comparator Indirect comparator for cluster member sorting
   */

  public ClusterDigester(ConsistencyMetric metric, int maxSize, boolean aggregate, ClusterMemberRanking comparator) {
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

  public Digest digest(Cluster cluster) {
    Graph graph = aggregate ? cluster.aggregateGraph() : cluster.remainderGraph();
    int[] vertices = graph.collectVertices();
    double[] weights = graph.weights();
    double[] consistencyScores = metric.compute(cluster.rootGraph(), graph);
    MemberSortOps.sort(vertices, weights, consistencyScores, comparator);
    if (maxSize > 0) {
      return subsetDigest(vertices, weights, consistencyScores, vertices.length, cluster.depth());
    }
    return new Digest(vertices, weights, consistencyScores, vertices.length, cluster.depth());
  }

  /**
   * Return a new size-limited digest
   *
   * @param vertices          Cluster vertices
   * @param weights           Vertex weights
   * @param consistencyScores Vertex consistency scores
   * @param totalSize         Total cluster size
   * @param depth             Cluster depth within the hierarchy
   * @return New cluster digest
   */

  private Digest subsetDigest(int[] vertices, double[] weights, double[] consistencyScores, int totalSize, int depth) {
    return new Digest(
            Arr.shrink(vertices, maxSize),
            Arr.shrink(weights, maxSize),
            Arr.shrink(consistencyScores, maxSize),
            totalSize, depth);
  }

}
