/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.filters.DegreeFilter;
import net.adeptropolis.frogspawn.graphs.filters.GraphFilter;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GraphTest extends GraphTestBase {

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
  public void degrees() {
    SparseGraph graph = new SparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .add(3, 0, 6)
            .build();
    assertThat(graph.degrees().length, is(4));
    assertThat(graph.degrees()[0], is(3L));
    assertThat(graph.degrees()[1], is(2L));
    assertThat(graph.degrees()[2], is(2L));
    assertThat(graph.degrees()[3], is(1L));
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

  @Test
  public void degreeForGlobalId() {
    Graph graph = new SparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .add(3, 0, 6)
            .build()
            .subgraph(IntIterators.wrap(new int[]{1, 2, 3}));
    assertThat(graph.degreeForGlobalId(1), is(1L));
    assertThat(graph.degreeForGlobalId(2), is(1L));
    assertThat(graph.degreeForGlobalId(3), is(0L));
  }

  @Test
  public void subgraphFromPredicate() {
    int[] filteredGlobalIds = subgraph(completeGraph(10), 1, 3, 5, 7, 9)
            .subgraph(i -> i % 2 == 0)
            .collectVertices();
    assertThat(filteredGlobalIds, is(new int[]{1, 5, 9}));
  }

  @Test
  public void sequentialTraversal() {
    CollectingEdgeConsumer consumer = new CollectingEdgeConsumer();
    completeGraph(4).traverse(consumer, TraversalMode.LOWER_TRIANGULAR);
    assertThat(consumer.getEdges(), containsInAnyOrder(
            Edge.of(1, 0, 1),
            Edge.of(2, 0, 1),
            Edge.of(3, 0, 1),
            Edge.of(2, 1, 1),
            Edge.of(3, 1, 1),
            Edge.of(3, 2, 1)));
  }

  @Test
  public void sequentialStandardTraversal() {
    CollectingEdgeConsumer consumer = new CollectingEdgeConsumer();
    completeGraph(2).traverse(consumer);
    assertThat(consumer.getEdges(), containsInAnyOrder(Edge.of(1, 0, 1), Edge.of(0, 1, 1)));
  }

  @Test
  public void filters() {
    SparseGraph graph = new SparseGraphBuilder()
            .add(0, 1, 1)
            .add(1, 2, 1)
            .add(2, 0, 1)
            .add(2, 3, 1)
            .add(3, 4, 1)
            .build();
    GraphFilter filter = new DegreeFilter(2, 0);
    assertThat(graph.filter(filter, false).order(), is(4));
    assertThat(graph.filter(filter, true).order(), is(3));
  }

}