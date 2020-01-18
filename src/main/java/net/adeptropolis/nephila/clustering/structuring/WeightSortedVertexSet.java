/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.structuring;

import it.unimi.dsi.fastutil.Arrays;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;
import net.adeptropolis.nephila.helpers.Arr;

public class WeightSortedVertexSet {

  private final int[] vertices;
  private final double[] weights;

  public WeightSortedVertexSet(Graph graph) {
    this.weights = java.util.Arrays.copyOf(graph.weights(), graph.size());
    this.vertices = new int[graph.size()];
    sortVertices(graph);
  }

  private void sortVertices(Graph graph) {
    VertexIterator it = graph.vertexIterator();
    while (it.hasNext()) {
      vertices[it.localId()] = it.globalId();
    }
    Arrays.mergeSort(0, graph.size(), (a,b) -> Double.compare(this.weights[b], this.weights[a]), (a, b) -> {
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