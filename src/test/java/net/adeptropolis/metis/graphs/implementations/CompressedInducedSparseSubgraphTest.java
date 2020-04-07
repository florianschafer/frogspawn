/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.adeptropolis.metis.graphs.Edge;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.GraphTestBase;
import net.adeptropolis.metis.graphs.VertexIterator;
import org.junit.Test;

import java.util.Arrays;

import static net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class CompressedInducedSparseSubgraphTest extends GraphTestBase {

  private static CompressedSparseGraph defaultGraph = builder()
          .add(0, 1, 2)
          .add(1, 2, 3)
          .add(4, 9, 5)
          .add(4, 10, 7)
          .add(4, 11, 11)
          .build();

  private static Graph subgraph(Graph graph, int... vertices) {
    Arrays.sort(vertices);
    return graph.inducedSubgraph(IntIterators.wrap(vertices));
  }

  private static Graph defaultSubgraph(int... vertices) {
    return subgraph(defaultGraph, vertices);
  }

  @Test
  public void isDefaultSubgraph() {
    assertThat(defaultSubgraph(0, 1), instanceOf(CompressedInducedSparseSubgraph.class));
  }

  @Test
  public void size() {
    assertThat(defaultGraph.order(), is(12));
  }

  @Test
  public void numOrderedEdges() {
    Graph graph = builder()
            .add(0, 1, 2)
            .add(1, 2, 3)
            .add(1, 4, 5)
            .add(1, 5, 7)
            .add(1, 11, 11)
            .build();
    Graph sub = subgraph(graph, 1, 2, 5);
    assertThat(sub.size(), is(4L));
  }

  @Test
  public void vertices() {
    VertexIterator it = defaultSubgraph(4, 9, 10).vertexIterator();
    IntArrayList localIds = new IntArrayList();
    IntArrayList globalIds = new IntArrayList();
    while (it.hasNext()) {
      localIds.add(it.localId());
      globalIds.add(it.globalId());
    }
    assertThat(localIds, contains(0, 1, 2));
    assertThat(globalIds, contains(4, 9, 10));
  }

  @Test
  public void collectVertices() {
    int[] vertices = defaultSubgraph(4, 9, 10).collectVertices();
    assertThat(vertices.length, is(3));
    assertThat(vertices[0], is(4));
    assertThat(vertices[1], is(9));
    assertThat(vertices[2], is(10));
  }

  @Test
  public void globalVertexIdIterator() {
    IntList list = IntIterators.pour(defaultSubgraph(4, 9, 10).globalVertexIdIterator());
    assertThat(list, is(new IntArrayList(new int[]{4, 9, 10})));
  }

  @Test
  public void emptyGraph() {
    Graph graph = defaultSubgraph();
    assertThat(graph.order(), is(0));
    graph.traverseParallel(consumer);
    assertThat(consumer.getEdges().size(), is(0));
  }

  @Test
  public void fullTraversal() {
    defaultSubgraph(1, 2, 4, 9, 10).traverseParallel(consumer);
    assertThat(consumer.getEdges(), containsInAnyOrder(
            Edge.of(0, 1, 3),
            Edge.of(1, 0, 3),
            Edge.of(2, 3, 5),
            Edge.of(3, 2, 5),
            Edge.of(2, 4, 7),
            Edge.of(4, 2, 7)));
  }

  @Test
  public void traverseBAdjacent() {
    Graph graph = defaultSubgraph(1, 2, 4, 9, 11);
    graph.traverseIncidentEdges(graph.localVertexId(4), consumer);
    assertThat(consumer.getEdges(), containsInAnyOrder(
            Edge.of(graph.localVertexId(4), graph.localVertexId(9), 5),
            Edge.of(graph.localVertexId(4), graph.localVertexId(11), 11)
    ));
  }

  @Test
  public void traverseByVertices() {
    Graph graph = builder()
            .add(0, 1, 2)
            .add(1, 2, 3)
            .add(1, 4, 5)
            .add(1, 5, 7)
            .add(1, 11, 11)
            .build();
    Graph sub = subgraph(graph, 1, 2, 5);
    sub.traverseIncidentEdges(sub.localVertexId(1), consumer);
    assertThat(consumer.getEdges(), containsInAnyOrder(
            Edge.of(sub.localVertexId(1), sub.localVertexId(2), 3),
            Edge.of(sub.localVertexId(1), sub.localVertexId(5), 7)
    ));
  }

  @Test
  public void traverseUndef() {
    defaultSubgraph(0, 1).traverseIncidentEdges(-1, consumer);
    assertThat(consumer.getEdges().size(), is(0));
  }

  @Test
  public void traverseEmpty() {
    Graph graph = defaultSubgraph(0, 1, 2, 3);
    graph.traverseIncidentEdges(3, consumer);
    assertThat(consumer.getEdges().size(), is(0));
  }

  @Test
  public void subgraph() {
    Graph subgraph = defaultSubgraph(4, 10, 11).inducedSubgraph(IntIterators.wrap(new int[]{4, 10}));
    assertThat("Subgraph size", subgraph.order(), is(2));
    subgraph.traverseParallel(consumer);
    assertThat("Subgraph edges", consumer.getEdges(), containsInAnyOrder(
            Edge.of(subgraph.localVertexId(4), subgraph.localVertexId(10), 7),
            Edge.of(subgraph.localVertexId(10), subgraph.localVertexId(4), 7)));


    VertexIterator it = subgraph.vertexIterator();
    assertThat("Subgraph vertex 4", it.hasNext(), is(true));
    assertThat(it.localId(), equalTo(0));
    assertThat(it.globalId(), equalTo(4));
    assertThat("Subgraph vertex 10", it.hasNext(), is(true));
    assertThat(it.localId(), equalTo(1));
    assertThat(it.globalId(), equalTo(10));

  }

}