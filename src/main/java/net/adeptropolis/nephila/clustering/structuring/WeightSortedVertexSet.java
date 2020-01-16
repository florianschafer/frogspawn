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

import java.util.function.IntUnaryOperator;

public class WeightSortedVertexSet {

  private final int[] vertices;
  private final double[] weights;
  private final IntUnaryOperator mappingOp;

  public WeightSortedVertexSet(Graph graph, IntUnaryOperator mappingOp) {
    this.weights = new double[graph.size()];
    this.vertices = new int[graph.size()];
    this.mappingOp = mappingOp;
    sortVertices(graph);
  }

  public WeightSortedVertexSet(Graph graph) {
    this.weights = new double[graph.size()];
    this.vertices = new int[graph.size()];
    this.mappingOp = x -> x;
    sortVertices(graph);
  }

  private void sortVertices(Graph graph) {
    int[] vertices = new int[graph.size()];
    double[] weights = new double[graph.size()];
    for (VertexIterator it = graph.vertexIterator(); it.hasNext();) {
      vertices[it.localId()] = mappingOp.applyAsInt(it.globalId());
      weights[it.localId()] = graph.weights()[it.localId()];
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