package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.backend.arrays.InterpolationSearch;

// TODO: Remove calls to subview

public class View {

  /*
   *  A View on an index subset of the given matrix
   */

  private CSRStorage csrStorage;
  private final int[] vertices;

  View(CSRStorage csrStorage, int[] vertices) {
    this.csrStorage = csrStorage;
    this.vertices = vertices;
  }

  public View subview(int[] subviewIndices) {
    return csrStorage.view(subviewIndices);
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

  public int getIndex(int val) {
    return InterpolationSearch.search(vertices, val, 0, vertices.length - 1);
  }

  public void traverse(final EdgeVisitor visitor) {
    csrStorage.traversal.traverse(visitor, this);
  }

  public void traverseAdjacent(final int idx, final EdgeVisitor visitor) {
    if (vertices.length == 0) return;
    int v = vertices[idx];
    long low = csrStorage.vertexPtrs[v];
    long high = csrStorage.vertexPtrs[v + 1];
    if (low == high) return;
    if (vertices.length > high - low) traverseByAdjacent(idx, visitor, low, high);
    else traverseByIndices(idx, visitor, low, high);
  }

  private void traverseByAdjacent(final int rowIdx, final EdgeVisitor visitor, final long low, final long high) {
    int secPtr = 0;
    int colIdx;
    for (long ptr = low; ptr < high; ptr++) {
      colIdx = InterpolationSearch.search(vertices, csrStorage.neighbours.get(ptr), secPtr, vertices.length - 1);
      if (colIdx >= 0) {
        visitor.visit(rowIdx, colIdx, csrStorage.weights.get(ptr));
        secPtr = colIdx + 1;
      }
      if (secPtr >= vertices.length) break;
    }
  }

  private void traverseByIndices(final int rowIdx, final EdgeVisitor visitor, final long low, final long high) {
    long ptr = low;
    long retrievedIdx;
    for (int colIdx = 0; colIdx < vertices.length; colIdx++) {
      retrievedIdx = InterpolationSearch.search(csrStorage.neighbours, vertices[colIdx], ptr, high - 1);
      if (retrievedIdx >= 0 && retrievedIdx < high) {
        visitor.visit(rowIdx, colIdx, csrStorage.weights.get(retrievedIdx));
        ptr = retrievedIdx + 1;
      }
      if (ptr >= high) break;
    }
  }

}
