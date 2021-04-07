/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import net.adeptropolis.frogspawn.graphs.traversal.EdgeConsumer;

/**
 * Compute the vertex weights of a graph
 */

public class VertexDegrees implements EdgeConsumer {

  private final long[] degrees;

  /**
   * Constructor
   *
   * @param graph Graph whose vertex degrees should be computed
   */

  private VertexDegrees(Graph graph) {
    this.degrees = new long[graph.order()];
  }

  /**
   * Convenience method for computing the vertex degrees of a graph
   *
   * @param graph A graph
   * @return Array containing the vertex degrees of the graph
   */

  public static long[] compute(Graph graph) {
    VertexDegrees degrees = new VertexDegrees(graph);
    graph.traverseParallel(degrees);
    return degrees.degrees;
  }

  /**
   * EdgeConsumer callback
   *
   * @param u      Left endpoint
   * @param v      Right endpoint
   * @param weight Edge weight
   */

  @Override
  public void accept(int u, int v, double weight) {
    degrees[u]++;
  }

}
