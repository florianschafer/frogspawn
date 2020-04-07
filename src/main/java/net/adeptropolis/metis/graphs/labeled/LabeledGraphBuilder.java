/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.labeled;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;

import java.lang.reflect.Array;

/**
 * Provides a convenient builder for compressed sparse graphs from labels instead of integers.
 *
 * @param <V> Label type
 */

public class LabeledGraphBuilder<V> {

  private final Object2IntOpenHashMap<V> vertexMap;
  private final CompressedSparseGraphBuilder builder;
  private final Class<V> labelClass;

  /**
   * Constructor
   *
   * @param labelClass Label class
   */

  public LabeledGraphBuilder(Class<V> labelClass) {
    this.labelClass = labelClass;
    this.vertexMap = new Object2IntOpenHashMap<>();
    this.builder = new CompressedSparseGraphBuilder();
  }

  /**
   * Add a new undirected edge to the graph.
   *
   * @param left   left vertex label
   * @param right  right vertex label
   * @param weight edge weight
   * @return this
   */

  public LabeledGraphBuilder<V> add(V left, V right, double weight) {
    builder.add(vertexId(left), vertexId(right), weight);
    return this;
  }

  /**
   * Build the labeled graph
   *
   * @return A new immutable LabeledGraph instance
   */

  public LabeledGraph<V> build() {
    CompressedSparseGraph graph = builder.build();
    V[] labels = invertLabels();
    return new LabeledGraph<>(graph, labels);
  }

  /**
   * @return Mapping between vertex ids and labels
   */

  private V[] invertLabels() {
    @SuppressWarnings("unchecked")
    V[] map = (V[]) Array.newInstance(labelClass, vertexMap.size());
    vertexMap.forEach((label, id) -> map[id] = label);
    return map;
  }

  /**
   * @param label A label
   * @return Unique vertex id for this label
   */

  private int vertexId(V label) {
    return vertexMap.computeIntIfAbsent(label, x -> vertexMap.size());
  }


}
