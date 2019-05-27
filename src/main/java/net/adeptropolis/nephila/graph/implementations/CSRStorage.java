package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.LongBuffer;

public class CSRStorage {

  private final int numRows;
  private final long nnz;

  private final LongBuffer rowPtrs;
  private final IntBuffer colIndices;
  private final DoubleBuffer values;

  CSRStorage(int numRows, long nnz, LongBuffer rowPtrs, IntBuffer colIndices, DoubleBuffer values) {
    this.numRows = numRows;
    this.nnz = nnz;
    this.rowPtrs = rowPtrs;
    this.colIndices = colIndices;
    this.values = values;
  }

  public void free() {
    rowPtrs.free();
    colIndices.free();
    values.free();
  }

  public int getNumRows() {
    return numRows;
  }

  public long getNnz() {
    return nnz;
  }

  public LongBuffer getRowPtrs() {
    return rowPtrs;
  }

  public IntBuffer getColIndices() {
    return colIndices;
  }

  public DoubleBuffer getValues() {
    return values;
  }

  public String memoryFootprint() {
    long fp = ((numRows + 1) << 3) + (nnz << 2) + (nnz << 3);
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

  public void print() {
    System.out.println("Row pointers:");
    for (int i = 0; i < numRows; i++) {
      System.out.printf("  %d -> %d\n", i, rowPtrs.get(i));
    }
    System.out.println("Column indices / values:");
    for (int i = 0; i < nnz; i++) {
      System.out.printf("  %d: %d -> %f\n", i,colIndices.get(i), values.get(i));
    }
  }

}
