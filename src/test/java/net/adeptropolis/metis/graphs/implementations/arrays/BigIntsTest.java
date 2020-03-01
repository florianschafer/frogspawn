/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.implementations.arrays;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class BigIntsTest {

  private static final long BIN_SIZE = (1 << BigInts.BIN_BITS);

  @Test
  public void basicFunctions() {
    BigInts b = new BigInts(BIN_SIZE);
    assertThat(b.size(), is(0L));
    for (long i = 0; i < BIN_SIZE; i++) b.set(i, (int) (3 * i));
    for (long i = 0; i < BIN_SIZE; i++) assertThat(b.get(i), is((int) (3 * i)));
    assertThat(b.size(), is(BIN_SIZE));
  }

  @Test
  public void resize() {
    BigInts b = new BigInts(0);
    b.resize(2 * BIN_SIZE);
    assertThat(b.bins(), is(2));
  }

  @Test
  public void autoResize() {
    BigInts b = new BigInts(0);
    for (int i = 0; i < 10 * BIN_SIZE; i++) b.set(i, 271 * i);
    assertThat(b.size(), is(10 * BIN_SIZE));
    for (int i = 0; i < 10 * BIN_SIZE; i++) assertThat(b.get(i), is(271 * i));
    assertThat(b.bins(), is(16));
  }

  @Test
  public void shrinkResize() {
    BigInts b = new BigInts(10 * BIN_SIZE);
    for (int i = 0; i < 10 * BIN_SIZE; i++) b.set(i, 271 * i);
    b.resize(4 * BIN_SIZE);
    assertThat(b.size(), is(4 * BIN_SIZE));
    assertThat(b.bins(), is(4));
    for (int i = 0; i < 4 * BIN_SIZE; i++) assertThat(b.get(i), is(271 * i));
  }

  @Test
  public void equals() {
    BigInts b = new BigInts(29);
    b.set(0, 3);
    b.set(1, 1);
    b.set(2, 4);
    assertThat(b, is(BigInts.of(3, 1, 4)));
    assertThat(b, is(not(BigInts.of(3, 1, 5))));
    assertThat(b, is(not(BigInts.of(3))));
  }

  @Test
  public void sort() {
    BigInts sorted = BigInts.of(9, 8, 6, 7, 3, 5, 4, 2, 0, 1).sort();
    assertThat(sorted, is(BigInts.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
  }

  @Test
  public void stringValue() {
    assertThat(BigInts.of(271, 314).toString(), is("271, 314"));
  }

}