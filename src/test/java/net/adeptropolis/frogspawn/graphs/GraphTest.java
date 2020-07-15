/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class GraphTest {

  @Test
  public void weights() {
    SparseGraph graph = new SparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .build();
    assertThat(graph.weights().length, is(3));
    assertThat(graph.weights()[0], closeTo(8, 1E-9));
    assertThat(graph.weights()[1], closeTo(7, 1E-9));
    assertThat(graph.weights()[2], closeTo(9, 1E-9));

  }

  @Test
  public void totalWeight() {
    SparseGraph graph = new SparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .build();
    assertThat(graph.totalWeight(), closeTo(24, 1E-6));
  }

  @Test
  public void contains() {
    Graph graph = new SparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .build()
            .subgraph(IntIterators.wrap(new int[]{0, 1, 2}));
    assertThat(graph.containsVertex(0), is(true));
    assertThat(graph.containsVertex(1), is(true));
    assertThat(graph.containsVertex(2), is(true));
    assertThat(graph.containsVertex(3), is(false));
  }

  @Test
  public void weightForGlobalId() {
    Graph graph = new SparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 3, 5)
            .build()
            .subgraph(IntIterators.wrap(new int[]{1, 2, 3}));
    assertThat(graph.weightForGlobalId(1), closeTo(4.0, 1E-6));
    assertThat(graph.weightForGlobalId(2), closeTo(9.0, 1E-6));
    assertThat(graph.weightForGlobalId(3), closeTo(5.0, 1E-6));
  }

}