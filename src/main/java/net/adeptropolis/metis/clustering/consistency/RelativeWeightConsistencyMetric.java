/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.consistency;

import net.adeptropolis.metis.graphs.Graph;

/**
 * Relative weight consistency metric
 */

public class RelativeWeightConsistencyMetric implements ConsistencyMetric {

  /**
   * Compute a the relative weight consistency score for all vertices of a subgraph with respect to one of its supergraphs
   * That is, return the fraction of subgraph vertex weights compared to those of the same vertex set embedded in the supergraph.
   *
   * @param graph    A graph
   * @param subgraph A subgraph
   * @return Array of consistency scores for all vertices of the subgraph
   */

  @Override
  public double[] compute(Graph graph, Graph subgraph) {
    return subgraph.relativeWeights(graph);
  }

}
