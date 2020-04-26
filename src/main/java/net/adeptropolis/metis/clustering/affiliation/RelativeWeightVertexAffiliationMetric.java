/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.affiliation;

import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.VertexIterator;

/**
 * Relative weight vertex affiliation metric
 */

public class RelativeWeightVertexAffiliationMetric implements VertexAffiliationMetric {

  /**
   * Compute a the relative weight affiliation score for all vertices of a subgraph with respect to one of its supergraphs
   * That is, return the fraction of subgraph vertex weights compared to those of the same vertex set embedded in the supergraph.
   *
   * @param supergraph A graph
   * @param subgraph   A subgraph of supergraph
   * @return Array of affiliation scores for all vertices of the subgraph
   */

  @Override
  public double[] compute(Graph supergraph, Graph subgraph) {
    double[] relWeights = new double[subgraph.order()];
    VertexIterator it = subgraph.vertexIterator();
    while (it.hasNext()) {
      double supergraphWeight = supergraph.weightForGlobalId(it.globalId());
      relWeights[it.localId()] = (supergraphWeight != 0) ? subgraph.weights()[it.localId()] / supergraphWeight : 0;
    }
    return relWeights;
  }

  /**
   * Compute a the relative weight affiliation score for all vertices of a subgraph with respect to one of its supergraphs,
   * restricted to a sub-subgraph of the supergraph.
   * That is, return the fraction of subgraph vertex weights compared to those of the same vertex set embedded in the supergraph.
   *
   * @param supergraph  A graph
   * @param subgraph    A subgraph of supergraph
   * @param subsubgraph A subgraph of subgraph
   * @return Array of affiliation scores for all vertices of the subgraph, restricted to the sub-subgraph
   */

  @Override
  public double[] compute(Graph supergraph, Graph subgraph, Graph subsubgraph) {
    double[] relWeights = new double[subsubgraph.order()];
    VertexIterator it = subsubgraph.vertexIterator();
    while (it.hasNext()) {
      double subgraphWeight = subgraph.weightForGlobalId(it.globalId());
      double supergraphWeight = supergraph.weightForGlobalId(it.globalId());
      relWeights[it.localId()] = (supergraphWeight != 0) ? subgraphWeight / supergraphWeight : 0;
    }
    return relWeights;
  }

  /**
   * @return Just the simple class name
   */

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }

}
