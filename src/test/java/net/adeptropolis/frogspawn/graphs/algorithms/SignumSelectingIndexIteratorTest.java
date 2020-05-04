/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SignumSelectingIndexIteratorTest {

  @Test
  public void emptyIterator() {
    assertThat(new SignumSelectingIndexIterator(new double[]{}, 1, null).hasNext(), is(false));
    assertThat(new SignumSelectingIndexIterator(new double[]{}, -1, null).hasNext(), is(false));
  }

  @Test
  public void singleElementPositiveIterator() {
    double[] v2 = new double[]{10};
    SignumSelectingIndexIterator posIt = new SignumSelectingIndexIterator(v2, 1, null);
    assertThat(posIt.hasNext(), is(true));
    assertThat(posIt.nextInt(), is(0));
    assertThat(posIt.hasNext(), is(false));
    SignumSelectingIndexIterator negIt = new SignumSelectingIndexIterator(v2, -1, null);
    assertThat(negIt.hasNext(), is(false));
  }

  @Test
  public void singleElementNegativeIterator() {
    double[] v2 = new double[]{-10};
    SignumSelectingIndexIterator negIt = new SignumSelectingIndexIterator(v2, -1, null);
    assertThat(negIt.hasNext(), is(true));
    assertThat(negIt.nextInt(), is(0));
    assertThat(negIt.hasNext(), is(false));
    SignumSelectingIndexIterator posIt = new SignumSelectingIndexIterator(v2, 1, null);
    assertThat(posIt.hasNext(), is(false));
  }

  @Test
  public void multipleElements() {
    double[] v2 = new double[]{2, -3};
    SignumSelectingIndexIterator posIt = new SignumSelectingIndexIterator(v2, 1, null);
    assertThat(posIt.hasNext(), is(true));
    assertThat(posIt.nextInt(), is(0));
    assertThat(posIt.hasNext(), is(false));
    SignumSelectingIndexIterator negIt = new SignumSelectingIndexIterator(v2, -1, null);
    assertThat(negIt.hasNext(), is(true));
    assertThat(negIt.nextInt(), is(1));
    assertThat(negIt.hasNext(), is(false));
  }

  @Test
  public void customPredicate() {
    double[] v2 = new double[]{2, -3};
    SignumSelectingIndexIterator posIt = new SignumSelectingIndexIterator(v2, 1, i -> i == 0);
    assertThat(posIt.hasNext(), is(true));
    assertThat(posIt.nextInt(), is(0));
    assertThat(posIt.hasNext(), is(false));
    SignumSelectingIndexIterator negIt = new SignumSelectingIndexIterator(v2, -1, i -> i == 0);
    assertThat(negIt.hasNext(), is(true));
    assertThat(negIt.nextInt(), is(0));
    assertThat(negIt.hasNext(), is(true));
    assertThat(negIt.nextInt(), is(1));
    assertThat(negIt.hasNext(), is(false));
  }

}