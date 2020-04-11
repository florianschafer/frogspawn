/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.consistency;

import net.adeptropolis.metis.graphs.Graph;

/**
 * Consistency metric
 *
 * @see ConsistencyGuard
 */

public interface ConsistencyMetric {

  /**
   * Compute a consistency score for all vertices of a subgraph with respect to one of its supergraphs
   *
   * @param supergraph A graph
   * @param subgraph   A subgraph of supergraph
   * @return Array of consistency scores for all vertices of the subgraph
   */

  double[] compute(Graph supergraph, Graph subgraph);

  /**
   * Compute a consistency score for all vertices of a subgraph with respect to one of its supergraphs,
   * but limit the computation to the vertices to a sub-subgraph
   *
   * @param supergraph  A graph
   * @param subgraph    A subgraph of supergraph
   * @param subsubgraph A subgraph of subgraph
   * @return Array of consistency scores for all vertices of the subgraph, restricted to those of subsubgraph
   */

  double[] compute(Graph supergraph, Graph subgraph, Graph subsubgraph);

}
