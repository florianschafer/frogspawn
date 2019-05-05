package net.adeptropolis.nephila.graph.implementations.buffers;

import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.nephila.graph.implementations.buffers.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.implementations.buffers.sorting.LongSwapper;

public class MatrixEntryBuffer implements LongSwapper, LongComparator {

  private static final long DEFAULT_SIZE_INCREMENT = 1 << 24;
  private final long sizeIncrement;
  private long maxSize;
  private long size;
  private UnsafeIntBuffer rowIndices;

  private UnsafeIntBuffer colIndices;
  private UnsafeDoubleBuffer values;

  MatrixEntryBuffer(long initialSize, long sizeIncrement) {
    this.maxSize = initialSize;
    this.sizeIncrement = sizeIncrement;
    this.size = 0L;
    this.rowIndices = new UnsafeIntBuffer(initialSize);
    this.colIndices = new UnsafeIntBuffer(initialSize);
    this.values = new UnsafeDoubleBuffer(initialSize);
  }

  public MatrixEntryBuffer() {
    this(DEFAULT_SIZE_INCREMENT, DEFAULT_SIZE_INCREMENT);
  }

  public void add(int row, int col, double value) {
    if (size + 1 == maxSize) {
      resize(maxSize + sizeIncrement);
    }
    set(size, row, col, value);
    size++;
  }

  public void reduce() {
    sort();
    reduceSorted();
    compact();
  }

  void sort() {
    LongMergeSort.mergeSort(0, size, this, this);
  }

  private void reduceSorted() {

    if (size == 0) return;

    int activeRow = rowIndices.get(0);
    int activeCol = colIndices.get(0);
    double activeValue = values.get(0);
    long writePtr = 0;

    for (long scrollPtr = 1; scrollPtr < size; scrollPtr++) {
      int row = rowIndices.get(scrollPtr);
      int col = colIndices.get(scrollPtr);
      double val = values.get(scrollPtr);
      if (row == activeRow && col == activeCol) {
        activeValue += val;
      } else {
        if (scrollPtr > writePtr + 1 /* CHECK */) {
          set(writePtr, activeRow, activeCol, activeValue);
        }
        activeRow = row;
        activeCol = col;
        activeValue = val;
        writePtr++;
      }
    }

    set(writePtr, activeRow, activeCol, activeValue);
    size = ++writePtr;

  }

  private void set(long idx, int row, int col, double value) {
    rowIndices.set(idx, row);
    colIndices.set(idx, col);
    values.set(idx, value);
  }

  private void compact() {
    resize(size);
  }

  private void resize(long newSize) {
    maxSize = newSize;
    rowIndices.resize(newSize);
    colIndices.resize(newSize);
    values.resize(newSize);
  }

  public void free() {
    rowIndices.free();
    colIndices.free();
    values.free();
  }

  public void freeRowIndices() {
    rowIndices.free();
  }


  public long getSize() {
    return size;
  }

  public int getRow(long idx) {
    return rowIndices.get(idx);
  }

  public int getCol(long idx) {
    return colIndices.get(idx);
  }

  double getValue(long idx) {
    return values.get(idx);
  }

  public UnsafeIntBuffer getColIndices() {
    return colIndices;
  }

  public UnsafeDoubleBuffer getValues() {
    return values;
  }

  @Override
  public int compare(long idx1, long idx2) {
    int row1 = rowIndices.get(idx1);
    int row2 = rowIndices.get(idx2);
    if (row1 != row2) {
      return Integer.compare(row1, row2);
    }
    int col1 = colIndices.get(idx1);
    int col2 = colIndices.get(idx2);
    return Integer.compare(col1, col2);
  }

  @Override
  public void swap(long idx1, long idx2) {
    rowIndices.swap(idx1, idx2);
    colIndices.swap(idx1, idx2);
    values.swap(idx1, idx2);
  }

}
