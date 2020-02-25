/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs;

import it.unimi.dsi.fastutil.Arrays;
import net.adeptropolis.nephila.helpers.Arr;

public class WeightSortedVertexSet {

  private final int[] vertices;
  private final double[] weights;

  public WeightSortedVertexSet(Graph graph) {
    this.weights = java.util.Arrays.copyOf(graph.weights(), graph.order());
    this.vertices = new int[graph.order()];
    sortVertices(graph);
  }

  private void sortVertices(Graph graph) {
    VertexIterator it = graph.vertexIterator();
    while (it.hasNext()) {
      vertices[it.localId()] = it.globalId();
    }
    Arrays.mergeSort(0, graph.order(), (a, b) -> Double.compare(this.weights[b], this.weights[a]), (a, b) -> {
      Arr.swap(vertices, a, b);
      Arr.swap(weights, a, b);
    });
  }

  public int size() {
    return vertices.length;
  }

  public int[] getVertices() {
    return vertices;
  }

  public double[] getWeights() {
    return weights;
  }

}