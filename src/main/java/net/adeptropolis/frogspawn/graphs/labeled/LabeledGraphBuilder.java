/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;

import java.io.Serializable;

/**
 * Provides a convenient builder for compressed sparse graphs from labels instead of integers.
 *
 * @param <V> Label type
 */

public class LabeledGraphBuilder<V extends Serializable> {

  private final Labelling<V> labelling;
  private final SparseGraphBuilder builder;

  /**
   * Constructor
   *
   * @param labelling Instance of a vertex labelling
   */

  public LabeledGraphBuilder(Labelling<V> labelling) {
    this.labelling = labelling;
    this.builder = new SparseGraphBuilder();
  }

  /**
   * Add a new undirected edge to the graph.
   *
   * @param left   left vertex label
   * @param right  right vertex label
   * @param weight edge weight
   * @return this
   */

  public synchronized LabeledGraphBuilder<V> add(V left, V right, double weight) {
    builder.add(labelling.index(left), labelling.index(right), weight);
    return this;
  }

  /**
   * Build the labeled graph
   *
   * @return New immutable LabeledGraph instance
   */

  public LabeledGraph<V> build() {
    SparseGraph graph = builder.build();
    return new LabeledGraph<>(graph, labelling);
  }

}
