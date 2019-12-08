/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;

/**
 * Ensures the consistency of a new subgraph. That is, given a parent cluster and a potential subgraph,
 * vertices from the subgraph are shifted from the subgraph to the parent cluster's remainder bucket until
 * all remaining satisfy the required minimum likelihood.
 */

public class ConsistencyGuard {

  private final Graph graph;
  private final int minClusterSize;
  private final double minClusterLikelihood;

  /**
   * Constructor
   *
   * @param graph                Root graph
   * @param minClusterSize       Minimum cluster (graph) size
   * @param minClusterLikelihood Minimum cluster (graph) likelihood
   */

  public ConsistencyGuard(Graph graph, int minClusterSize, double minClusterLikelihood) {
    this.graph = graph;
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
  }

  /**
   * Produces a subgraph where all vertices are guaranteed to be self-consistent.
   *
   * @param parentCluster An existing cluster that the new graph should be assigned to as subcluster
   * @param candidate     The subcluster graph candidate
   * @return Either a new subgraph with all vertices guaranteed to exhibit >= minClusterLikelihood or null if that graph would be smaller than the allowed min size
   */

  public Graph ensure(Cluster parentCluster, Graph candidate) {
    IntRBTreeSet survivors = initSurvivors(candidate);
    for (Graph subgraph = candidate; true; subgraph = inducedSubgraph(survivors)) {
      int prevSize = survivors.size();
      shiftInconsistentVertices(subgraph, parentCluster, survivors);
      if (survivors.size() < minClusterSize) {
        parentCluster.addToRemainder(survivors.iterator());
        return null;
      } else if (survivors.size() == prevSize) {
        return subgraph;
      }
    }
  }

  /**
   * Move all inconcistent vertices of a subgraph to the parent's remainder
   *
   * @param subgraph      The subgraph candidate
   * @param parentCluster Parent cluster
   * @param survivors     Set of vertices that are considered to be part of the subgraph
   */

  private void shiftInconsistentVertices(Graph subgraph, Cluster parentCluster, IntRBTreeSet survivors) {
    double[] likelihoods = subgraph.relativeWeights(graph);
    VertexIterator it = subgraph.vertexIterator();
    while (it.hasNext()) {
      if (likelihoods[it.localId()] < minClusterLikelihood) {
        parentCluster.addToRemainder(it.globalId());
        survivors.remove(it.globalId());
      }
    }
  }

  /**
   * Initialize survivors set with all vertices of a subgraph candidate
   *
   * @param candidate The subgraph candidate
   * @return The full set of candidate vertices
   */

  private IntRBTreeSet initSurvivors(Graph candidate) {
    IntRBTreeSet remainingVertices = new IntRBTreeSet();
    VertexIterator vertexIt = candidate.vertexIterator();
    while (vertexIt.hasNext()) {
      remainingVertices.add(vertexIt.globalId());
    }
    return remainingVertices;
  }

  private Graph inducedSubgraph(IntRBTreeSet survivors) {
    return graph.inducedSubgraph(survivors.iterator());
  }

}
