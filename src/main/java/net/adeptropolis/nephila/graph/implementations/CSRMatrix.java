package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.Buffers;
import net.adeptropolis.nephila.graph.implementations.buffers.SortedBuffers;

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

  double rowScalarProduct(int row, long vecBuf, long indexBuf, int bufSize) {

    long low = Buffers.getLong(rowPtrs, row);
    long high = Buffers.getLong(rowPtrs, row + 1);
    if (low == high) return 0; // Empty row

    double prod = 0;
    int col;
    long retrievedIdx;
    long secPtr;

    if (bufSize == numRows) {
      // All indices selected
      for (long ptr = low; ptr < high; ptr++) {
        col = Buffers.getInt(colIndices, ptr);
        prod += Buffers.getDouble(values, ptr) * Buffers.getDouble(vecBuf, col);
      }
    } else if (bufSize > high - low) {
      // |indices| > row nnz
      secPtr = 0L;
      for (long ptr = low; ptr < high; ptr++) {
        col = Buffers.getInt(colIndices, ptr);
        retrievedIdx = SortedBuffers.searchInt(indexBuf, bufSize, col, secPtr);
        if (retrievedIdx >= 0) {
          prod += Buffers.getDouble(values, ptr) * Buffers.getDouble(vecBuf, retrievedIdx);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= bufSize) break;
      }
    } else {
      // row nnz > |indices|
      secPtr = low;
      for (long ptr = 0; ptr < bufSize; ptr++) {
        col = Buffers.getInt(indexBuf, ptr);
//        todo: row ptrs seem to be off. See log statement
//        System.out.printf("Searching key = %d, size = %d, secPtr = %d\n", col, bufSize, secPtr);
        retrievedIdx = SortedBuffers.searchInt(colIndices, bufSize, col, secPtr);
        if (retrievedIdx >= 0 && retrievedIdx < high) {
          prod += Buffers.getDouble(values, retrievedIdx) * Buffers.getDouble(vecBuf, ptr);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= high) break;
      }
    }

    return prod;
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
      return String.format("%.2f GB", (double) fp / (1 << 30));
    } else if (fp >= (1 << 20)) {
      return String.format("%.2f MB", (double) fp / (1 << 20));
    } else if (fp >= (1 << 10)) {
      return String.format("%.2f KB", (double) fp / (1 << 10));
    } else {
      return String.format("%d bytes", fp);
    }
  }


}
