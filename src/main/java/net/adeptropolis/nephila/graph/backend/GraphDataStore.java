package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.backend.arrays.BigDoubles;
import net.adeptropolis.nephila.graph.backend.arrays.BigInts;

public class GraphDataStore {

  final long[] pointers;
  final BigInts neighbours;
  final BigDoubles weights;
  private final int size;
  private final long edgeCount;

  GraphDataStore(int size, long edgeCount, long[] pointers, BigInts neighbours, BigDoubles weights) {
    this.size = size;
    this.edgeCount = edgeCount;
    this.pointers = pointers;
    this.neighbours = neighbours;
    this.weights = weights;
  }

  public View defaultView() {
    int[] indices = new int[size];
    for (int i = 0; i < size; i++) {
      indices[i] = i;
    }
    return view(indices);
  }

  public View view(int[] indices) {
    return new View(this, indices);
  }

  public int getSize() {
    return size;
  }

  public long getEdgeCount() {
    return edgeCount;
  }

}
