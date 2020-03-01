/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs;

import net.adeptropolis.metis.graphs.traversal.EdgeConsumer;
import net.adeptropolis.metis.graphs.traversal.ParallelEdgeOps;

public class VertexWeights implements EdgeConsumer {

  private final double[] weights;

  private VertexWeights(Graph graph) {
    this.weights = new double[graph.order()];
  }

  public static double[] compute(Graph graph) {
    VertexWeights weights = new VertexWeights(graph);
    ParallelEdgeOps.traverse(graph, weights);
    return weights.weights;
  }

  @Override
  public void accept(int u, int v, double weight) {
    weights[u] += weight;
  }

}
