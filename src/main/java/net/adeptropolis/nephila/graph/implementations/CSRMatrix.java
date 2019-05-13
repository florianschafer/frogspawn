package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.Buffers;

public class CSRMatrix {

  private final int numRows;
  private final long nnz;
  final long rowPtrs;
  final long colIndices;
  final long values;

  CSRMatrix(int numRows, long nnz, long rowPtrs, long colIndices, long values) {
    this.numRows = numRows;
    this.nnz = nnz;
    this.rowPtrs = rowPtrs;
    this.colIndices = colIndices;
    this.values = values;
  }

  public void free() {
    Buffers.free(rowPtrs);
    Buffers.free(colIndices);
    Buffers.free(values);
  }

  public int getNumRows() {
    return numRows;
  }

  public long getNnz() {
    return nnz;
  }

  public String memoryFootprint() {
    long fp = ((numRows + 1) << 3) + (nnz << 2) + (nnz << 3);
    if (fp >= (1 << 30)) {
      return String.format("%.2f GB", (double)fp / (1 << 30));
    } else if (fp >= (1 << 20)) {
      return String.format("%.2f MB", (double)fp / (1 << 20));
    } else if (fp >= (1 << 10)) {
      return String.format("%.2f KB", (double)fp / (1 << 10));
    } else {
      return String.format("%d bytes", fp);
    }
  }


}
