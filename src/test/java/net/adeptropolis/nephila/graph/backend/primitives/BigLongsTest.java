package net.adeptropolis.nephila.graph.backend.primitives;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class BigLongsTest {

  private static final long BIN_SIZE = (1 << BigLongs.BIN_BITS);

  @Test
  public void basicFunctions() {
    BigLongs b = new BigLongs(BIN_SIZE);
    assertThat(b.size(), is(0L));
    for (long i = 0; i < BIN_SIZE; i++) b.set(i, 3L * i);
    for (long i = 0; i < BIN_SIZE; i++) assertThat(b.get(i), is(3L * i));
    assertThat(b.size(), is(BIN_SIZE));
  }

  @Test
  public void resize() {
    BigLongs b = new BigLongs(0);
    for (long i = 0; i < BIN_SIZE; i++) b.set(i, 4L * i);
    b.resize(2 * BIN_SIZE);
    assertThat(b.size(), is(BIN_SIZE));
    for (long i = BIN_SIZE; i < 2 * BIN_SIZE; i++) b.set(i, 4L * i);
    assertThat(b.size(), is(2 * BIN_SIZE));
    for (long i = 0; i < 2 * BIN_SIZE; i++) assertThat(b.get(i), is(4L * i));
  }

  @Test
  public void equals() {
    BigLongs b = new BigLongs(29);
    b.set(0, 3L);
    b.set(1, 1L);
    b.set(2, 4L);
    assertThat(b, is(BigLongs.of(3L, 1L, 4L)));
    assertThat(b, is(not(BigLongs.of(3L, 1L, 5L))));
    assertThat(b, is(not(BigLongs.of(3L))));
  }

}