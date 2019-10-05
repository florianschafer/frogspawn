package net.adeptropolis.nephila.graph;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graph.backend.EdgeVisitor;

public interface Graph {

  int size();

  IntIterator vertices();

  void traverse(EdgeVisitor visitor);

  void traverseNeighbours(int v, EdgeVisitor visitor);

  void subgraph(int[] vertices);

}
