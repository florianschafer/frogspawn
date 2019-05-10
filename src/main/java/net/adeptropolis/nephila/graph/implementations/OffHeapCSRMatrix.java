package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.MatrixEntryBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.UnsafeDoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.UnsafeIntBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.UnsafeLongBuffer;

import java.util.stream.IntStream;

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
//    System.out.printf("low: %d, high: %d ---- %d %d -> %d\n", low, high, row, col, 0);
    return binarySearch(col, low, high);
  }

  // Assumes selIndices to be sorted!
  public void multiply(int selIndicesSize, UnsafeIntBuffer selIndices, UnsafeDoubleBuffer arg, UnsafeDoubleBuffer results) {
    IntStream.range(0, selIndicesSize).parallel().forEach(i -> {
      long low = rowPointers.get(i);
      long high = rowPointers.get(i + 1);
      if (low == high) {
        results.set(i, 0); // Row is empty
      } else {
        double prod = 0;
        if (selIndicesSize > high - low) {

          for (long idx = low; idx < high; idx++) {
            int j = colIndices.get(idx);
            // ...




            prod += values.get(idx) * arg.get(foo);
          }



        } else {

        }
//        double prod = 0;
//        int matJ;
//        for (int jIdx = 0; jIdx < selIndicesSize; jIdx++) {
//          int selJ = selIndices.get(jIdx);
//          while ((matJ = colIndices.get(low)) < selJ && low < high) low++; WRONG (matJ is set in the final wrong try)
//          prod += values.get(matJ) * arg.get(jIdx);
//        }
//        values.set(i, prod);

//        long selIndicesPtr = 0;
//        int foo;
//        double prod = 0;
//        for (long idx = low; idx < high; idx++) {
//          int j = colIndices.get(idx);
//          while ((foo = selIndices.get(selIndicesPtr)) < j) selIndicesPtr++; WRONG (matJ is set in the final wrong try)
//          prod += values.get(idx) * arg.get(foo);
//        }
//        results.set(i, prod);

        results.set(i, prod);

      }
    });
  }

  private double binarySearch(long col, long low, long high) {
//    System.out.printf("%d: %d -- %d\n", col, low, high);
    if (colIndices.get(low) == col) return values.get(low);
    if (high == low + 1) return 0.0; // No match within this column
    long mid = (low + high) >> 1;
    if (col >= colIndices.get(mid)) {
      return binarySearch(col, mid, high);
    } else {
      return binarySearch(col, low, mid);
    }
  }

  private static boolean checkFoo(UnsafeIntBuffer buffer, int target, int max, long low, long high) {
    int lowVal = buffer.get(low);
    if (lowVal == target) return true;
    long mid = low + (high - low) * target / (max - lowVal);
    if (target >= buffer.get(mid)) {
      return binarySearch(col, mid, high);
    } else {
      return binarySearch(col, low, mid);
    }


    return false;
  }

  public void free() {
    rowPointers.free();
    colIndices.free();
    values.free();
  }

  public String memoryFootprint() {
    System.out.printf("NumRows: %d, numEntries: %d", numRows, numEntries);
    long fp = (numRows << 2) + (numEntries << 2) + (numEntries << 3);
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
