/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.similarity;

import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.helpers.Arr;

import java.util.Arrays;

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

    double[] embeddedSubgraphWeights = new double[subgraph.order()];
    double[] embeddedComplementWeights = new double[supergraph.order()];
    double[] cuts = new double[subgraph.order()];
    int[] idMap = mapLocalIds(supergraph, subgraph);

    supergraph.traverseParallel((u, v, weight) -> {
      if (idMap[u] < 0 && idMap[v] < 0) {
        embeddedComplementWeights[u] += weight / 2;
      } else {
        if (idMap[u] >= 0 && idMap[v] >= 0) {
          embeddedSubgraphWeights[idMap[u]] += weight / 2;
        } else if (idMap[u] >= 0) {
          embeddedSubgraphWeights[idMap[u]] += weight;
          embeddedComplementWeights[u] += weight;
          cuts[idMap[u]] += weight;
        }
      }
    });

    return computeNcut(embeddedSubgraphWeights, embeddedComplementWeights, cuts) / 2;

  }

  /**
   * Provides a mapping between local vertex ids from the supergraph and those of the subgraph
   * <p>
   *   This mapping is provided through a supergraph local id-indexed array of
   *   local subgraph ids. An element may be <code>-1</code> in case that vertex
   *   is not contained in the subgraph.
   * </p>
   *
   * @param supergraph A graph
   * @param subgraph Subgraph of <code>supergraph</code>
   * @return Array of local subgraph ids
   */

  private int[] mapLocalIds(Graph supergraph, Graph subgraph) {
    int[] subgraphLocalIds = new int[supergraph.order()];
    Arrays.fill(subgraphLocalIds, -1);
    subgraph.traverseVerticesParallel( u -> subgraphLocalIds[
            supergraph.localVertexId(subgraph.globalVertexId(u))] = u );
    return subgraphLocalIds;
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
