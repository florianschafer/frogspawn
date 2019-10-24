package net.adeptropolis.nephila.graphs;

public interface VertexIterator {

  boolean proceed();

  int localId();

  int globalId();

}
