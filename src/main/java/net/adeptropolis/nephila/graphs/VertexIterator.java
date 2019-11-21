package net.adeptropolis.nephila.graphs;

public interface VertexIterator {

  boolean hasNext();

  int localId();

  int globalId();

}
