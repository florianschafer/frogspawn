/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import net.adeptropolis.metis.ClusteringSettings;
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

  public static final DigestRanking WEIGHT_RANKING = (vertexId, weight, score) -> weight;
  public static final DigestRanking SCORE_RANKING = (vertexId, weight, score) -> score;
  public static final Function<Double, DigestRanking> COMBINED_RANKING
          = weightExp -> (vertexId, weight, score) -> Math.pow(weight, weightExp) * score;

  private final ConsistencyMetric metric;
  private final int maxSize;
  private final boolean aggregate;
  private final DigestRanking ranking;

  /**
   * Constructor
   *
   * @param settings Global clustering settings
   */

  public ClusterDigester(ClusteringSettings settings) {
    this.metric = settings.getConsistencyMetric();
    this.maxSize = settings.getMaxDigestSize();
    this.aggregate = settings.isAggregateDigests();
    this.ranking = settings.getDigestRanking();
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
    MemberSortOps.sort(vertices, weights, consistencyScores, ranking);
    if (maxSize > 0) {
      return subsetDigest(vertices, weights, consistencyScores, vertices.length);
    }
    return new Digest(vertices, weights, consistencyScores, vertices.length);
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
