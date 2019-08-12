package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.implementations.primitives.Doubles;
import net.adeptropolis.nephila.graph.implementations.primitives.Ints;

// TODO: Make indices an efficiently-flushable sorted set?

public class CSRStorage {

  final CSRTraversal traversal = new CSRTraversal();

  private final int numRows;
  private final long nnz;

  final long[] rowPtrs;
  final Ints colIndices;
  final Doubles values;

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
    return new View(this, indices);
  }

  public void free() {
    colIndices.free();
    values.free();
    traversal.cleanup();
  }

  public int getNumRows() {
    return numRows;
  }

  public long getNnz() {
    return nnz;
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

}
