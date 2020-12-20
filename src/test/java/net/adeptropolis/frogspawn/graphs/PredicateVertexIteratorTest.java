/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PredicateVertexIteratorTest extends GraphTestBase {

  private static final Graph GRAPH = subgraph(completeGraph(10), 1, 3, 5, 7, 9);

  @Test
  public void allFalse() {
    assertThat(new PredicateVertexIterator(GRAPH, (i) -> false).hasNext(), is(false));
  }

  @Test
  public void allTrue() {
    PredicateVertexIterator it = new PredicateVertexIterator(GRAPH, (i) -> true);
    assertThat(it.hasNext(), is(true));
    assertThat(it.nextInt(), is(1));
    assertThat(it.hasNext(), is(true));
    assertThat(it.nextInt(), is(3));
    assertThat(it.hasNext(), is(true));
    assertThat(it.nextInt(), is(5));
    assertThat(it.hasNext(), is(true));
    assertThat(it.nextInt(), is(7));
    assertThat(it.hasNext(), is(true));
    assertThat(it.nextInt(), is(9));
    assertThat(it.hasNext(), is(false));
  }

  @Test
  public void evenIndices() {
    PredicateVertexIterator it = new PredicateVertexIterator(GRAPH, (i) -> i % 2 == 0);
    assertThat(it.hasNext(), is(true));
    assertThat(it.nextInt(), is(1));
    assertThat(it.hasNext(), is(true));
    assertThat(it.nextInt(), is(5));
    assertThat(it.hasNext(), is(true));
    assertThat(it.nextInt(), is(9));
    assertThat(it.hasNext(), is(false));
  }

  @Test
  public void emptyGraph() {
    assertThat(new PredicateVertexIterator(new SparseGraphBuilder().build(), i -> true).hasNext(), is(false));
  }

}