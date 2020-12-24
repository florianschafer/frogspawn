/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.filters.GraphFilter;
import net.adeptropolis.frogspawn.graphs.traversal.EdgeConsumer;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;

import java.io.Serializable;
import java.util.stream.IntStream;
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

  static final long serialVersionUID = 7842023986873566825L;

  private final Graph graph;

  private final Labeling<V> labeling;

  /**
   * Constructor
   *
   * @param graph     A graph
   * @param labeling Instance of a vertex labeling
   */

  LabeledGraph(Graph graph, Labeling<V> labeling) {
    this.graph = graph;
    this.labeling = labeling;
  }

  /**
   * @return The underlying graph
   */

  public Graph getGraph() {
    return graph;
  }

  /**
   * Sequential traversal over all edges of the graph
   *
   * @param consumer Instance of LabeledEdgeConsumer
   */

  public void traverse(LabeledEdgeConsumer<V> consumer) {
    traverse(consumer, TraversalMode.DEFAULT);
  }

  /**
   * Sequential traversal over all edges of the graph
   *
   * @param consumer Instance of LabeledEdgeConsumer
   */

  public void traverse(LabeledEdgeConsumer<V> consumer, TraversalMode mode) {
    graph.traverse(asEdgeConsumer(consumer), mode);
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
   * @param label    Vertex label
   * @param consumer Instance of LabeledEdgeConsumer
   */

  public void traverse(V label, LabeledEdgeConsumer<V> consumer) {
    traverse(label, consumer, TraversalMode.DEFAULT);
  }

  /**
   * Traverse over all adjacent vertices
   *
   * @param label    Vertex label
   * @param consumer Instance of LabeledEdgeConsumer
   */

  public void traverse(V label, LabeledEdgeConsumer<V> consumer, TraversalMode mode) {
    int localId = graph.localVertexId(labeling.id(label));
    if (localId < 0) {
      return;
    }
    graph.traverseIncidentEdges(localId, asEdgeConsumer(consumer), mode);
  }


  /**
   * Create a labelled subgraph from a regular subgraph
   *
   * @param subgraph Subgraph
   * @return new labeled subgraph
   */

  public LabeledGraph<V> subgraph(Graph subgraph) {
    return new LabeledGraph<>(subgraph, labeling);
  }

  /**
   * Create a labelled subgraph from a stream of vertices
   *
   * @param vertices Vertices of the new subgraph
   * @return new labeled subgraph
   */

  public LabeledGraph<V> subgraph(Stream<V> vertices) {
    IntStream ids = vertices
            .mapToInt(labeling::id)
            .filter(id -> id >= 0);
    Graph subgraph = graph.subgraph(IntIterators.asIntIterator(ids.iterator()));
    return new LabeledGraph<>(subgraph, labeling);
  }

  /**
   * @param vertexId Vertex id
   * @return The label for a particular vertex id
   */

  public V getLabel(int vertexId) {
    return labeling.label(vertexId);
  }

  /**
   * @return Labeling for this graph
   */

  public Labeling<V> getLabeling() {
    return labeling;
  }

  /**
   * @return Stream of all labels
   */

  public Stream<V> labels() {
    return labeling.labels();
  }

  /**
   * @return Order of the graph
   */

  public int order() {
    return graph.order();
  }

  /**
   * @return Size of the graph
   */

  public long size() {
    return graph.size();
  }

  /**
   * Apply a filter to this graph
   *
   * @param filter Instance of GraphFilter
   * @param applyIteratively Whether the filter should be applied just once or iteratively
   * @return Filtered graph
   */

  public LabeledGraph<V> filter(GraphFilter filter, boolean applyIteratively) {
    if (applyIteratively) {
      return new LabeledGraph<>(filter.applyIteratively(graph), labeling);
    } else {
      return new LabeledGraph<>(filter.apply(graph), labeling);
    }
  }

  /**
   * Collapse a subgraph into a minimal sparse graph
   *
   * @param collapsedLabeling A fresh labeling instance
   * @return New, collapsed graph
   */

  public LabeledGraph<V> collapse(Labeling<V> collapsedLabeling) { // TODO: Change name, document, test
    LabeledGraphBuilder<V> builder = new LabeledGraphBuilder<>(collapsedLabeling);
    traverse(builder::add);
    return builder.build();
  }

  /**
   * Helper: wrap a labeledEdgeConsumer into an edge consumer
   *
   * @param consumer Labeled edge consumer
   * @return Edge consumer
   */

  private EdgeConsumer asEdgeConsumer(LabeledEdgeConsumer<V> consumer) {
    return (u, v, weight) -> consumer.accept(
            labeling.label(graph.globalVertexId(u)),
            labeling.label(graph.globalVertexId(v)),
            weight);
  }


}
