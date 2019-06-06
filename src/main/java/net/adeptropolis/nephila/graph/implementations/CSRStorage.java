package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.primitives.Doubles;
import net.adeptropolis.nephila.graph.implementations.primitives.Ints;

public class CSRStorage {

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

  public void free() {
    colIndices.free();
    values.free();
  }

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

}
