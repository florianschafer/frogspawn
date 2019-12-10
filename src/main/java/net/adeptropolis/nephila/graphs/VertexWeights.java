/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs;

public class VertexWeights implements EdgeConsumer {

  private final double[] weights;

  private VertexWeights(Graph graph) {
    this.weights = new double[graph.size()];
  }

  public static double[] compute(Graph graph) {
    VertexWeights weights = new VertexWeights(graph);
    EdgeOps.traverse(graph, weights);
    return weights.weights;
  }

  @Override
  public void accept(int u, int v, double weight) {
    weights[u] += weight;
  }

}
