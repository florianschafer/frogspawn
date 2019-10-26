package net.adeptropolis.nephila.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;

public interface Graph {

  int size();

  VertexIterator vertices();

  /**
   * @param v        A (local!) vertex
   * @param consumer
   */
  void traverse(int v, EdgeConsumer consumer);

  default void traverse(EdgeConsumer consumer) {
    EdgeOps.traverse(this, consumer);
  }

  int localVertexId(int globalVertexId);

  int globalVertexId(int localVertexId);

  Graph inducedSubgraph(IntIterator vertices);

  default double[] computeWeights() {
    return VertexWeights.compute(this);
  }

  interface Builder {

    Builder add(int u, int v, double weight);

    Graph build();
  }

}
