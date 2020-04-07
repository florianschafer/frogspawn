/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.implementations;

/**
 * A labeled graph.
 * <p>
 * More precisely, this is merely a wrapper object for regular sparse graphs
 * together with a mapping from int vertex indices to labels.
 * </p>
 *
 * @param <V>
 */

public class LabeledGraph<V> {

  private final CompressedSparseGraph graph;
  private final V[] labels;

  /**
   * Constructor
   *
   * @param graph  A graph
   * @param labels Array of labels, indexed by vertex id
   */

  public LabeledGraph(CompressedSparseGraph graph, V[] labels) {
    this.graph = graph;
    this.labels = labels;
  }

  /**
   * @return The underlying graph
   */

  public CompressedSparseGraph getGraph() {
    return graph;
  }

  /**
   * @return The label for a particular vertex id
   */

  public V getLabel(int vertexId) {
    return labels[vertexId];
  }

}
