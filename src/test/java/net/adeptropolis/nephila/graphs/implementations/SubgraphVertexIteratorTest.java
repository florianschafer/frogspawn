package net.adeptropolis.nephila.graphs.implementations;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

public class SubgraphVertexIteratorTest {

  @Test
  public void emptyIterator() {
    SubgraphVertexIterator iterator = new SubgraphVertexIterator();
    assertThat(iterator.proceed(), is(false));
  }

  @Test
  public void singletonIterator() {
    int[] buf = new int[]{ 42 };
    SubgraphVertexIterator iterator = new SubgraphVertexIterator().reset(buf, buf.length);
    verifyIterator(iterator, 42);
  }

  @Test
  public void sizeIsObeyed() {
    int[] buf = new int[] { 271, 314, 581};
    SubgraphVertexIterator iterator = new SubgraphVertexIterator().reset(buf, 2);
    verifyIterator(iterator, 271, 314);
  }

  @Test
  public void fullSize() {
    int[] buf = new int[] { 2, 3, 5, 7, 11, 13, 17, 19 };
    SubgraphVertexIterator iterator = new SubgraphVertexIterator().reset(buf, buf.length);
    verifyIterator(iterator, buf);
  }

  @Test
  public void resetWorks() {
    int[] buf = new int[] { 23, 29, 31, 37, 41 };
    SubgraphVertexIterator iterator = new SubgraphVertexIterator();
    verifyIterator(iterator.reset(buf, buf.length), buf);
    int[] anotherBuf = new int[] { 43, 47, 53, 59, 61, 67 };
    verifyIterator(iterator.reset(anotherBuf, anotherBuf.length), anotherBuf);
  }

  private static void verifyIterator(SubgraphVertexIterator iterator, int... expected) {
    for (int i = 0; i < expected.length; i++) {
      assertThat("Premature exhaustion", iterator.proceed(), is(true));
      assertThat("Index mismatch", iterator.localId(), is(i));
      assertThat("id mismatch", iterator.globalId(), is(expected[i]));
    }
    assertThat("Iterator should have been exhausted", iterator.proceed(), is(false));
  }

}