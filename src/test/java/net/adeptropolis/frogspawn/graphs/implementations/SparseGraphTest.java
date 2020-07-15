/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.adeptropolis.frogspawn.graphs.Edge;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.VertexIterator;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static net.adeptropolis.frogspawn.graphs.implementations.SparseGraph.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SparseGraphTest extends GraphTestBase {

  private static SparseGraph defaultGraph = builder()
          .add(0, 1, 2)
          .add(1, 2, 3)
          .add(4, 9, 5)
          .add(4, 10, 7)
          .add(4, 11, 11)
          .build();

  @Test
  public void size() {
    assertThat(defaultGraph.order(), is(12));
  }

  @Test
  public void NumEdges() {
    assertThat(defaultGraph.size(), is(10L));
  }

  @Test
  public void vertices() {
    VertexIterator it = defaultGraph.vertexIterator();
    IntArrayList localIds = new IntArrayList();
    IntArrayList globalIds = new IntArrayList();
    while (it.hasNext()) {
      localIds.add(it.localId());
      globalIds.add(it.globalId());
    }
    assertThat(localIds, contains(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
    assertThat(globalIds, contains(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
  }

  @Test
  public void collectVertices() {
    int[] vertices = defaultGraph.collectVertices();
    assertThat(vertices.length, is(12));
    for (int i = 0; i < 12; i++) {
      assertThat(vertices[i], is(i));
    }
  }

  @Test
  public void globalVertexIdIterator() {
    IntList list = IntIterators.pour(defaultGraph.globalVertexIdIterator());
    assertThat(list, is(new IntArrayList(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})));
  }

  @Test
  public void emptyGraph() {
    SparseGraph graph = builder().build();
    assertThat(graph.order(), is(0));
    graph.traverseParallel(consumer);
    assertThat(consumer.getEdges().size(), is(0));
  }

  @Test
  public void fullTraversal() {
    SparseGraph graph = builder()
            .add(0, 1, 2)
            .add(1, 3, 3)
            .add(6, 4, 5)
            .build();
    graph.traverseParallel(consumer);
    assertThat(consumer.getEdges(), containsInAnyOrder(
            Edge.of(0, 1, 2),
            Edge.of(1, 0, 2),
            Edge.of(1, 3, 3),
            Edge.of(3, 1, 3),
            Edge.of(6, 4, 5),
            Edge.of(4, 6, 5)));
  }

  @Test
  public void indexMapping() {
    for (int i = 0; i < defaultGraph.order(); i++) {
      assertThat(defaultGraph.globalVertexId(i), is(i));
      assertThat(defaultGraph.localVertexId(i), is(i));

    }
  }

  @Test
  public void traverseById() {
    defaultGraph.traverseIncidentEdges(defaultGraph.localVertexId(4), consumer, TraversalMode.DEFAULT);
    assertThat(consumer.getEdges(), containsInAnyOrder(
            Edge.of(4, 9, 5),
            Edge.of(4, 10, 7),
            Edge.of(4, 11, 11)));
  }

  @Test
  public void traverseNonExistentId() {
    defaultGraph.traverseIncidentEdges(-1, consumer, TraversalMode.DEFAULT);
    assertThat(consumer.getEdges().size(), is(0));
  }

  @Test
  public void subgraph() {
    Graph subgraph = defaultGraph.subgraph(IntIterators.wrap(new int[]{4, 11}));
    assertThat(subgraph.order(), is(2));
  }

  @Test
  public void lowerTriangularTraversal() {
    SparseGraph graph = completeGraph(150);
    CollectingEdgeConsumer consumer = new CollectingEdgeConsumer();
    graph.traverseParallel(consumer, TraversalMode.LOWER_TRIANGULAR);
    assertThat(consumer.getEdges(), hasSize(75 * 149));
    Set<Edge> edges = new HashSet<>(consumer.getEdges());
    for (int i = 0; i < 150; i++) {
      for (int j = 0; j < i; j++) {
        assertThat(edges, hasItem(Edge.of(i, j, 1)));
      }
    }
  }

}