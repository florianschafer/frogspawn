/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.algorithms.MinDegreeFilter;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;

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

  private final SparseGraph graph;
  private final V[] labels;
  private Object2IntOpenHashMap<V> inverseLabelsCache;

  /**
   * Constructor
   *  @param graph  A graph
   * @param labels Array of labels, indexed by vertex id
   */

  LabeledGraph(SparseGraph graph, V[] labels) {
    this.graph = graph;
    this.labels = labels;
    this.inverseLabelsCache = null;
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
    return labels[vertexId];
  }

  /**
   * @return All vertex-label mappings
   */

  public V[] getLabels() {
    return labels;
  }

  /**
   * Traverse all edges of the graph using the vertex labels
   *
   * @param consumer Instance of LabeledEdgeConsumer
   */

  public void traverse(LabeledEdgeConsumer<V> consumer) {
    traverse(consumer, TraversalMode.DEFAULT);
  }

  /**
   * Traverse all edges adjacent to a given endpoint
   *
   * @param label        Endpoint label
   * @param consumer Instance of LabeledEdgeConsumer
   * @param mode     Traversal mode
   */

  // TODO: Test
  public void traverseIncidentEdges(V label, LabeledEdgeConsumer<V> consumer, TraversalMode mode) {
    int i = inverseLabels().getOrDefault(label, -1);
    if (i < 0) {
      return;
    }
    graph.traverseIncidentEdges(graph.localVertexId(i), (u,v,weight) -> {
      consumer.accept(labels[graph.globalVertexId(u)], labels[graph.globalVertexId(v)], weight);
    }, mode);
  }

  /**
   * Traverse all edges of the graph using the vertex labels
   *
   * @param consumer Instance of LabeledEdgeConsumer
   * @param mode Traversal mode
   */

  public void traverse(LabeledEdgeConsumer<V> consumer, TraversalMode mode) {
    for (int i = 0 ; i < graph.order(); i++) {
      graph.traverseIncidentEdges(i, (v, w, weight) -> consumer.accept(labels[graph.globalVertexId(v)], labels[graph.globalVertexId(w)], weight), mode);
    }
  }

  // TODO: Test, comment
  // NOTE: Creates new instance
  public LabeledGraph<V> minDegreeFilter(int minDegree, Class<V> labelsClass) {
    Graph filtered = MinDegreeFilter.apply(graph, minDegree);
    LabeledGraphBuilder<V> builder = new LabeledGraphBuilder<>(labelsClass);
    filtered.traverse((u, v, weight) -> builder.add(labels[filtered.globalVertexId(u)], labels[filtered.globalVertexId(v)], weight), TraversalMode.LOWER_TRIANGULAR);
    return builder.build();
  }

  /**
   * Note: This method creates a new map on every invocation!
   *
   * @return A new map label -> vertex id
   */

  // TODO: Test
  public Object2IntOpenHashMap<V> inverseLabels() {
    if (inverseLabelsCache != null) {
      return inverseLabelsCache;
    }
    Object2IntOpenHashMap<V> map = new Object2IntOpenHashMap<V>();
    for (int v = 0; v < labels.length; v++) {
      map.put(labels[v], v);
    }
    inverseLabelsCache = map;
    return map;

  }

}
