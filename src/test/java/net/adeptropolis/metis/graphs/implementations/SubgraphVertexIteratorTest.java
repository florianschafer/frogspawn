/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.implementations;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SubgraphVertexIteratorTest {

  private static void verifyIterator(SubgraphVertexIterator iterator, int... expected) {
    for (int i = 0; i < expected.length; i++) {
      assertThat("Premature exhaustion", iterator.hasNext(), is(true));
      assertThat("Index mismatch", iterator.localId(), is(i));
      assertThat("id mismatch", iterator.globalId(), is(expected[i]));
    }
    assertThat("Iterator should have been exhausted", iterator.hasNext(), is(false));
  }

  @Test
  public void emptyIterator() {
    SubgraphVertexIterator iterator = new SubgraphVertexIterator();
    assertThat(iterator.hasNext(), is(false));
  }

  @Test
  public void singletonIterator() {
    int[] buf = new int[]{42};
    SubgraphVertexIterator iterator = new SubgraphVertexIterator().reset(buf);
    verifyIterator(iterator, 42);
  }

  @Test
  public void verify() {
    int[] buf = new int[]{2, 3, 5, 7, 11, 13, 17, 19};
    SubgraphVertexIterator iterator = new SubgraphVertexIterator().reset(buf);
    verifyIterator(iterator, buf);
  }

  @Test
  public void resetWorks() {
    int[] buf = new int[]{23, 29, 31, 37, 41};
    SubgraphVertexIterator iterator = new SubgraphVertexIterator();
    verifyIterator(iterator.reset(buf), buf);
    int[] anotherBuf = new int[]{43, 47, 53, 59, 61, 67};
    verifyIterator(iterator.reset(anotherBuf), anotherBuf);
  }

}