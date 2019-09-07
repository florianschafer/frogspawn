package net.adeptropolis.nephila.graph.backend.primitives;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class BigFloatsTest {

  private static final long BIN_SIZE = (1 << BigFloats.BIN_BITS);

  @Test
  public void basicFunctions() {
    BigFloats b = new BigFloats(BIN_SIZE);
    assertThat(b.size(), is(0L));
    for (long i = 0; i < BIN_SIZE; i++) b.set(i, 3.14f * i);
    for (long i = 0; i < BIN_SIZE; i++) assertThat(b.get(i), is(3.14f * i));
    assertThat(b.size(), is(BIN_SIZE));
  }

  @Test
  public void resize() {
    BigFloats b = new BigFloats(0);
    for (long i = 0; i < BIN_SIZE; i++) b.set(i, 2.71f * i);
    b.resize(2 * BIN_SIZE);
    assertThat(b.size(), is(BIN_SIZE));
    for (long i = BIN_SIZE; i < 2 * BIN_SIZE; i++) b.set(i, 2.71f * i);
    assertThat(b.size(), is(2 * BIN_SIZE));
    for (long i = 0; i < 2 * BIN_SIZE; i++) assertThat(b.get(i), is(2.71f * i));
  }

  @Test
  public void equals() {
    BigFloats b = new BigFloats(29);
    b.set(0, 3.1f);
    b.set(1, 1.4f);
    b.set(2, 4.1f);
    assertThat(b, is(BigFloats.of(3.1f, 1.4f, 4.1f)));
    assertThat(b, is(not(BigFloats.of(3.1f, 1.4f, 5.2f))));
    assertThat(b, is(not(BigFloats.of(3f))));
  }

  @Test
  public void sort() {
    BigFloats sorted = BigFloats.of(9f, 8f, 6f, 7f, 3f, 5f, 4f, 2f, 0f, 1f).sort();
    assertThat(sorted, is(BigFloats.of(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f)));
  }

}