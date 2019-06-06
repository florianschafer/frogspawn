package net.adeptropolis.nephila.graph.implementations;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.implementations.primitives.Doubles;
import net.adeptropolis.nephila.graph.implementations.primitives.Ints;
import net.adeptropolis.nephila.graph.implementations.primitives.arrays.ArrayDoubles;
import net.adeptropolis.nephila.graph.implementations.primitives.arrays.ArrayInts;
import net.adeptropolis.nephila.graph.implementations.primitives.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.implementations.primitives.sorting.LongSwapper;

public class CSRStorageBuilder {

  private static final long DEFAULT_SIZE_INCREMENT = 1 << 24;

  private final long sizeIncrement;
  private long reservedSize;
  private long ptr = 0L;
  private Ints rowIndices;
  private Ints colIndices;
  private Doubles values;

  public CSRStorageBuilder() {
    this(DEFAULT_SIZE_INCREMENT, DEFAULT_SIZE_INCREMENT);
  }

  CSRStorageBuilder(long initialReservedSize, long sizeIncrement) {
    Preconditions.checkArgument(initialReservedSize > 0, "Inital reserved size must be > 0");
    this.reservedSize = initialReservedSize;
    this.sizeIncrement = sizeIncrement;
    this.rowIndices = new ArrayInts(initialReservedSize);
    this.colIndices = new ArrayInts(initialReservedSize);
    this.values = new ArrayDoubles(initialReservedSize);
  }

  public CSRStorageBuilder addSymmetric(int row, int col, double value) {
    add(row, col, value);
    if (row != col) add(col, row, value);
    return this;
  }

  public CSRStorageBuilder add(int row, int col, double value) {
    set(ptr++, row, col, value);
    if (ptr == reservedSize) resize(reservedSize + sizeIncrement);
    return this;
  }

  private void set(long i, int row, int col, double value) {
    rowIndices.set(i, row);
    colIndices.set(i, col);
    values.set(i, value);
  }

  private void resize(long newSize) {
    reservedSize = newSize;
    rowIndices.resize(newSize);
    colIndices.resize(newSize);
    values.resize(newSize);
  }

  public CSRStorage build() {

    if (ptr == 0L) {
      return new CSRStorage(0, 0, new long[0], new ArrayInts(0), new ArrayDoubles(0));
    }

    sort();
    reduceSorted();
    compact();

    int numRows = rowIndices.get(ptr - 1) + 1;
    long[] rowPtrs = computeRowPointers(numRows);
    rowIndices.free();

    return new CSRStorage(numRows, ptr, rowPtrs, colIndices, values);
  }

  private void sort() {
    SortHelper helper = new SortHelper();
    LongMergeSort.mergeSort(0, ptr, helper, helper);
  }

  private void reduceSorted() {

    if (ptr == 0) return;

    int activeRow = rowIndices.get(0);
    int activeCol = colIndices.get(0);
    double activeValue = values.get(0);

    int row;
    int col;
    double val;

    long writePtr = 0;

    for (long scrollPtr = 1; scrollPtr < ptr; scrollPtr++) {

      row = rowIndices.get(scrollPtr);
      col = colIndices.get(scrollPtr);
      val = values.get(scrollPtr);

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

  private long[] computeRowPointers(int numRows) {

    long[] rowPtrs = new long[numRows + 1];
    rowPtrs[0] = 0;
    rowPtrs[numRows] = ptr;

    int prevRow = 0;
    int row;

    for (long i = 0; i < ptr; i++) {
      row = rowIndices.get(i);
      if (row > prevRow) {
        for (int j = prevRow + 1; j <= row; j++) rowPtrs[j] = i;
        prevRow = row;
      }
    }

    return rowPtrs;
  }


  private class SortHelper implements LongSwapper, LongComparator {

    @Override
    public int compare(long i1, long i2) {
      int c = rowIndices.compareEntries(i1, i2);
      return c != 0 ? c : colIndices.compareEntries(i1, i2);
    }

    @Override
    public void swap(long idx1, long idx2) {
      rowIndices.swapEntries(idx1, idx2);
      colIndices.swapEntries(idx1, idx2);
      values.swapEntries(idx1, idx2);
    }

  }


}
