/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.consistency;

import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.VertexIterator;

/**
 * Relative weight consistency metric
 */

public class RelativeWeightConsistencyMetric implements ConsistencyMetric {

  /**
   * Compute a the relative weight consistency score for all vertices of a subgraph with respect to one of its supergraphs
   * That is, return the fraction of subgraph vertex weights compared to those of the same vertex set embedded in the supergraph.
   *
   * @param supergraph A graph
   * @param subgraph   A subgraph of supergraph
   * @return Array of consistency scores for all vertices of the subgraph
   */

  @Override
  public double[] compute(Graph supergraph, Graph subgraph) {
    double[] relWeights = new double[subgraph.order()];
    VertexIterator it = subgraph.vertexIterator();
    while (it.hasNext()) {
      int v = supergraph.localVertexId(it.globalId());
      assert v >= 0;
      double supergraphWeight = supergraph.weights()[v];
      relWeights[it.localId()] = (supergraphWeight != 0) ? subgraph.weights()[it.localId()] / supergraphWeight : 0;
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
