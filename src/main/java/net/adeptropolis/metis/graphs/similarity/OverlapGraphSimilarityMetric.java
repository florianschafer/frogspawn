/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.similarity;

import net.adeptropolis.metis.graphs.Graph;

public class OverlapGraphSimilarityMetric implements GraphSimilarityMetric {

  /**
   * Return the fractional total weight of a subgraph relative to its supergraph
   * <p><b>Note: The subgraph <b>must be fully contained</b> within the supergraph!</b></p>
   *
   * @param subgraph   A subgraph
   * @param supergraph Supergraph of graph
   * @return relative overlap
   */

  @Override
  public double compute(Graph supergraph, Graph subgraph) {
    double weight = 0;
    double supergraphEmbeddingWeight = 0;
    for (int i = 0; i < subgraph.order(); i++) {
      weight += subgraph.weights()[i];
      supergraphEmbeddingWeight += supergraph.weightForGlobalId(subgraph.globalVertexId(i));
    }
    return (supergraphEmbeddingWeight > 0) ? weight / supergraphEmbeddingWeight : 0;
  }
}
