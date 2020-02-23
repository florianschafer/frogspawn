/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.implementations.arrays;

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
    b.resize(2 * BIN_SIZE);
    assertThat(b.bins(), is(2));
  }

  @Test
  public void autoResize() {
    BigLongs b = new BigLongs(0);
    for (long i = 0; i < 10 * BIN_SIZE; i++) b.set(i, 271L * i);
    assertThat(b.size(), is(10 * BIN_SIZE));
    for (long i = 0; i < 10 * BIN_SIZE; i++) assertThat(b.get(i), is(271L * i));
    assertThat(b.bins(), is(16));
  }

  @Test
  public void shrinkResize() {
    BigLongs b = new BigLongs(10L * BIN_SIZE);
    for (long i = 0; i < 10 * BIN_SIZE; i++) b.set(i, 271L * i);
    b.resize(4 * BIN_SIZE);
    assertThat(b.size(), is(4 * BIN_SIZE));
    assertThat(b.bins(), is(4));
    for (long i = 0; i < 4 * BIN_SIZE; i++) assertThat(b.get(i), is(271L * i));
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

  @Test
  public void sort() {
    BigLongs sorted = BigLongs.of(9L, 8L, 6L, 7L, 3L, 5L, 4L, 2L, 0L, 1L).sort();
    assertThat(sorted, is(BigLongs.of(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)));
  }

  @Test
  public void stringValue() {
    assertThat(BigLongs.of(271L, 314L).toString(), is("271, 314"));
  }

}