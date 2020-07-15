/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import org.junit.Test;

import static net.adeptropolis.frogspawn.graphs.implementations.SparseGraph.builder;
import static net.adeptropolis.frogspawn.graphs.implementations.arrays.Helpers.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class SparseGraphBuilderTest {

  private static void assertGraphSizeMatches(CSRDatastore datastore, int expected) {
    assertThat("Graph size", datastore.order(), is(expected));
  }

  private static void assertEdgeCountMatches(CSRDatastore datastore, long expected) {
    assertThat("Number of edges", datastore.size(), is(expected));
  }

  private static void assertEdgesMatch(CSRDatastore datastore, int... expected) {
    assertEquals("EdgeOps", datastore.edges, expected);
  }

  private static void assertPointersMatch(CSRDatastore datastore, long... expected) {
    assertEquals("Vertex pointers", datastore.pointers, expected);
  }

  private static void assertWeightsMatch(CSRDatastore datastore, double... expected) {
    assertEquals("Weights", datastore.weights, expected);
  }

  @Test
  public void emptyGraph() {
    CSRDatastore datastore = builder().buildDatastore();
    assertGraphSizeMatches(datastore, 0);
    assertEdgeCountMatches(datastore, 0L);
  }

  @Test
  public void loopsCountAsSingleEdges() {
    CSRDatastore datastore = builder()
            .add(0, 0, 1)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 1);
    assertEdgeCountMatches(datastore, 1L);
  }

  @Test
  public void simpleReduce() {
    CSRDatastore datastore = builder()
            .add(0, 0, 2)
            .add(0, 0, 3)
            .buildDatastore();
    assertGraphSizeMatches(datastore, 1);
    assertEdgeCountMatches(datastore, 1L);
    assertWeightsMatch(datastore, 5);
  }

  @Test
  public void entrySorting() {
    CSRDatastore datastore = builder()
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
    CSRDatastore datastore = builder()
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
    CSRDatastore datastore = builder()
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
    CSRDatastore datastore = builder()
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
    CSRDatastore datastore = builder()
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
    CSRDatastore datastore = builder()
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
    CSRDatastore datastore = builder()
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
    SparseGraph graph = builder()
            .add(0, 1, 2)
            .add(1, 2, 3)
            .build();
    assertThat(graph, instanceOf(SparseGraph.class));
    assertThat(graph.order(), is(3));
  }

  @Test
  public void edgeWeightBelowMinThrows() {
    assertThrows(GraphConstructionException.class, () -> {
      builder().add(0, 1, 1.0 - 1E-9);
    });
  }

}