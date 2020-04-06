/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.consistency;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.VertexIterator;

/**
 * Ensures the consistency of a new subgraph. That is, given a parent cluster and a potential subgraph,
 * vertices from the subgraph are shifted from the subgraph to the parent cluster's remainder bucket until
 * all remaining satisfy the required minimum consistency criterion. Since the removal if vertices may affect the consistency
 * of remaining ones, the process is repeated until all satisfy the minimum consistency criterion.
 */

public class ConsistencyGuard {

  private final ConsistencyMetric metric;
  private final Graph graph;
  private final int minClusterSize;
  private final double minConsistency;

  /**
   * Constructor
   *
   * @param metric         The consistency metric to be used
   * @param graph          Root graph
   * @param minClusterSize Minimum cluster (graph) size
   * @param minConsistency Minimum vertex consistency score
   */

  public ConsistencyGuard(ConsistencyMetric metric, Graph graph, int minClusterSize, double minConsistency) {
    this.metric = metric;
    this.graph = graph;
    this.minClusterSize = minClusterSize;
    this.minConsistency = minConsistency;
  }

  /**
   * Produces a subgraph where all vertices are guaranteed to be self-consistent.
   *
   * @param parentCluster An existing cluster that the new graph should be assigned to as subcluster
   * @param candidate     The subcluster graph candidate
   * @return Either a new subgraph with all vertices guaranteed to <code>exhibit ≥ minConsistency</code> or <code>null</code>
   * if that graph would be smaller than the allowed min size
   */

  public Graph ensure(Cluster parentCluster, Graph candidate) {
    IntRBTreeSet survivors = new IntRBTreeSet(candidate.globalVertexIdIterator());
    for (Graph subgraph = candidate; true; subgraph = graph.inducedSubgraph(survivors.iterator())) {
      int prevSize = survivors.size();
      shiftInconsistentVertices(subgraph, parentCluster, survivors);
      if (survivors.size() < minClusterSize) {
        parentCluster.addToRemainder(survivors.iterator());
        return null;
      } else if (survivors.size() == minClusterSize || survivors.size() == prevSize) {
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
    double[] metrics = metric.compute(graph, subgraph);
    VertexIterator it = subgraph.vertexIterator();
    while (it.hasNext()) {
      if (metrics[it.localId()] < minConsistency) {
        parentCluster.addToRemainder(it.globalId());
        survivors.remove(it.globalId());
      }
    }
  }

}
