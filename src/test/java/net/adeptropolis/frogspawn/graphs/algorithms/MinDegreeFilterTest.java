/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.*;

public class MinDegreeFilterTest {

  @Test
  public void cascading() {

    SparseGraph graph = new SparseGraphBuilder()
            .add(0, 1, 1)
            .add(1, 2, 1)
            .add(2, 0, 1)
            .add(2, 3, 1)
            .add(3, 4, 1)
            .build();

    Graph filtered = MinDegreeFilter.apply(graph, 2);
    assertThat(filtered.order(), is(3));
    IntIterator it = filtered.globalVertexIdIterator();
    assertThat(it.nextInt(), is(0));
    assertThat(it.nextInt(), is(1));
    assertThat(it.nextInt(), is(2));
    assertThat(it.hasNext(), is(false));

  }

}