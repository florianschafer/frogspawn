package net.adeptropolis.nephila.graph.implementations.buffers;

import net.adeptropolis.nephila.graph.implementations.buffers.unsafe.Buffers;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

public class SortedBuffersTest {

  @Test
  public void denseIntArray() {
    long buf = Buffers.allocInts(10000);
    for (int i = 0; i < 10000; i++) Buffers.setInt(buf, i,i);
    SortedBuffers.sortInts(buf, 10000);
    for (int i = 0; i < 10000; i++) assertThat(SortedBuffers.searchInt(buf, 10000, i), greaterThanOrEqualTo(0L));
    Buffers.free(buf);
  }

  @Test
  public void sparseIntArray() {
    long buf = Buffers.allocInts(10000);
    for (int i = 0; i < 10000; i++) Buffers.setInt(buf, i, i * 13);
    SortedBuffers.sortInts(buf, 10000);
    for (int i = 0; i < 10000; i++) {
      if (i % 13 == 0) assertThat(SortedBuffers.searchInt(buf, 10000, i), greaterThanOrEqualTo(0L));
      else assertThat(SortedBuffers.searchInt(buf, 10000, i), lessThan(0L));
    }
    Buffers.free(buf);
  }

  @Test
  public void verySparse() {
    long buf = Buffers.allocInts(10000);
    for (int i = 0; i < 10000; i++) Buffers.setInt(buf, i, 0);
    Buffers.setInt(buf, 9998, 60);
    Buffers.setInt(buf, 9999, 100);
    assertThat(SortedBuffers.searchInt(buf, 10000, 60), greaterThanOrEqualTo(0L));
    assertThat(SortedBuffers.searchInt(buf, 10000, 90), lessThan(0L));
    Buffers.free(buf);
  }


}