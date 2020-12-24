/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.Labeling;

import java.io.Serializable;

/**
 * Provides a convenient builder for compressed sparse graphs from labels instead of integers.
 *
 * @param <V> Label type
 */

public class LabeledGraphBuilder<V extends Serializable> {

  private final Labeling<V> labeling;
  private final SparseGraphBuilder builder;

  /**
   * Constructor
   *
   * @param labeling Instance of a vertex labeling
   */

  public LabeledGraphBuilder(Labeling<V> labeling) {
    this.labeling = labeling;
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
    builder.add(labeling.id(left), labeling.id(right), weight);
    return this;
  }

  /**
   * Build the labeled graph
   *
   * @return New immutable LabeledGraph instance
   */

  public LabeledGraph<V> build() {
    SparseGraph graph = builder.build();
    return new LabeledGraph<>(graph, labeling);
  }

}
