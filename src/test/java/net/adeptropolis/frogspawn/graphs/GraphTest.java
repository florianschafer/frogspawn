/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;
import org.junit.Test;

import java.util.Arrays;

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

}