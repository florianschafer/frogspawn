/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.traversal.EdgeConsumer;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;

import java.io.Serializable;
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

  static final long serialVersionUID = 7802023986873566825L;

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
   * Sequential traversal over all edges of the graph
   *
   * @param consumer Instance of LabeledEdgeConsumer
   */

  public void traverse(LabeledEdgeConsumer<V> consumer) {
    graph.traverse(asEdgeConsumer(consumer));
  }

  /**
   * Parallel traversal over all edges of the graph
   *
   * @param consumer Instance of LabeledEdgeConsumer
   */

  // TODO: Test
  public void traverseParallel(LabeledEdgeConsumer<V> consumer) {
    graph.traverseParallel(asEdgeConsumer(consumer));
  }

  /**
   * Traverse over all adjacent vertices
   *
   * @param label Vertex label
   * @param consumer Instance of LabeledEdgeConsumer
   */

  public void traverse(V label, LabeledEdgeConsumer<V> consumer) {
    int localId = graph.localVertexId(labelling.index(label));
    if (localId < 0) {
      return;
    }
    graph.traverseIncidentEdges(localId, asEdgeConsumer(consumer), TraversalMode.DEFAULT);
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

  /**
   * Helper: wrap a labeledEdgeConsumer into an edge consumer
   *
   * @param consumer Labeled edge consumer
   * @return Edge consumer
   */

  private EdgeConsumer asEdgeConsumer(LabeledEdgeConsumer<V> consumer) {
    return (u, v, weight) -> consumer.accept(
            labelling.label(graph.globalVertexId(u)),
            labelling.label(graph.globalVertexId(v)),
            weight);
  }


}
