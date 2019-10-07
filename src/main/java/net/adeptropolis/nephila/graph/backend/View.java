package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.backend.arrays.InterpolationSearch;

// TODO: Remove calls to subview

public class View {

  /*
   *  A View on an index subset of the given matrix
   */

  private final int[] vertices;
  private CompressedSparseGraphDatastore graphDatastore;

  View(CompressedSparseGraphDatastore graphDatastore, int[] vertices) {
    this.graphDatastore = graphDatastore;
    this.vertices = vertices;
  }

  public View subview(int[] subviewIndices) {
    return graphDatastore.view(subviewIndices);
  }

  public int size() {
    return vertices.length;
  }

  public int[] getVertices() {
    return vertices;
  }

  public int getVertex(int idx) {
    return vertices[idx];
  }

  public int getIndex(int v) {
    return InterpolationSearch.search(vertices, v, 0, vertices.length - 1);
  }

  public void traverse(EdgeConsumer visitor) {
    new ParallelEdgeTraversal().traverse(visitor, this);
  }

  public void traverseAdjacent(int idx, EdgeConsumer visitor) {
    if (vertices.length == 0) {
      return;
    }
    int v = vertices[idx];
    long low = graphDatastore.pointers[v];
    long high = graphDatastore.pointers[v + 1];
    if (low == high) {
      return;
    }
    if (vertices.length > high - low) {
      traverseByAdjacent(idx, visitor, low, high);
    } else {
      traverseByIndices(idx, visitor, low, high);
    }
  }

  private void traverseByAdjacent(final int rowIdx, final EdgeConsumer visitor, final long low, final long high) {
    int secPtr = 0;
    int colIdx;
    for (long ptr = low; ptr < high; ptr++) {
      colIdx = InterpolationSearch.search(vertices, graphDatastore.edges.get(ptr), secPtr, vertices.length - 1);
      if (colIdx >= 0) {
        visitor.accept(rowIdx, colIdx, graphDatastore.weights.get(ptr));
        secPtr = colIdx + 1;
      }
      if (secPtr >= vertices.length) break;
    }
  }

  private void traverseByIndices(final int rowIdx, final EdgeConsumer visitor, final long low, final long high) {
    long ptr = low;
    long retrievedIdx;
    for (int i = 0; i < vertices.length; i++) {
      retrievedIdx = InterpolationSearch.search(graphDatastore.edges, vertices[i], ptr, high - 1);
      if (retrievedIdx >= 0 && retrievedIdx < high) {
        visitor.accept(rowIdx, i, graphDatastore.weights.get(retrievedIdx));
        ptr = retrievedIdx + 1;
      }
      if (ptr >= high) break;
    }
  }

}
