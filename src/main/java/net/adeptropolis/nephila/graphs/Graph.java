/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.traversal.EdgeConsumer;
import net.adeptropolis.nephila.graphs.traversal.ParallelEdgeOps;
import net.adeptropolis.nephila.graphs.traversal.ParallelVertexOps;
import net.adeptropolis.nephila.graphs.traversal.VertexConsumer;

/**
 * <p>A weighted graph supporting all basic operations required in this context, i.e.</p>
 * <ul>
 *   <li>Very fast edge traversal over either the full graph or the set of adjacent vertices wrt. a given endpoint</li>
 *   <li>Efficient creation of induced subgraphs</li>
 *   <li>Fast computation of various properties and metrics such as vertex weights, relative weights (wrt. a supergraph) or edge overlap</li>
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
   * Parallel traversal over all edges adjacent to a given endpoint
   *
   * @param v        (Local!) vertex id of the endpoint
   * @param consumer Instance of EdgeConsumer
   */

  public abstract void traverseParallel(int v, EdgeConsumer consumer);

  /**
   * Parallel traversal over all edges of the graph
   *
   * @param consumer Instance of EdgeConsumer
   */

  public void traverseParallel(EdgeConsumer consumer) {
    ParallelEdgeOps.traverse(this, consumer);
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
   * <p>Compute the induced subgraph from a given set of global vertex ids</p>
   *
   * @param vertices The vertex set of the new subgraph
   * @return a new subgraph
   */

  public abstract Graph inducedSubgraph(IntIterator vertices);

  /**
   * <p>Compute the induced subgraph from a given set of local vertex ids</p>
   *
   * @param vertices The (local) vertex set of the new subgraph
   * @return a new subgraph
   */

  public Graph localInducedSubgraph(IntIterator vertices) {
    return inducedSubgraph(new VertexMappingIterator(vertices));
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

  /**
   * Return the fractional weights of a subgraph relative to its supergraph
   * <p><b>Note: The subgraph <b>must be fully contained</b> within the supergraph!</b></p>
   *
   * @param supergraph The supergraph
   * @return The array of relative weights
   */

  public double[] relativeWeights(Graph supergraph) {
    double[] relWeights = new double[order()];
    VertexIterator it = vertexIterator();
    while (it.hasNext()) {
      int v = supergraph.localVertexId(it.globalId());
      assert v >= 0;
      double supergraphWeight = supergraph.weights()[v];
      relWeights[it.localId()] = (supergraphWeight != 0) ? weights()[it.localId()] / supergraphWeight : 0;
    }
    return relWeights;
  }

  /**
   * Return the fractional total weight of a subgraph relative to its supergraph
   * <p><b>Note: The subgraph <b>must be fully contained</b> within the supergraph!</b></p>
   *
   * @param supergraph The supergraph
   * @return relative overlap
   */

  public double overlap(Graph supergraph) {
    double weight = 0;
    double supergraphEmbeddingWeight = 0;
    for (int i = 0; i < order(); i++) {
      weight += weights()[i];
      supergraphEmbeddingWeight += supergraph.weights()[supergraph.localVertexId(globalVertexId(i))];
    }
    return (supergraphEmbeddingWeight > 0) ? weight / supergraphEmbeddingWeight : 0;
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
