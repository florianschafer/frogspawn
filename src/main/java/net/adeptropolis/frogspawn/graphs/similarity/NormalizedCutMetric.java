/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.similarity;

import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.traversal.ParallelEdgeOps;

import java.util.Arrays;

/**
 * Graph similarity metric using the normalized cut
 */

public class NormalizedCutMetric implements GraphSimilarityMetric {

  /**
   * Reduce multiple accumulators into a single instance by just adding values
   *
   * @param accumulators Array of accumulators
   * @return Single accumulator with summed up values
   */

  private static Accumulator reduce(Accumulator[] accumulators) {
    Accumulator acc = new Accumulator();
    for (int i = 0; i < ParallelEdgeOps.slices(); i++) {
      acc.subgraphWeights += accumulators[i].subgraphWeights;
      acc.complementWeights += accumulators[i].complementWeights;
      acc.cuts += accumulators[i].cuts;
    }
    return acc;
  }

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

    Accumulator acc = collectWeights(supergraph, subgraph);
    return computeNcut(acc) / 2;

  }

  /**
   * Create new accumulators
   *
   * @return Array of new accumulators
   */

  private Accumulator[] createAccumulators() {
    Accumulator[] accumulators = new Accumulator[ParallelEdgeOps.slices()];
    for (int i = 0; i < ParallelEdgeOps.slices(); i++) {
      accumulators[i] = new Accumulator();
    }
    return accumulators;
  }

  /**
   * Traverse the graph and accumulate weights
   *
   * @param supergraph A graph
   * @param subgraph   Subgraph
   * @return Collected weights
   */

  private Accumulator collectWeights(Graph supergraph, Graph subgraph) {
    boolean[] sub = subgraphMap(supergraph, subgraph);
    Accumulator[] accumulators = createAccumulators();
    supergraph.traverseParallel((u, v, weight) -> {
      Accumulator acc = accumulators[ParallelEdgeOps.slice(u)];
      if (!sub[u] && !sub[v]) {
        acc.complementWeights += weight;
      } else {
        if (sub[u] && sub[v]) {
          acc.subgraphWeights += weight;
        } else {
          acc.subgraphWeights += weight;
          acc.complementWeights += weight;
          acc.cuts += weight;
        }
      }
    });
    return reduce(accumulators);
  }

  /**
   * Provides a lookup table for graphs to quickly check whether a vertex
   * is contained in any of its subgraphs
   *
   * @param supergraph A graph
   * @param subgraph   Subgraph of <code>supergraph</code>
   * @return Boolean array with elements (local graph ids)
   * indicating whether it is also part of the subgraph
   */

  private boolean[] subgraphMap(Graph supergraph, Graph subgraph) {
    boolean[] subgraphLocalIds = new boolean[supergraph.order()];
    Arrays.fill(subgraphLocalIds, false);
    subgraph.traverseVerticesParallel(u ->
            subgraphLocalIds[supergraph.localVertexId(subgraph.globalVertexId(u))] = true);
    return subgraphLocalIds;
  }

  /**
   * Compute the normalized cut from collected weights
   *
   * @param agg Accumulator
   * @return Normalized cut for the given input
   */

  private double computeNcut(Accumulator agg) {

    double embeddedSubgraphWeight = agg.subgraphWeights;
    double embeddedComplementWeight = agg.complementWeights;
    double cut = agg.cuts;

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


  /**
   * Per-thread accumulator
   */

  private static class Accumulator {

    double subgraphWeights = 0;
    double complementWeights = 0;
    double cuts = 0;

  }

}
