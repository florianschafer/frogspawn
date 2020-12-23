/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Stream;

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

  private final SparseGraph graph;

  private final Labelling<V> labelling;

  /**
   * Constructor
   *
   * @param graph     A graph
   * @param labelling Instance of a vertex labelling
   */

  LabeledGraph(SparseGraph graph, Labelling<V> labelling) {
    this.graph = graph;
    this.labelling = labelling;
  }

  /**
   * @return The underlying graph
   */

  public SparseGraph getGraph() {
    return graph;
  }

  /**
   * @param vertexId Vertex id
   * @return The label for a particular vertex id
   */

  public V getLabel(int vertexId) {
    return labelling.label(vertexId);
  }

  /**
   * @return Labelling for this graph
   */

  public Labelling<V> getLabelling() {
    return labelling;
  }

  /**
   * @return Stream of all labels
   */

  public Stream<V> labels() {
    return labelling.labels();
  }

}
