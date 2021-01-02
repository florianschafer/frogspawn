/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.filters;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DegreeFilterTest {

  private static final SparseGraph graph = new SparseGraphBuilder()
          .add(0, 1, 1)
          .add(1, 2, 1)
          .add(2, 0, 1)
          .add(2, 3, 1)
          .add(3, 4, 1)
          .build();

  @Test
  public void minSingleApplication() {
    Graph filtered = new DegreeFilter(2, 0).apply(graph);
    assertThat(filtered.order(), is(4));
    IntIterator it = filtered.globalVertexIdIterator();
    assertThat(it.nextInt(), is(0));
    assertThat(it.nextInt(), is(1));
    assertThat(it.nextInt(), is(2));
    assertThat(it.nextInt(), is(3));
    assertThat(it.hasNext(), is(false));
  }

  @Test
  public void minCascading() {
    Graph filtered = new DegreeFilter(2, 0).applyIteratively(graph);
    assertThat(filtered.order(), is(3));
    IntIterator it = filtered.globalVertexIdIterator();
    assertThat(it.nextInt(), is(0));
    assertThat(it.nextInt(), is(1));
    assertThat(it.nextInt(), is(2));
    assertThat(it.hasNext(), is(false));
  }

  @Test
  public void max() {
    Graph filtered = new DegreeFilter(2, 2).apply(graph);
    assertThat(filtered.order(), is(3));
    IntIterator it = filtered.globalVertexIdIterator();
    assertThat(it.nextInt(), is(0));
    assertThat(it.nextInt(), is(1));
    assertThat(it.nextInt(), is(3));
    assertThat(it.hasNext(), is(false));
  }


}