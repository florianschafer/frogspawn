package net.adeptropolis.nephila.graph;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Graph {

  IntStream vertices();

  Stream<Edge> edges();

  Stream<DoubleVertexProperty> outEdges(int vertexId);

  int degree(int vertexId);

}
