package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.MatrixEntryBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.UnsafeDoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.UnsafeIntBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.UnsafeLongBuffer;

/*
* ICSR refers to a slightly modified variant where pointers to empty rows have index -1.
* This should facilitate random access for row-parallel multiplication.
* */

public class OffHeapCSRMatrix {

  final int numRows;
  final long numEntries;
  final UnsafeLongBuffer rowPointers;
  private final UnsafeIntBuffer colIndices;
  private final UnsafeDoubleBuffer values;

  public OffHeapCSRMatrix(int numRows, long numEntries, UnsafeLongBuffer rowPointers, UnsafeIntBuffer colIndices, UnsafeDoubleBuffer values) {
    this.numRows = numRows;
    this.numEntries = numEntries;
    this.rowPointers = rowPointers;
    this.colIndices = colIndices;
    this.values = values;
  }

  public double get(int row, int col) {
    if (row < 0 || row >= numRows) return 0d;
    long low = rowPointers.get(row);
    long high = rowPointers.get(row + 1);
    if (low == high) return 0d; // Row is empty
    if (colIndices.get(high - 1) <= col ) return 0;
    return binarySearch(col, low, high);
  }

  private double binarySearch(long col, long low, long high) {
    if (colIndices.get(low) == col) return values.get(low);
    if (high == low + 1) return 0.0; // No match within this column
    long mid = (low + high) >> 1;
    if (col >= colIndices.get(mid)) {
      return binarySearch(col, mid, high);
    } else {
      return binarySearch(col, low, mid);
    }
  }

  public void free() {
    rowPointers.free();
    colIndices.free();
    values.free();
  }

  public String memoryFootprint() {
    long fp = (numRows << 2) + (numEntries << 2) + (numEntries << 3);
    if (fp >= (1 << 30)) {
      return String.format("%d GB", fp >> 30);
    } else if (fp >= (1 << 20)) {
      return String.format("%d MB", fp >> 20);
    } else if (fp >= (1 << 10)) {
      return String.format("%d KB", fp >> 10);
    } else {
      return String.format("%d bytes", fp);
    }
  }

  static class Builder {

    private final MatrixEntryBuffer buffer;

    Builder() {
      this.buffer = new MatrixEntryBuffer();
    }

    public Builder add(int row, int col, double value) {
      buffer.add(row, col, value);
      return this;
    }

    public OffHeapCSRMatrix build() {
      buffer.reduce();
      int numRows = buffer.getRow(buffer.getSize() - 1) + 1;
      long numEntries = buffer.getSize();
      UnsafeLongBuffer rowPointers = computeRowPointers(numRows);
      buffer.freeRowIndices();
      return new OffHeapCSRMatrix(numRows, numEntries, rowPointers, buffer.getColIndices(), buffer.getValues());
    }

    private UnsafeLongBuffer computeRowPointers(int numRows) {

      UnsafeLongBuffer rowPointers = new UnsafeLongBuffer(numRows + 1);
      rowPointers.set(0, 0);
      rowPointers.set(numRows, buffer.getSize());

      int prevCol = -1;
      int prevRow = 0;
      for (long i = 0; i < buffer.getSize(); i++) {
        int col = buffer.getCol(i);
        if (prevCol == - 1 || col < prevCol) {
          int row = buffer.getRow(i);
          for (int j = prevRow + 1; j < row; j++) rowPointers.set(j, i);
          rowPointers.set(row, i);
          prevRow = row;
        }
        prevCol = col;
      }

      return rowPointers;
    }

  }

}
