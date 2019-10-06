package net.adeptropolis.nephila.graph;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graph.backend.EdgeConsumer;
import net.adeptropolis.nephila.graph.backend.VertexIterator;

public interface Graph {

  int size();
  VertexIterator vertices();
  void traverse(EdgeConsumer visitor);
  void traverseByGlobalId(int leftEndpoint, EdgeConsumer consumer);
  int localVertexId(int globalVertexId);
  int globalVertexId(int localVertexId);
  Graph inducedSubgraph(IntIterator vertices);

  default void traverseByLocalId(int v, EdgeConsumer consumer) {
    traverseByGlobalId(globalVertexId(v), consumer);
  }

}
