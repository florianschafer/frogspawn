package net.adeptropolis.nephila.graphs.algorithms;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class SignumSelectingIndexIteratorTest {

  @Test
  public void emptyIterator() {
    assertFalse(new SignumSelectingIndexIterator(new double[]{}, 1).hasNext());
    assertFalse(new SignumSelectingIndexIterator(new double[]{}, -1).hasNext());
  }

  @Test
  public void singleElementPositiveIterator() {
    double[] v2 = new double[]{10};
    SignumSelectingIndexIterator posIt = new SignumSelectingIndexIterator(v2, 1);
    assertTrue(posIt.hasNext());
    assertThat(posIt.nextInt(), is(0));
    assertFalse(posIt.hasNext());
    SignumSelectingIndexIterator negIt = new SignumSelectingIndexIterator(v2, -1);
    assertFalse(negIt.hasNext());
  }

  @Test
  public void singleElementNegativeIterator() {
    double[] v2 = new double[]{-10};
    SignumSelectingIndexIterator negIt = new SignumSelectingIndexIterator(v2, -1);
    assertTrue(negIt.hasNext());
    assertThat(negIt.nextInt(), is(0));
    assertFalse(negIt.hasNext());
    SignumSelectingIndexIterator posIt = new SignumSelectingIndexIterator(v2, 1);
    assertFalse(posIt.hasNext());
  }

  @Test
  public void multipleElements() {
    double[] v2 = new double[]{2, -3};
    SignumSelectingIndexIterator posIt = new SignumSelectingIndexIterator(v2, 1);
    assertTrue(posIt.hasNext());
    assertThat(posIt.nextInt(), is(0));
    assertFalse(posIt.hasNext());
    SignumSelectingIndexIterator negIt = new SignumSelectingIndexIterator(v2, -1);
    assertTrue(negIt.hasNext());
    assertThat(negIt.nextInt(), is(1));
    assertFalse(negIt.hasNext());
  }

}