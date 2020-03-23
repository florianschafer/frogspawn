/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static net.adeptropolis.metis.graphs.traversal.ParallelOps.PARALLELIZATION_THRESHOLD;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class NormalizedCutTest extends GraphTestBase {

  @Test
  public void trivialSubgraph() {
    Graph graph = completeGraph(20);
    double nCut = NormalizedCut.compute(graph, graph);
    assertThat(nCut, closeTo(0, 1E-6));
  }

  @Test
  public void completeGraphSequential() {
    Graph graph = completeGraph(4);
    Graph subgraph = graph.inducedSubgraph(IntIterators.fromTo(0, 2));
    double nCut = NormalizedCut.compute(subgraph, graph);
    assertThat(nCut, closeTo(8.0 / 5.0, 1E-6));
  }

  @Test
  public void completeGraphParallel() {
    int size = 2 * (PARALLELIZATION_THRESHOLD >> 1) + 2;
    Graph graph = completeGraph(size);
    Graph subgraph = graph.inducedSubgraph(IntIterators.fromTo(0, size >> 1));
    double nCut = NormalizedCut.compute(subgraph, graph);
    assertThat(nCut, closeTo(1.3342210386151798, 1E-6));
  }

  @Test
  public void unevenCutOfWeightedGraphs() {
    Graph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 2)
            .add(0, 3, 3)
            .add(1, 2, 5)
            .add(2, 3, 7)
            .add(3, 4, 11)
            .build();
    Graph subgraph = graph.inducedSubgraph(IntIterators.wrap(new int[]{0, 1}));
    double nCut = NormalizedCut.compute(subgraph, graph);
    assertThat(nCut, closeTo(8.0 / 10 + 8.0 / 26, 1E-6));
  }


}