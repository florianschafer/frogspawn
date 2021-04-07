/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.VertexIterator;
import net.adeptropolis.frogspawn.helpers.Arr;

/**
 * Digester using only the remainder of a cluster
 *
 * @see Digest
 */

public class ClusterDigester {

  private final AffiliationMetric metric;
  private final int maxSize;
  private final boolean aggregate;
  private final DigestRanking ranking;

  /**
   * Constructor
   *
   * @param settings Global clustering settings
   */

  public ClusterDigester(DigesterSettings settings) {
    this.metric = settings.getAffiliationMetric();
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
    double[] affiliationScores = metric.compute(cluster.rootGraph(), aggregateGraph);
    return finalizeDigest(vertices, weights, affiliationScores);
  }

  /**
   * Create a new digest, considering only a cluster's remainder.
   * <p>Note: Vertex weights and affiliation scores still refer to the aggregate graph of the cluster.</p>
   *
   * @param cluster Cluster from which to create the new digest
   * @return New digest instance.
   */

  private Digest remainderDigest(Cluster cluster) {
    Graph aggregateGraph = cluster.aggregateGraph();
    Graph remainderGraph = cluster.remainderGraph();
    int[] vertices = remainderGraph.collectVertices();
    double[] weights = restrictedWeights(aggregateGraph, remainderGraph);
    double[] affiliationScores = metric.compute(cluster.rootGraph(), aggregateGraph, remainderGraph);
    return finalizeDigest(vertices, weights, affiliationScores);
  }

  /**
   * Finalize cluster vertices and create a new digest: Sort + limit
   *
   * @param vertices          Digest vertices
   * @param weights           Digest weights
   * @param affiliationScores Digest vertex affiliation scores
   * @return New digest instance
   */

  private Digest finalizeDigest(int[] vertices, double[] weights, double[] affiliationScores) {
    MemberSortOps.sort(vertices, weights, affiliationScores, ranking);
    if (maxSize > 0) {
      return subsetDigest(vertices, weights, affiliationScores, vertices.length);
    }
    return new Digest(vertices, weights, affiliationScores, vertices.length);
  }

  /**
   * Return a new size-limited digest
   *
   * @param vertices          Cluster vertices
   * @param weights           Vertex weights
   * @param affiliationScores Vertex affiliation scores
   * @param totalSize         Total cluster size
   * @return New cluster digest
   */

  private Digest subsetDigest(int[] vertices, double[] weights, double[] affiliationScores, int totalSize) {
    return new Digest(
            Arr.shrink(vertices, maxSize),
            Arr.shrink(weights, maxSize),
            Arr.shrink(affiliationScores, maxSize),
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
