package net.adeptropolis.nephila.graph.implementations;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.implementations.buffers.Buffers;
import net.adeptropolis.nephila.graph.implementations.buffers.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.implementations.buffers.sorting.LongSwapper;

public class CSRMatrixBuilder {

  private static final long DEFAULT_SIZE_INCREMENT = 1 << 24;

  private final long sizeIncrement;
  private long reservedSize;
  private long ptr = 0L;
  private long rowIndices;
  private long colIndices;
  private long values;

  CSRMatrixBuilder(long initialReservedSize, long sizeIncrement) {
    Preconditions.checkArgument(initialReservedSize > 0, "Inital reserved size must be > 0");
    this.reservedSize = initialReservedSize;
    this.sizeIncrement = sizeIncrement;
    this.rowIndices = Buffers.allocInts(initialReservedSize);
    this.colIndices = Buffers.allocInts(initialReservedSize);
    this.values = Buffers.allocDoubles(initialReservedSize);
  }

  public CSRMatrixBuilder() {
    this(DEFAULT_SIZE_INCREMENT, DEFAULT_SIZE_INCREMENT);
  }

  public CSRMatrixBuilder add(int row, int col, double value) {
    set(ptr++, row, col, value);
    if (ptr == reservedSize) resize(reservedSize + sizeIncrement);
    return this;
  }

  public CSRMatrixBuilder addSymmetric(int row, int col, double value) {
    add(row, col, value);
    add(col, row, value);
    return this;
  }

  public CSRMatrix build() {

    if (ptr == 0L) {
      return new CSRMatrix(0, 0, Buffers.allocLongs(0), Buffers.allocInts(0), Buffers.allocDoubles(0));
    }

    sort();
    reduceSorted();
    compact();

    int numRows = Buffers.getInt(rowIndices, ptr - 1) + 1;
    long rowPtrs = computeRowPointers(numRows);

    Buffers.free(rowIndices);

    return new CSRMatrix(numRows, ptr, rowPtrs, colIndices, values);
  }

  private void set(long i, int row, int col, double value) {
    Buffers.setInt(rowIndices, i, row);
    Buffers.setInt(colIndices, i, col);
    Buffers.setDouble(values, i, value);
  }

  private void resize(long newSize) {
    reservedSize = newSize;
    Buffers.resizeInts(rowIndices, newSize);
    Buffers.resizeInts(colIndices, newSize);
    Buffers.resizeDoubles(values, newSize);
  }

  private void sort() {
    SortHelper helper = new SortHelper();
    LongMergeSort.mergeSort(0, ptr, helper, helper);
  }

  private void reduceSorted() {

    if (ptr == 0) return;

    int activeRow = Buffers.getInt(rowIndices, 0);
    int activeCol = Buffers.getInt(colIndices, 0);
    double activeValue = Buffers.getDouble(values, 0);

    int row;
    int col;
    double val;

    long writePtr = 0;

    for (long scrollPtr = 1; scrollPtr < ptr; scrollPtr++) {

      row = Buffers.getInt(rowIndices, scrollPtr);
      col = Buffers.getInt(colIndices, scrollPtr);
      val = Buffers.getDouble(values, scrollPtr);

      if (row == activeRow && col == activeCol) {
        activeValue += val;
      } else {
        if (writePtr < scrollPtr) set(writePtr++, activeRow, activeCol, activeValue);
        activeRow = row;
        activeCol = col;
        activeValue = val;
      }
    }

    set(writePtr++, activeRow, activeCol, activeValue);
    ptr = writePtr;

  }

  private void compact() {
    resize(ptr);
  }

  private long computeRowPointers(int numRows) {

    long rowPtrs = Buffers.allocLongs(numRows + 1);
    Buffers.setLong(rowPtrs, 0, 0);
    Buffers.setLong(rowPtrs, numRows, ptr);

    int prevRow = 0;
    int row;

    for (long i = 0; i < ptr; i++) {
      row = Buffers.getInt(rowIndices, i);
      if (row > prevRow) {
        for (int j = prevRow + 1; j < row; j++) Buffers.setLong(rowPtrs, j, i);
        Buffers.setLong(rowPtrs, row, i);
        prevRow = row;
      }
    }

    return rowPtrs;
  }


  private class SortHelper implements LongSwapper, LongComparator {

    @Override
    public int compare(long i1, long i2) {
      int c = Buffers.compareInts(rowIndices, i1, i2);
      return c != 0 ? c : Buffers.compareInts(colIndices, i1, i2);
    }

    @Override
    public void swap(long idx1, long idx2) {
      Buffers.swapInts(rowIndices, idx1, idx2);
      Buffers.swapInts(colIndices, idx1, idx2);
      Buffers.swapDoubles(values, idx1, idx2);
    }

  }


}
