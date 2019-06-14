package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.primitives.Doubles;
import net.adeptropolis.nephila.graph.implementations.primitives.Ints;
import net.adeptropolis.nephila.graph.implementations.primitives.search.InterpolationSearch;

import java.util.Arrays;

public class CSRStorage {

  private final int numRows;
  private final long nnz;

  private final long[] rowPtrs;
  private final Ints colIndices;
  private final Doubles values;
  private final View defaultView;

  CSRStorage(int numRows, long nnz, long[] rowPtrs, Ints colIndices, Doubles values) {
    this.numRows = numRows;
    this.nnz = nnz;
    this.rowPtrs = rowPtrs;
    this.colIndices = colIndices;
    this.values = values;
    this.defaultView = new View();
  }

  public View defaultView() {
    return defaultView;
  }

  public void free() {
    defaultView.cleanup();
    colIndices.free();
    values.free();
  }

  // TODO: Remove all those getters!

  public int getNumRows() {
    return numRows;
  }

  public long getNnz() {
    return nnz;
  }

  public long[] getRowPtrs() {
    return rowPtrs;
  }

  public Ints getColIndices() {
    return colIndices;
  }

  public Doubles getValues() {
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

    public final int[] indices;
    public int indicesSize;
    public final CSRViewTraversal traversal;

    View() {
      // Init with default (full) defaultView
      this.indices = new int[numRows];
      for (int i = 0; i < numRows; i++) this.indices[i] = i;
      this.indicesSize = numRows;
      this.traversal = new CSRViewTraversal(this);
    }

    public void traverse(final EntryVisitor visitor) {
      traversal.traverse(visitor);
    }

    public void traverseRow(final int rowIdx, final EntryVisitor visitor) {
      if (indicesSize == 0) return;
      int row = indices[rowIdx];
      long low = rowPtrs[row];
      long high = rowPtrs[row + 1];
      if (low == high) return;

      if (indicesSize > high - low)
        traverseRowByEntries(rowIdx, visitor, low, high);
      else
        traverseRowByIndices(rowIdx, visitor, low, high);

    }

    private void traverseRowByEntries(final int rowIdx, final EntryVisitor visitor, final long low, final long high) {
      int secPtr = 0;
      int colIdx;
      for (long ptr = low; ptr < high; ptr++) {
        colIdx = InterpolationSearch.search(indices, colIndices.get(ptr), secPtr, indicesSize - 1);
        if (colIdx >= 0) {
          visitor.visit(rowIdx, colIdx, values.get(ptr));
          secPtr = colIdx + 1;
        }
        if (secPtr >= indicesSize) break;
      }
    }

    private void traverseRowByIndices(final int rowIdx, final EntryVisitor visitor, final long low, final long high) {
      long ptr = low;
      long retrievedIdx;
      for (int colIdx = 0; colIdx < indicesSize; colIdx++) {
        retrievedIdx = InterpolationSearch.search(colIndices, indices[colIdx], ptr, high - 1);
        if (retrievedIdx >= 0 && retrievedIdx < high) {
          visitor.visit(rowIdx, colIdx, values.get(retrievedIdx));
          ptr = retrievedIdx + 1;
        }
        if (ptr >= high) break;
      }
    }

    public void set(int[] newIndices) {
      Arrays.sort(newIndices);
      System.arraycopy(newIndices, 0, indices, 0, newIndices.length);
      indicesSize = newIndices.length;
    }

    public int maxSize() {
      return indices.length;
    }

    public void cleanup() {
      traversal.cleanup();
    }

  }

}
