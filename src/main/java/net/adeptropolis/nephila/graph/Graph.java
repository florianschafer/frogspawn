package net.adeptropolis.nephila.graph;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graph.backend.EdgeConsumer;
import net.adeptropolis.nephila.graph.backend.EdgeOps;
import net.adeptropolis.nephila.graph.backend.VertexIterator;

public interface Graph {

  int size();
  VertexIterator vertices();

  /**
   *
   * @param leftEndpoint A (local!) vertex
   * @param consumer
   */
  void traverse(int leftEndpoint, EdgeConsumer consumer);

  default void traverse(EdgeConsumer consumer) {
    EdgeOps.traverse(this, consumer);
  }

  int localVertexId(int globalVertexId);

  int globalVertexId(int localVertexId);

  Graph inducedSubgraph(IntIterator vertices);

  public interface Builder {

    Builder add(int u, int v, double weight);

    Graph build();
  }

}
