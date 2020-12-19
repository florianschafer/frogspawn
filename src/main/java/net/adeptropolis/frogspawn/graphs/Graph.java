/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.frogspawn.graphs.traversal.*;

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
  private double cachedTotalWeight = -1;

  /**
   * Graph order
   *
   * @return The number of vertices in the graph
   */

  public abstract int order();

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
   * Traverse all edges adjacent to a given endpoint
   *
   * @param v        (Local!) vertex id of the endpoint
   * @param consumer Instance of EdgeConsumer
   * @param mode     Traversal mode
   */

  public abstract void traverseIncidentEdges(int v, EdgeConsumer consumer, TraversalMode mode);

  /**
   * Sequential traversal over all edges of the graph
   *
   * @param consumer Instance of EdgeConsumer
   */

  // TODO: Test
  public void traverse(EdgeConsumer consumer, TraversalMode mode) {
    for (int i = 0; i < order(); i++) {
      traverseIncidentEdges(i, consumer, mode);
    }
  }

  /**
   * Sequential traversal over all edges of the graph
   *
   * @param consumer Instance of EdgeConsumer
   */

  // TODO: Test
  public void traverse(EdgeConsumer consumer) {
    this.traverse(consumer, TraversalMode.DEFAULT);
  }

  /**
   * Sequential traversal over all edges of the graph using global vertex ids
   *
   * @param consumer Instance of EdgeConsumer
   */

  // TODO: Test
  public void traverseGlobal(EdgeConsumer consumer, TraversalMode mode) {
    this.traverse( (u, v, weight) -> consumer.accept(globalVertexId(u), globalVertexId(v), weight), mode);
  }

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
   * Translate between global and local vertex ids
   *
   * @param globalVertexId A global vertex id
   * @return Local vertex id
   */

  public abstract int localVertexId(int globalVertexId);

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
   * @return Weight for this id
   */

  public double weightForGlobalId(int globalVertexId) {
    return weights()[localVertexId(globalVertexId)];
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
