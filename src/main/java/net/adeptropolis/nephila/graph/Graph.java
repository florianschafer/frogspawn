package net.adeptropolis.nephila.graph;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graph.backend.EdgeVisitor;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public interface Graph {

  int size();

  IntStream vertices();

  void traverse(EdgeVisitor visitor);

  void traverseNeighbours(int v, EdgeVisitor visitor);

}
