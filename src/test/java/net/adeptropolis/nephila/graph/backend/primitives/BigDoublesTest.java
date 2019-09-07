package net.adeptropolis.nephila.graph.backend.primitives;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class BigDoublesTest {

  private static final long BIN_SIZE = (1 << BigDoubles.BIN_BITS);

  @Test
  public void basicFunctions() {
    BigDoubles b = new BigDoubles(BIN_SIZE);
    assertThat(b.size(), is(0L));
    for (long i = 0; i < BIN_SIZE; i++) b.set(i, 3.14 * i);
    for (long i = 0; i < BIN_SIZE; i++) assertThat(b.get(i), is(3.14 * i));
    assertThat(b.size(), is(BIN_SIZE));
  }

  @Test
  public void resize() {
    BigDoubles b = new BigDoubles(0);
    for (long i = 0; i < BIN_SIZE; i++) b.set(i, 2.71 * i);
    b.resize(2 * BIN_SIZE);
    assertThat(b.size(), is(BIN_SIZE));
    for (long i = BIN_SIZE; i < 2 * BIN_SIZE; i++) b.set(i, 2.71 * i);
    assertThat(b.size(), is(2 * BIN_SIZE));
    for (long i = 0; i < 2 * BIN_SIZE; i++) assertThat(b.get(i), is(2.71 * i));
  }

  @Test
  public void equals() {
    BigDoubles b = new BigDoubles(29);
    b.set(0, 3.1);
    b.set(1, 1.4);
    b.set(2, 4.1);
    assertThat(b, is(BigDoubles.of(3.1, 1.4, 4.1)));
    assertThat(b, is(not(BigDoubles.of(3.1, 1.4, 5.2))));
    assertThat(b, is(not(BigDoubles.of(3))));
  }

}