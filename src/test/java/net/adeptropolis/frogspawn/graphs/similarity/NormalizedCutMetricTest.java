/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.similarity;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.implementations.CompressedSparseGraph;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class NormalizedCutMetricTest extends GraphTestBase {

  private static final NormalizedCutMetric metric = new NormalizedCutMetric();

  @Test
  public void ncutIsCorrect() {
    Graph supergraph = CompressedSparseGraph.builder()
            .add(0, 1, 2)
            .add(0, 2, 5)
            .add(0, 4, 3)
            .add(1, 2, 11)
            .add(1, 3, 7)
            .add(2, 3, 13)
            .add(2, 4, 17)
            .add(3, 4, 23)
            .build();
    Graph subgraph = supergraph.inducedSubgraph(IntIterators.wrap(new int[]{2, 3, 4}));
    double ncut = metric.compute(supergraph, subgraph);
    assertThat(ncut, closeTo((26d / 79d + 26d / 28d) / 2, 1E-9));
  }

  @Test
  public void nCutWithEmptySubgraphIsZero() {
    Graph graph = completeGraph(32);
    Graph subgraph = graph.inducedSubgraph(IntIterators.EMPTY_ITERATOR);
    assertThat(metric.compute(graph, subgraph), closeTo(0d, 1E-9));
  }

  @Test
  public void nCutOfGraphWithSelfIsZero() {
    Graph graph = completeGraph(32);
    assertThat(metric.compute(graph, graph), closeTo(0d, 1E-9));
  }

  @Test
  public void ncutWithConnectedComponentIsZero() {
    Graph graph = CompressedSparseGraph.builder()
            .add(0, 1, 1)
            .add(1, 2, 1)
            .add(2, 0, 1)
            .add(3, 4, 1)
            .add(4, 5, 1)
            .add(5, 3, 1)
            .build();
    Graph subgraph = graph.inducedSubgraph(IntIterators.fromTo(3, 6));
    assertThat(metric.compute(graph, subgraph), closeTo(0d, 1E-9));
  }

  @Test
  public void largerParallelCompleteGraph() {
    int size = 129;
    int subgraphSize = 42;
    Graph graph = completeGraph(size);
    Graph subgraph = graph.inducedSubgraph(IntIterators.fromTo(0, subgraphSize));
    double expectedCut = subgraphSize * (size - subgraphSize);
    double expectedSubgraphWeight = expectedCut + (subgraphSize * (subgraphSize - 1)) / 2d;
    double expectedComplementWeight = expectedCut + ((size - subgraphSize) * ((size - subgraphSize) - 1)) / 2d;
    double expectedNcut = expectedCut / expectedSubgraphWeight + expectedCut / expectedComplementWeight;
    assertThat(metric.compute(graph, subgraph), closeTo(expectedNcut / 2, 1E-9));
  }

  @Test
  public void maximumValueIsAssumed() {
    Graph graph = CompressedSparseGraph.builder().add(0, 1, 1).build();
    Graph subgraph = graph.inducedSubgraph(IntIterators.singleton(0));
    assertThat(metric.compute(graph, subgraph), closeTo(1d, 1E-9));
  }


}