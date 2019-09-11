package net.adeptropolis.nephila.graph.backend;

// TODO: Make vertices an efficiently iterable + flushable sorted set?

import net.adeptropolis.nephila.graph.backend.arrays.BigDoubles;
import net.adeptropolis.nephila.graph.backend.arrays.BigInts;

public class CSRStorage {

  final ParallelEdgeTraversal traversal = new ParallelEdgeTraversal();

  private final int size;
  private final long edgeCount;

  final long[] vertexPtrs;
  final BigInts neighbours;
  final BigDoubles weights;

  CSRStorage(int size, long edgeCount, long[] vertexPtrs, BigInts neighbours, BigDoubles weights) {
    this.size = size;
    this.edgeCount = edgeCount;
    this.vertexPtrs = vertexPtrs;
    this.neighbours = neighbours;
    this.weights = weights;
  }

  public View defaultView() {
    int[] indices = new int[size];
    for (int i = 0; i < size; i++) indices[i] = i;
    return view(indices);
  }

  public View view(int[] indices) {
    return new View(this, indices);
  }

  public void free() {
    traversal.cleanup();
  }

  public int getSize() {
    return size;
  }

  public long getEdgeCount() {
    return edgeCount;
  }

  public long memoryFootprint() {
    return 16 + 4 + 8 + ((size + 1) << 3) + (edgeCount << 2) + (edgeCount << 3);
  }

}
