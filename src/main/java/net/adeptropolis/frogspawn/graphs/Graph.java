/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.frogspawn.graphs.filters.GraphFilter;
import net.adeptropolis.frogspawn.graphs.traversal.*;

import java.util.function.IntPredicate;

/**
 * <p>A weighted graph supporting all basic operations required in this context, i.e.</p>
 * <ul>
 *   <li>Very fast edge traversal over either the full graph or the set of adjacent vertices wrt. a given endpoint</li>
 *   <li>Efficient creation of induced subgraphs</li>
 *   <li>Fast computation of various properties and metrics</li>
 * </ul>
 */

public abstract class Graph {

  private double[] cachedWeights = null;
  private long[] cachedDegrees = null;
  private double cachedTotalWeight = -1;

  /**
   * <p>Size of the Graph</p>
   * <p><b>Note:</b> The edges of undirected graphs are accounted for in a directed fashion, i.e.
   * for such graphs, the return value of this method is exactly twice the number of edges.</p>
   *
   * @return Total number of edges
   */

  public abstract long size();

  /**
   * Iterator over the full vertex set
   *
   * @return An iterator over the vertex set
   */

  public abstract VertexIterator vertexIterator();

  /**
   * Retrieve the vertex set
   *
   * @return Array of all global vertex ids
   */

  public abstract int[] collectVertices();

  /**
   * Create an iterator over all global vertex ids of this graph
   *
   * @return Iterator over all global vertex ids
   */

  public abstract IntIterator globalVertexIdIterator();

  /**
   * Sequential traversal over all edges of the graph
   *
   * @param consumer Instance of EdgeConsumer
   */

  public void traverse(EdgeConsumer consumer) {
    this.traverse(consumer, TraversalMode.DEFAULT);
  }

  /**
   * Sequential traversal over all edges of the graph
   *
   * @param consumer Instance of EdgeConsumer
   */

  public void traverse(EdgeConsumer consumer, TraversalMode mode) {
    for (int i = 0; i < order(); i++) {
      traverseIncidentEdges(i, consumer, mode);
    }
  }

  /**
   * Graph order
   *
   * @return The number of vertices in the graph
   */

  public abstract int order();

  /**
   * Traverse all edges adjacent to a given endpoint
   *
   * @param v        (Local!) vertex id of the endpoint
   * @param consumer Instance of EdgeConsumer
   * @param mode     Traversal mode
   */

  public abstract void traverseIncidentEdges(int v, EdgeConsumer consumer, TraversalMode mode);

  /**
   * Parallel traversal over all edges of the graph
   *
   * @param consumer Instance of EdgeConsumer
   */

  public void traverseParallel(EdgeConsumer consumer) {
    traverseParallel(consumer, TraversalMode.DEFAULT);
  }

  /**
   * Parallel traversal over all edges of the graph using a given traversal mode
   *
   * @param consumer Instance of EdgeConsumer
   * @param mode     Traversal mode
   */

  public void traverseParallel(EdgeConsumer consumer, TraversalMode mode) {
    ParallelEdgeOps.traverse(this, consumer, mode);
  }

  /**
   * Parallel traversal over all vertices of the graph
   *
   * @param consumer Instance of VertexConsumer
   */

  public void traverseVerticesParallel(VertexConsumer consumer) {
    ParallelVertexOps.traverse(this, consumer);
  }

  /**
   * Translate between local and global vertex ids
   *
   * @param localVertexId A local vertex id
   * @return Global vertex id
   */

  public abstract int globalVertexId(int localVertexId);

  /**
   * Check whether a graph contains a given vertex
   *
   * @param globalVertexId A global vertex id
   * @return true if this graph contains the given vertex, otherwise false
   */

  public boolean containsVertex(int globalVertexId) {
    return localVertexId(globalVertexId) >= 0;
  }

  /**
   * Translate between global and local vertex ids
   *
   * @param globalVertexId A global vertex id
   * @return Local vertex id
   */

  public abstract int localVertexId(int globalVertexId);

  /**
   * <p>Compute the induced subgraph from a local vertex id predicate</p>
   *
   * @param predicate Predicate on local(!) vertex ids of the graph
   * @return a new subgraph
   */

  public Graph subgraph(IntPredicate predicate) {
    return subgraph(new PredicateVertexIterator(this, predicate));
  }

  /**
   * <p>Compute the induced subgraph from a given set of global vertex ids</p>
   *
   * @param vertices The vertex set of the new subgraph
   * @return a new subgraph
   */

  public abstract Graph subgraph(IntIterator vertices);

  /**
   * <p>Compute the induced subgraph from a given set of local vertex ids</p>
   *
   * @param vertices The (local) vertex set of the new subgraph
   * @return a new subgraph
   */

  public Graph localSubgraph(IntIterator vertices) {
    return subgraph(new VertexMappingIterator(vertices));
  }

  /**
   * Apply a filter to this graph
   *
   * @param filter           Instance of GraphFilter
   * @param applyIteratively Whether the filter should be applied just once or iteratively
   * @return Filtered graph
   */

  public Graph filter(GraphFilter filter, boolean applyIteratively) {
    if (applyIteratively) {
      return filter.applyIteratively(this);
    } else {
      return filter.apply(this);
    }
  }

  /**
   * @param globalVertexId global vertex id
   * @return Weight for this id
   */

  public double weightForGlobalId(int globalVertexId) {
    return weights()[localVertexId(globalVertexId)];
  }

  /**
   * @return The vertex weights of the graph.
   */

  public double[] weights() {
    if (cachedWeights == null) {
      cachedWeights = VertexWeights.compute(this);
    }
    return cachedWeights;
  }

  /**
   * @param globalVertexId global vertex id
   * @return Degree for this id
   */

  public long degreeForGlobalId(int globalVertexId) {
    return degrees()[localVertexId(globalVertexId)];
  }

  /**
   * @return The vertex degrees of the graph.
   */

  public long[] degrees() {
    if (cachedDegrees == null) {
      cachedDegrees = VertexDegrees.compute(this);
    }
    return cachedDegrees;
  }

  /**
   * The total weight of the graph. <b>Note:</b> For undirected graphs, weights are counted twice!
   *
   * @return The total weight of the graph
   */

  public double totalWeight() {
    if (cachedTotalWeight >= 0) {
      return cachedTotalWeight;
    }
    double[] weights = weights();
    double total = 0;
    for (int i = 0; i < order(); i++) {
      total += weights[i];
    }
    cachedTotalWeight = total;
    return total;
  }

  public interface Builder {

    Builder add(int u, int v, double weight);

    Builder addDirected(int u, int v, double weight);

    Graph build();
  }

  /**
   * Iterator providing a mapping between local and global vertex ids
   */

  class VertexMappingIterator implements IntIterator {

    private final IntIterator localIds;

    VertexMappingIterator(IntIterator localIds) {
      this.localIds = localIds;
    }

    @Override
    public int nextInt() {
      return globalVertexId(localIds.nextInt());
    }

    @Override
    public boolean hasNext() {
      return localIds.hasNext();
    }
  }

}
