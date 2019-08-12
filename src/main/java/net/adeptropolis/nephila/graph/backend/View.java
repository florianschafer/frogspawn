package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.implementations.primitives.search.InterpolationSearch;

public class View {

  /*
   *  A View on an index subset of the given matrix
   */

  private CSRStorage csrStorage;
  final int[] indices;

  View(CSRStorage csrStorage, int[] indices) {
    this.csrStorage = csrStorage;
    this.indices = indices;
  }

  public View subview(int[] subviewIndices) {
    return csrStorage.view(subviewIndices);
  }

  public int size() {
    return indices.length;
  }

  public int get(int idx) {
    return indices[idx];
  }

  public int[] getIndices() {
    return indices;
  }

  public int getIndex(int val) {
    return InterpolationSearch.search(indices, val, 0, indices.length - 1);
  }

  public void traverse(final EdgeVisitor visitor) {
    csrStorage.traversal.traverse(visitor, this);
  }

  public void traverseIncidentEdges(final int u, final EdgeVisitor visitor) {
    if (indices.length == 0) return;
    int row = indices[u];
    long low = csrStorage.rowPtrs[row];
    long high = csrStorage.rowPtrs[row + 1];
    if (low == high) return;

    if (indices.length > high - low)
      traverseRowByEntries(u, visitor, low, high);
    else
      traverseRowByIndices(u, visitor, low, high);

  }

  private void traverseRowByEntries(final int rowIdx, final EdgeVisitor visitor, final long low, final long high) {
    int secPtr = 0;
    int colIdx;
    for (long ptr = low; ptr < high; ptr++) {
      colIdx = InterpolationSearch.search(indices, csrStorage.colIndices.get(ptr), secPtr, indices.length - 1);
      if (colIdx >= 0) {
        visitor.visit(rowIdx, colIdx, csrStorage.values.get(ptr));
        secPtr = colIdx + 1;
      }
      if (secPtr >= indices.length) break;
    }
  }

  private void traverseRowByIndices(final int rowIdx, final EdgeVisitor visitor, final long low, final long high) {
    long ptr = low;
    long retrievedIdx;
    for (int colIdx = 0; colIdx < indices.length; colIdx++) {
      retrievedIdx = InterpolationSearch.search(csrStorage.colIndices, indices[colIdx], ptr, high - 1);
      if (retrievedIdx >= 0 && retrievedIdx < high) {
        visitor.visit(rowIdx, colIdx, csrStorage.values.get(retrievedIdx));
        ptr = retrievedIdx + 1;
      }
      if (ptr >= high) break;
    }
  }

}
