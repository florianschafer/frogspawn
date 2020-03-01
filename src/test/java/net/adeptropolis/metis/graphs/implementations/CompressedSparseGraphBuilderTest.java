/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.implementations;

import org.junit.Test;

import static net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph.builder;
import static net.adeptropolis.metis.graphs.implementations.arrays.Helpers.assertEquals;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CompressedSparseGraphBuilderTest {

  private static void assertGraphSizeMatches(CompressedSparseGraphDatastore datastore, int expected) {
    assertThat("Graph size", datastore.size(), is(expected));
  }

  private static void assertEdgeCountMatches(CompressedSparseGraphDatastore datastore, long expected) {
    assertThat("Number of edges", datastore.edgeCount(), is(expected));
  }

  private static void assertEdgesMatch(CompressedSparseGraphDatastore datastore, int... expected) {
    assertEquals("EdgeOps", datastore.edges, expected);
  }

  private static void assertPointersMatch(CompressedSparseGraphDatastore datastore, long... expected) {
    assertEquals("Vertex pointers", datastore.pointers, expected);
  }

  private static void assertWeightsMatch(CompressedSparseGraphDatastore datastore, double... expected) {
    assertEquals("Weights", datastore.weights, expected);
  }

  @Test
  public void emptyGraph() {
    CompressedSparseGraphDatastore datastore = builder().buildDatastore();
    assertGraphSizeMatches(datastore, 0);
    assertEdgeCountMatches(datastore, 0L);
  }

  @Test
  public void loopsCountAsSingleEdges() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(0, 0, 1)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 1);
    assertEdgeCountMatches(datastore, 1L);
  }

  @Test
  public void simpleReduce() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(0, 0, 2)
            .add(0, 0, 3)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 1);
    assertEdgeCountMatches(datastore, 1L);
    assertWeightsMatch(datastore, 5);
  }

  @Test
  public void entrySorting() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(1, 3, 11)
            .add(1, 2, 7)
            .add(0, 6, 2)
            .add(0, 5, 3)
            .add(0, 4, 5)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 7);
    assertEdgeCountMatches(datastore, 10L);
    assertPointersMatch(datastore, 0L, 3L, 5L, 6L, 7L, 8L, 9L, 10L);
    assertEdgesMatch(datastore, 4, 5, 6, 2, 3, 1, 1, 0, 0, 0);
    assertWeightsMatch(datastore, 5, 3, 2, 7, 11, 7, 11, 5, 3, 2);
  }

  @Test
  public void trivialReduce() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(0, 0, 1)
            .add(0, 0, 3)
            .add(0, 0, 5)
            .add(0, 0, 7)
            .add(0, 0, 11)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 1);
    assertEdgeCountMatches(datastore, 1L);
    assertPointersMatch(datastore, 0L, 1L);
    assertEdgesMatch(datastore, 0);
    assertWeightsMatch(datastore, 27);
  }

  @Test
  public void reduceHead() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(0, 0, 1)
            .add(0, 0, 3)
            .add(1, 1, 5)
            .add(2, 2, 7)
            .add(2, 2, 11)
            .add(3, 3, 13)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 4);
    assertEdgeCountMatches(datastore, 4L);
    assertPointersMatch(datastore, 0L, 1L, 2L, 3L, 4L);
    assertEdgesMatch(datastore, 0, 1, 2, 3);
    assertWeightsMatch(datastore, 4, 5, 18, 13);
  }

  @Test
  public void reduceTail() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(0, 0, 1)
            .add(1, 1, 3)
            .add(2, 2, 5)
            .add(2, 2, 7)
            .add(3, 3, 11)
            .add(3, 3, 13)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 4);
    assertEdgeCountMatches(datastore, 4L);
    assertPointersMatch(datastore, 0L, 1L, 2L, 3L, 4L);
    assertEdgesMatch(datastore, 0, 1, 2, 3);
    assertWeightsMatch(datastore, 1, 3, 12, 24);
  }

  @Test
  public void emptyRows() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(1, 1, 1)
            .add(2, 2, 3)
            .add(2, 2, 5)
            .add(4, 4, 7)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 5);
    assertEdgeCountMatches(datastore, 3L);
    assertPointersMatch(datastore, 0L, 0L, 1L, 2L, 2L, 3L);
    assertEdgesMatch(datastore, 1, 2, 4);
    assertWeightsMatch(datastore, 1, 8, 7);
  }

  @Test
  public void emptyFirstRow() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(1, 1, 1)
            .add(1, 2, 2)
            .add(1, 3, 3)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 4);
    assertEdgeCountMatches(datastore, 5L);
    assertPointersMatch(datastore, 0L, 0L, 3L, 4L, 5L);
    assertEdgesMatch(datastore, 1, 2, 3, 1, 1);
    assertWeightsMatch(datastore, 1, 2, 3, 2, 3);
  }

  @Test
  public void compactness() {
    CompressedSparseGraphDatastore datastore = builder()
            .add(0, 1, 2)
            .add(1, 2, 3)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 3);
    assertEdgeCountMatches(datastore, 4L);
    assertThat("Pointer compactness", datastore.pointers.length, is(4));
    assertThat("EdgeOps compactness", datastore.edges.size(), is(4L));
    assertThat("Weights compactness", datastore.weights.size(), is(4L));
  }

  @Test
  public void build() {
    CompressedSparseGraph graph = builder()
            .add(0, 1, 2)
            .add(1, 2, 3)
            .build();
    assertThat(graph, instanceOf(CompressedSparseGraph.class));
    assertThat(graph.order(), is(3));
  }

}