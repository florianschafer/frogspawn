package net.adeptropolis.nephila.graph.implementations.primitives.arrays;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ArrayIntsTest {

  private static final long BASE_SIZE = (1 << ArrayInts.BIN_BITS);

  @Test
  public void basicCheck() {
    ArrayInts b = new ArrayInts(BASE_SIZE);
    assertThat(b.size(), is(BASE_SIZE));
    for (long i = 0; i < BASE_SIZE; i++) b.set(i, (int) (3 * i));
    for (long i = 0; i < BASE_SIZE; i++) assertThat(b.get(i), is((int) (3 * i)));
  }

  @Test
  public void resizingWorks() {
    ArrayInts b = new ArrayInts(BASE_SIZE);
    assertThat(b.size(), is(BASE_SIZE));
    for (long i = 0; i < BASE_SIZE; i++) b.set(i, (int) (4 * i));
    b.resize(2 * BASE_SIZE);
    assertThat(b.size(), is(2 * BASE_SIZE));
    for (long i = BASE_SIZE; i < 2 * BASE_SIZE; i++) b.set(i, (int) (4 * i));
    for (long i = 0; i < 2 * BASE_SIZE; i++) assertThat(b.get(i), is((int) (4 * i)));
  }

}