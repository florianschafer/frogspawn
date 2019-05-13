package net.adeptropolis.nephila.graph.implementations.buffers;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class MatrixEntryBufferTest {

  @Test
  public void warn() {
    throw new RuntimeException("Throw me out");
  }

//  @Test
//  public void checkGrowCapability() {
//    MatrixEntryBuffer buffer = new MatrixEntryBuffer(10, 10);
//    for (int i = 0; i < 100; i++) buffer.add(i / 10, i % 10, i);
//    assertThat("Size matches", buffer.getSize(), is(100L));
//    for (int i = 0; i < 100; i++) verifyEntry(buffer, i, i / 10, i % 10, i);
//    buffer.free();
//  }
//
//  @Test
//  public void checkSorting() {
//    MatrixEntryBuffer buffer = new MatrixEntryBuffer();
//    buffer.add(8, 9, 7);
//    buffer.add(5, 3, 6);
//    buffer.add(2, 6, 3);
//    buffer.add(2, 1, 2);
//    buffer.add(4, 4, 4);
//    buffer.add(5, 2, 5);
//    buffer.add(0, 0, 1);
//    buffer.sort();
//    assertThat("Size matches", buffer.getSize(), is(7L));
//    verifyEntry(buffer, 0L, 0, 0, 1d);
//    verifyEntry(buffer, 1L, 2, 1, 2d);
//    verifyEntry(buffer, 2L, 2, 6, 3d);
//    verifyEntry(buffer, 3L, 4, 4, 4d);
//    verifyEntry(buffer, 4L, 5, 2, 5d);
//    verifyEntry(buffer, 5L, 5, 3, 6d);
//    verifyEntry(buffer, 6L, 8, 9, 7d);
//    buffer.free();
//  }
//
//  @Test
//  public void checkReduceTail() {
//    MatrixEntryBuffer buffer = new MatrixEntryBuffer();
//    buffer.add(0, 1, 2.71);
//    buffer.add(0, 1, 3.14);
//    buffer.add(0, 0, 42.0);
//    buffer.reduce();
//    assertThat("Size matches", buffer.getSize(), is(2L));
//    verifyEntry(buffer, 0L, 0, 0, 42.0);
//    verifyEntry(buffer, 1L, 0, 1, 5.85d);
//    buffer.free();
//  }
//
//  @Test
//  public void checkReduceHead() {
//    MatrixEntryBuffer buffer = new MatrixEntryBuffer();
//    buffer.add(0, 0, 2.71);
//    buffer.add(0, 0, 3.14);
//    buffer.add(0, 1, 42.0);
//    buffer.reduce();
//    assertThat("Size matches", buffer.getSize(), is(2L));
//    verifyEntry(buffer, 0L, 0, 0, 5.85d);
//    verifyEntry(buffer, 1L, 0, 1, 42.0);
//    buffer.free();
//  }
//
//  @Test
//  public void checkReduceMid() {
//    MatrixEntryBuffer buffer = new MatrixEntryBuffer();
//    buffer.add(2, 4, 17);
//    buffer.add(1, 6, 13);
//    buffer.add(0, 1, 43.0);
//    buffer.add(1, 5, 11);
//    buffer.add(0, 0, 42.0);
//    buffer.add(1, 5, 5);
//    buffer.add(1, 5, 7);
//    buffer.reduce();
//    assertThat("Size matches", buffer.getSize(), is(5L));
//    verifyEntry(buffer, 0L, 0, 0, 42d);
//    verifyEntry(buffer, 1L, 0, 1, 43d);
//    verifyEntry(buffer, 2L, 1, 5, 23d);
//    verifyEntry(buffer, 3L, 1, 6, 13d);
//    verifyEntry(buffer, 4L, 2, 4, 17d);
//    buffer.free();
//  }
//
//  private void verifyEntry(MatrixEntryBuffer buffer, long idx, int row, int col, double value) {
//    assertThat("Row match", buffer.getRow(idx), is(row));
//    assertThat("Column match", buffer.getCol(idx), is(col));
//    assertThat("Value match", buffer.getValue(idx), is(value));
//  }

}