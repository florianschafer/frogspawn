/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;


public abstract class Graph {

  private double[] cachedWeights = null;
  private double cachedTotalWeight = -1;

  public abstract int size();

  public abstract long numEdges();

  public abstract VertexIterator vertexIterator();

  public abstract int[] collectVertices();

  /**
   * @param v        A (local!) vertex
   * @param consumer an instance of EdgeConsumer
   */

  public abstract void traverseParallel(int v, EdgeConsumer consumer);

  public void traverseParallel(EdgeConsumer consumer) {
    ParallelEdgeOps.traverse(this, consumer);
  }

  /**
   * Traverse in a sequential manner (i.e. no multithreading)
   *
   * @param consumer
   */

  public void traverseEdgesSequential(EdgeConsumer consumer) {
    for (int u = 0; u < size(); u++) {
      traverseParallel(u, consumer);
    }
  }

  public void traverseVerticesParallel(VertexConsumer consumer) {
    ParallelVertexOps.traverse(this, consumer);
  }

  public abstract int localVertexId(int globalVertexId);

  public abstract int globalVertexId(int localVertexId);

  /**
   * <p></p>Compute the induced subgraph from the given global vertices</p>
   *
   * @param vertices The vertex set of the new subgraph
   * @return a new subgraph
   */

  public abstract Graph inducedSubgraph(IntIterator vertices);

  /**
   * <p></p>Compute the induced subgraph from the given local vertices</p>
   *
   * @param vertices The vertex set (as local vertex ids) of the new subgraph
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
    for (int i = 0; i < size(); i++) {
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
    double[] relWeights = new double[size()];
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
    for (int i = 0; i < size(); i++) {
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
   * Iterator providing a mapping between internal and external vertex ids
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
