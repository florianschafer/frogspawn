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

@FunctionalInterface
public interface ConsistencyMetric {

  /**
   * Compute a consistency score for all vertices of a subgraph with respect to one of its supergraphs
   *
   * @param graph    A graph
   * @param subgraph A subgraph
   * @return Array of consistency scores for all vertices of the subgraph
   */

  double[] compute(Graph graph, Graph subgraph);

}
