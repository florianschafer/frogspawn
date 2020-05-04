/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.affiliation;

import net.adeptropolis.frogspawn.graphs.Graph;

/**
 * Vertex affiliation metric: Compute a score for how well a vertex fits into a cluster
 *
 * @see VertexAffiliationGuard
 */

public interface VertexAffiliationMetric {

  /**
   * Compute an affiliation score for all vertices of a subgraph with respect to one of its supergraphs
   *
   * @param supergraph A graph
   * @param subgraph   A subgraph of supergraph
   * @return Array of affiliation scores for all vertices of the subgraph
   */

  double[] compute(Graph supergraph, Graph subgraph);

  /**
   * Compute an affiliation score for all vertices of a subgraph with respect to one of its supergraphs,
   * but limit the computation to the vertices to a sub-subgraph
   *
   * @param supergraph  A graph
   * @param subgraph    A subgraph of supergraph
   * @param subsubgraph A subgraph of subgraph
   * @return Array of affiliation scores for all vertices of the subgraph, restricted to those of subsubgraph
   */

  double[] compute(Graph supergraph, Graph subgraph, Graph subsubgraph);

}
