/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.VertexIterator;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SparseSubgraphVertexIteratorTest extends GraphTestBase {

  private static void verifyIterator(VertexIterator iterator, int... expected) {
    for (int i = 0; i < expected.length; i++) {
      assertThat("Premature exhaustion", iterator.hasNext(), is(true));
      assertThat("Index mismatch", iterator.localId(), is(i));
      assertThat("id mismatch", iterator.globalId(), is(expected[i]));
    }
    assertThat("Iterator should have been exhausted", iterator.hasNext(), is(false));
  }

  @Test
  public void emptyIterator() {
    VertexIterator iterator = subgraph(completeGraph(5)).vertexIterator();
    assertThat("Iterator is expected to be exhausted", iterator.hasNext(), is(false));
  }

  @Test
  public void singletonIterator() {
    VertexIterator iterator = subgraph(completeGraph(5), 3).vertexIterator();
    verifyIterator(iterator, 3);
  }

  @Test
  public void verify() {
    int[] vertices = {2, 3, 5, 7, 11, 13, 17, 19};
    VertexIterator iterator = subgraph(completeGraph(30), vertices).vertexIterator();
    verifyIterator(iterator, vertices);
  }

}