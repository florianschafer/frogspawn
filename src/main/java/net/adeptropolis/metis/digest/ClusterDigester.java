/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.VertexIterator;
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
    return aggregate ? aggregateDigest(cluster) : remainderDigest(cluster);
  }

  /**
   * Create a new digest, considering a cluster's aggregate graph.
   *
   * @param cluster Cluster from which to create the new digest
   * @return New digest instance.
   */

  private Digest aggregateDigest(Cluster cluster) {
    Graph aggregateGraph = cluster.aggregateGraph();
    int[] vertices = aggregateGraph.collectVertices();
    double[] weights = aggregateGraph.weights();
    double[] consistencyScores = metric.compute(cluster.rootGraph(), aggregateGraph);
    return finalizeDigest(vertices, weights, consistencyScores);
  }

  /**
   * Create a new digest, considering only a cluster's remainder.
   * <p>Note: Vertex weights and consistency scores still refer to the aggregate graph of the cluster.</p>
   *
   * @param cluster Cluster from which to create the new digest
   * @return New digest instance.
   */

  private Digest remainderDigest(Cluster cluster) {
    Graph aggregateGraph = cluster.aggregateGraph();
    Graph remainderGraph = cluster.remainderGraph();
    int[] vertices = remainderGraph.collectVertices();
    double[] weights = restrictedWeights(aggregateGraph, remainderGraph);
    double[] consistencyScores = metric.compute(cluster.rootGraph(), aggregateGraph, remainderGraph);
    return finalizeDigest(vertices, weights, consistencyScores);
  }

  /**
   * Finalize cluster vertices and create a new digest: Sort + limit
   *
   * @param vertices          Digest vertices
   * @param weights           Digest weights
   * @param consistencyScores Digest consistency scores
   * @return New digest instance
   */

  private Digest finalizeDigest(int[] vertices, double[] weights, double[] consistencyScores) {
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

  /**
   * Restrict weights of an aggregate graph to a remainder-only graph
   *
   * @param aggregateGraph Aggregate graph of a cluster
   * @param remainderGraph Remainder graph of the same cluster
   * @return Aggregate graph weights, restricted to the remainder graph
   */

  private double[] restrictedWeights(Graph aggregateGraph, Graph remainderGraph) {
    double[] weights = new double[remainderGraph.order()];
    VertexIterator it = remainderGraph.vertexIterator();
    while (it.hasNext()) {
      weights[it.localId()] = aggregateGraph.weightForGlobalId(it.globalId());
    }
    return weights;
  }

}
