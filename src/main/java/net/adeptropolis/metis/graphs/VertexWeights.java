/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs;

import net.adeptropolis.metis.graphs.traversal.EdgeConsumer;
import net.adeptropolis.metis.graphs.traversal.ParallelEdgeOps;

/**
 * Compute the vertex weights of a graph
 */

public class VertexWeights implements EdgeConsumer {

  private final double[] weights;

  /**
   * Constructor
   *
   * @param graph Graph whose vertex weights should be computed
   */

  private VertexWeights(Graph graph) {
    this.weights = new double[graph.order()];
  }

  /**
   * Convenience method for computing the vertex weights of a graph
   *
   * @param graph A graph
   * @return Array containing the vertex weights of the graph
   */

  public static double[] compute(Graph graph) {
    VertexWeights weights = new VertexWeights(graph);
    ParallelEdgeOps.traverse(graph, weights);
    return weights.weights;
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
    weights[u] += weight;
  }

}
