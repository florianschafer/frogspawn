package net.adeptropolis.nephila;

import net.adeptropolis.nephila.graph.Edge;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface GraphSource {

  IntStream vertices();

  int vertexCount();

  Stream<Edge> edges();

}
