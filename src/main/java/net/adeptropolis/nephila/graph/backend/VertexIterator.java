package net.adeptropolis.nephila.graph.backend;

public interface VertexIterator {

  boolean proceed();

  int localId();

  int globalId();

}
