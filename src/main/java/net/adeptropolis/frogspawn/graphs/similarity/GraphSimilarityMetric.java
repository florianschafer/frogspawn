/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.similarity;

import net.adeptropolis.frogspawn.graphs.Graph;

@FunctionalInterface
public interface GraphSimilarityMetric {

  /**
   * Compute a similarity score for a subgraph with respect to one of its supergraphs
   *
   * @param supergraph A graph
   * @param subgraph   A subgraph of supergraph
   * @return Similarity between both graphs
   */

  double compute(Graph supergraph, Graph subgraph);

}
