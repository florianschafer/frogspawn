package net.adeptropolis.nephila.graph.implementations.buffers.arrays;

import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.arrays.ArrayIntBuffer;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ArrayIntBufferTest {

  private static final long BASE_SIZE = (1 << ArrayIntBuffer.BIN_BITS);

  @Test
  public void basicCheck() {
    ArrayIntBuffer b = new ArrayIntBuffer(BASE_SIZE);
    assertThat(b.size(), is(BASE_SIZE));
    for (long i = 0; i < BASE_SIZE; i++) b.set(i, (int) (3 * i));
    for (long i = 0; i < BASE_SIZE; i++) assertThat(b.get(i), is((int)(3 * i)));
  }

  @Test
  public void resizingWorks() {
    ArrayIntBuffer b = new ArrayIntBuffer(BASE_SIZE);
    assertThat(b.size(), is(BASE_SIZE));
    for (long i = 0; i < BASE_SIZE; i++) b.set(i, (int) (4 * i));
    b.resize(2 * BASE_SIZE);
    assertThat(b.size(), is(2 * BASE_SIZE));
    for (long i = BASE_SIZE; i < 2 * BASE_SIZE; i++) b.set(i, (int) (4 * i));
    for (long i = 0; i < 2 * BASE_SIZE; i++) assertThat(b.get(i), is((int)(4 * i)));
  }

  @Test
  public void searchDenseIntArray() {
    IntBuffer buf = new ArrayIntBuffer(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i,i);
    buf.sort(10000);
    for (int i = 0; i < 10000; i++) Assert.assertThat(buf.searchSorted(i, 10000), greaterThanOrEqualTo(0L));
  }

  @Test
  public void searchSparseIntArray() {
    IntBuffer buf = new ArrayIntBuffer(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i, i * 13);
    buf.sort(10000);
    for (int i = 0; i < 10000; i++) {
      if (i % 13 == 0) Assert.assertThat(buf.searchSorted(i, 10000), greaterThanOrEqualTo(0L));
      else Assert.assertThat(buf.searchSorted(i, 10000), lessThan(0L));
    }
  }

  @Test
  public void searchVerySparse() {
    IntBuffer buf = new ArrayIntBuffer(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i, 0);
    buf.set(9998, 60);
    buf.set(9999, 100);
    Assert.assertThat(buf.searchSorted(60, 10000), greaterThanOrEqualTo(0L));
    Assert.assertThat(buf.searchSorted(90, 10000), lessThan(0L));
  }

}