package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.primitives.Doubles;
import net.adeptropolis.nephila.graph.implementations.primitives.Ints;
import net.adeptropolis.nephila.graph.implementations.primitives.search.InterpolationSearch;

public class CSRStorage {

  private final CSRTraversal traversal = new CSRTraversal();

  private final int numRows;
  private final long nnz;

  private final long[] rowPtrs;
  private final Ints colIndices;
  private final Doubles values;

  CSRStorage(int numRows, long nnz, long[] rowPtrs, Ints colIndices, Doubles values) {
    this.numRows = numRows;
    this.nnz = nnz;
    this.rowPtrs = rowPtrs;
    this.colIndices = colIndices;
    this.values = values;
  }

  public View defaultView() {
    int[] indices = new int[numRows];
    for (int i = 0; i < numRows; i++) indices[i] = i;
    return view(indices);
  }

  public View view(int[] indices) {
    return new View(indices);
  }

  public void free() {
    colIndices.free();
    values.free();
    traversal.cleanup();
  }

  // TODO: Remove all those getters!

  public int getNumRows() {
    return numRows;
  }

  public long getNnz() {
    return nnz;
  }

  long[] getRowPtrs() {
    return rowPtrs;
  }

  Ints getColIndices() {
    return colIndices;
  }

  Doubles getValues() {
    return values;
  }

  public String fmtMemoryFootprint() {
    long fp = memoryFootprint();
    if (fp >= (1 << 30)) {
      return String.format("%.2f GB", (double) fp / (1 << 30));
    } else if (fp >= (1 << 20)) {
      return String.format("%.2f MB", (double) fp / (1 << 20));
    } else if (fp >= (1 << 10)) {
      return String.format("%.2f KB", (double) fp / (1 << 10));
    } else {
      return String.format("%d bytes", fp);
    }
  }

  public long memoryFootprint() {
    return ((numRows + 1) << 3) + (nnz << 2) + (nnz << 3);
  }

  public class View {

    /*
     *  A View on an index subset of the given matrix
     */

    final int[] indices;

    View(int[] indices) {
      this.indices = indices;
    }

    public View subview(int[] subviewIndices) {
      return new View(subviewIndices);
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

    public void traverse(final EntryVisitor visitor) {
      traversal.traverse(visitor, this);
    }

    public void traverseRow(final int rowIdx, final EntryVisitor visitor) {
      if (indices.length == 0) return;
      int row = indices[rowIdx];
      long low = rowPtrs[row];
      long high = rowPtrs[row + 1];
      if (low == high) return;

      if (indices.length > high - low)
        traverseRowByEntries(rowIdx, visitor, low, high);
      else
        traverseRowByIndices(rowIdx, visitor, low, high);

    }

    private void traverseRowByEntries(final int rowIdx, final EntryVisitor visitor, final long low, final long high) {
      int secPtr = 0;
      int colIdx;
      for (long ptr = low; ptr < high; ptr++) {
        colIdx = InterpolationSearch.search(indices, colIndices.get(ptr), secPtr, indices.length - 1);
        if (colIdx >= 0) {
          visitor.visit(rowIdx, colIdx, values.get(ptr));
          secPtr = colIdx + 1;
        }
        if (secPtr >= indices.length) break;
      }
    }

    private void traverseRowByIndices(final int rowIdx, final EntryVisitor visitor, final long low, final long high) {
      long ptr = low;
      long retrievedIdx;
      for (int colIdx = 0; colIdx < indices.length; colIdx++) {
        retrievedIdx = InterpolationSearch.search(colIndices, indices[colIdx], ptr, high - 1);
        if (retrievedIdx >= 0 && retrievedIdx < high) {
          visitor.visit(rowIdx, colIdx, values.get(retrievedIdx));
          ptr = retrievedIdx + 1;
        }
        if (ptr >= high) break;
      }
    }

  }

}
