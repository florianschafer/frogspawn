/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import net.adeptropolis.frogspawn.graphs.implementations.CompressedSparseGraph;

import java.io.Serializable;

/**
 * A labeled graph.
 * <p>
 * More precisely, this is merely a wrapper object for regular sparse graphs
 * together with a mapping from int vertex indices to labels.
 * </p>
 *
 * @param <V> Label type
 */

public class LabeledGraph<V extends Serializable> implements Serializable {

  static final long serialVersionUID = 7802023886873266825L;

  private final CompressedSparseGraph graph;
  private final V[] labels;

  /**
   * Constructor
   *
   * @param graph  A graph
   * @param labels Array of labels, indexed by vertex id
   */

  LabeledGraph(CompressedSparseGraph graph, V[] labels) {
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
   * @param vertexId Vertex id
   * @return The label for a particular vertex id
   */

  public V getLabel(int vertexId) {
    return labels[vertexId];
  }

  /**
   * @return All vertex-label mappings
   */

  public V[] getLabels() {
    return labels;
  }
}
