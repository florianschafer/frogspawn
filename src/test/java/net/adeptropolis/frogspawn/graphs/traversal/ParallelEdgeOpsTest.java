/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.traversal;

import net.adeptropolis.frogspawn.graphs.Edge;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.implementations.CompressedSparseGraph;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ParallelEdgeOpsTest extends GraphTestBase {

  @Test
  @Ignore("Intended for performance debugging")
  @SuppressWarnings("squid:S2699")
  public void perfTest() {
    Graph graph = bandedGraph(120000, 20);
    while (true) {
      traverseFingerprint(graph);
    }
  }

  @Test
  public void emptyGraph() {
    CompressedSparseGraph graph = CompressedSparseGraph.builder().build();
    ParallelEdgeOps.traverse(graph, consumer);
    assertThat(consumer.getEdges(), is(empty()));
  }

  @Test
  public void singleEdgeGraph() {
    CompressedSparseGraph graph = CompressedSparseGraph.builder()
            .add(2, 3, 3.14)
            .build();
    ParallelEdgeOps.traverse(graph, consumer);
    assertThat(consumer.getEdges(), hasSize(2));
    MatcherAssert.assertThat(consumer.getEdges(), Matchers.containsInAnyOrder(
            Edge.of(2, 3, 3.14),
            Edge.of(3, 2, 3.14)));
  }

  @Test
  public void largeBandedGraph() {
    Graph graph = bandedGraph(20000, 100);
    assertThat("Fingerprint mismatch", traverseFingerprint(graph), is(bandedGraphFingerprint(20000, 100)));
  }

}

