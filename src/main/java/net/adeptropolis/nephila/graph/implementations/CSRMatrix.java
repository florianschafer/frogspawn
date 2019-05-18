package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.Buffers;
import net.adeptropolis.nephila.graph.implementations.buffers.SortedBuffers;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;

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

  public void multiply(long vecBuf, long indexBuf, long resultBuf, int bufSize) {
    // TODO: Check parallel streams are efficient. Look at RecursiveAction
    for (int i = 0; i < bufSize; i++) Buffers.setDouble(resultBuf, i, 0);
    IntStream.range(0, bufSize).parallel().forEach(i -> {
      int row = Buffers.getInt(indexBuf, i);
      double p = rowScalarProduct(row, vecBuf, indexBuf, bufSize);
      Buffers.setDouble(resultBuf, i, p);
    });
  }

  double rowScalarProduct(int row, long vecBuf, long indexBuf, int bufSize) {
    long low = Buffers.getLong(rowPtrs, row);
    long high = Buffers.getLong(rowPtrs, row + 1);
    if (low == high) return 0; // Empty row
    if (bufSize == 0) return 0; // Empty indices

    double prod = 0;
    int col;
    long retrievedIdx;
    long secPtr;

    if (bufSize > high - low) {
      secPtr = 0L;
      for (long ptr = low; ptr < high; ptr++) {
        col = Buffers.getInt(colIndices, ptr);
        retrievedIdx = SortedBuffers.searchInt(indexBuf, col, secPtr, bufSize - 1);
        if (retrievedIdx >= 0) {
          prod += Buffers.getDouble(values, ptr) * Buffers.getDouble(vecBuf, retrievedIdx);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= bufSize) break;
      }
    } else {
      secPtr = low;
      for (long ptr = 0; ptr < bufSize; ptr++) {
        col = Buffers.getInt(indexBuf, ptr);
        retrievedIdx = SortedBuffers.searchInt(colIndices, col, secPtr, high);
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

  public void print() {
    System.out.println("Row pointers:");
    for (int i = 0; i < numRows; i++) {
      System.out.printf("  %d -> %d\n", i, Buffers.getLong(rowPtrs, i));
    }
    System.out.println("Column indices / values:");
    for (int i = 0; i < nnz; i++) {
      System.out.printf("  %d: %d -> %f\n", i, Buffers.getInt(colIndices, i), Buffers.getDouble(values, i));
    }
  }

}
