/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.similarity;

import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.helpers.Arr;

/**
 * Graph similarity metric using the normalized cut
 */

public class NormalizedCutMetric implements GraphSimilarityMetric {

  /**
   * Computes (half) the normalized cut between a graph and one of its subgraphs.
   *
   * @param supergraph A graph
   * @param subgraph   A subgraph of supergraph
   * @return Normalized cut between the graph and its subgraph / 2
   */

  @Override
  public double compute(Graph supergraph, Graph subgraph) {

    if (supergraph.order() == 0 || subgraph.order() == 0) {
      return 0d;
    }

    // TODO: This goes a long way just to avoid high-congestion atomics. Revisit/benchmark!
    double[] embeddedSubgraphWeights = new double[subgraph.order()];
    double[] embeddedComplementWeights = new double[supergraph.order()];
    double[] cuts = new double[subgraph.order()];

    supergraph.traverseParallel((u, v, weight) -> {
      int uGlobalId = supergraph.globalVertexId(u);
      boolean subgraphContainsU = subgraph.containsVertex(uGlobalId);
      boolean subgraphContainsV = subgraph.containsVertex(supergraph.globalVertexId(v));
      if (!subgraphContainsU && !subgraphContainsV) {
        embeddedComplementWeights[u] += weight / 2;
      } else {
        int subgraphLocalId = subgraph.localVertexId(uGlobalId);
        if (subgraphContainsU && subgraphContainsV) {
          embeddedSubgraphWeights[subgraphLocalId] += weight / 2;
        } else if (subgraphContainsU) {
          embeddedSubgraphWeights[subgraphLocalId] += weight;
          embeddedComplementWeights[u] += weight;
          cuts[subgraphLocalId] += weight;
        }
      }
    });

    return computeNcut(embeddedSubgraphWeights, embeddedComplementWeights, cuts) / 2;

  }

  private double computeNcut(double[] embeddedSubgraphWeights, double[] embeddedComplementWeights, double[] cuts) {

    double embeddedSubgraphWeight = Arr.sum(embeddedSubgraphWeights);
    double embeddedComplementWeight = Arr.sum(embeddedComplementWeights);
    double cut = Arr.sum(cuts);
    if (embeddedSubgraphWeight > 0 && embeddedComplementWeight > 0) {
      return cut / embeddedSubgraphWeight + cut / embeddedComplementWeight;
    } else if (embeddedSubgraphWeight > 0) {
      return cut / embeddedSubgraphWeight;
    } else if (embeddedComplementWeight > 0) {
      return cut / embeddedComplementWeight;
    }
    return 0;

  }

  /**
   * @return Just the simple name
   */

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
