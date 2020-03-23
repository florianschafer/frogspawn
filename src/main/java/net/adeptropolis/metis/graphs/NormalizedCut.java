/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import static net.adeptropolis.metis.graphs.traversal.ParallelOps.THREAD_POOL_SIZE;

/**
 * Provides the normalized cut between a graph and one of its supergraphs
 */

public class NormalizedCut {

  /**
   * Private default constructor
   */

  private NormalizedCut() {
  }

  /**
   * Compute the normalized cut between a graph and one of its supergraphs
   * <p><b>NOTE:</b> This implementation strictly requires the above subgraph-supergraph relationship. Otherwise, the result is undefined!</p>
   *
   * @param graph      Any graph
   * @param supergraph Any (non-trivial) supergraph
   * @return Normalized cut between the two graphs
   */

  // TODO: This method of traversal counts all edges twice. This doesn't change results, but is unnecessary!
  public static double compute(Graph graph, Graph supergraph) {

    // TODO: Check whether it would make much of a difference not to create this set here and instead just use Graph::containsVertex (triggering 2 searches/edge)
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    IntOpenHashSet subgraphVertices = new IntOpenHashSet(graph.globalVertexIdIterator());

    ThreadLocalAccumulator[] threadLocalAccumulators = createThreadLocalAccumulators();
    supergraph.traverseParallel((u, v, weight) -> {

      // NOTE: Only do these kinds of accumulator tricks if you really know what you're doing
      ThreadLocalAccumulator accumulator = threadLocalAccumulators[u % THREAD_POOL_SIZE];

      boolean uMemberOfSubgraph = subgraphVertices.contains(supergraph.globalVertexId(u));
      boolean vMemberOfSubgraph = subgraphVertices.contains(supergraph.globalVertexId(v));

      if (uMemberOfSubgraph && vMemberOfSubgraph) {
        accumulator.graphSupWeight += weight;
      } else if (uMemberOfSubgraph || vMemberOfSubgraph) {
        accumulator.graphSupWeight += weight;
        accumulator.complementSupWeight += weight;
        accumulator.boundaryWeight += weight;
      } else {
        accumulator.complementSupWeight += weight;
      }
    });

    return computeNcutFromAccumulators(threadLocalAccumulators);

  }

  /**
   * @return A fresh set of accumulators
   */

  private static ThreadLocalAccumulator[] createThreadLocalAccumulators() {
    ThreadLocalAccumulator[] threadLocalAccumulators = new ThreadLocalAccumulator[THREAD_POOL_SIZE];
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      threadLocalAccumulators[i] = new ThreadLocalAccumulator();
    }
    return threadLocalAccumulators;
  }

  /**
   * Reduce all accumulator values and compute the normalized cut
   *
   * @param threadLocalAccumulators Accumulators
   * @return Normalized cut
   */

  private static double computeNcutFromAccumulators(ThreadLocalAccumulator[] threadLocalAccumulators) {

    double graphSupWeight = 0d;
    double complementSupWeight = 0d;
    double boundaryWeight = 0d;

    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      ThreadLocalAccumulator acc = threadLocalAccumulators[i];
      graphSupWeight += acc.graphSupWeight;
      complementSupWeight += acc.complementSupWeight;
      boundaryWeight += acc.boundaryWeight;
    }

    if (graphSupWeight > 0 && complementSupWeight > 0) {
      return boundaryWeight / graphSupWeight + boundaryWeight / complementSupWeight;
    }

    return 0;

  }


  /**
   * Simple helper class for accumulating weights in a thread-local fashion
   */

  private static class ThreadLocalAccumulator {

    private double graphSupWeight = 0d;
    private double complementSupWeight = 0d;
    private double boundaryWeight = 0d;

  }

}
