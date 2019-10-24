package net.adeptropolis.nephila;

import net.adeptropolis.nephila.graphs.Edge;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface GraphSource {

  IntStream vertices();

  int vertexCount();

  Stream<Edge> edges();

}
