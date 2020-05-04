/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.similarity;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class OverlapGraphSimilarityMetricTest {

  @Test
  public void overlap() {
    OverlapGraphSimilarityMetric metric = new OverlapGraphSimilarityMetric();
    Graph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .add(1, 3, 6)
            .add(3, 2, 7)
            .add(3, 4, 8)
            .build();
    assertThat(metric.compute(graph, graph), closeTo(1.0, 1E-6));
    Graph subgraph = graph.inducedSubgraph(IntIterators.wrap(new int[]{0, 1, 2}));
    double overlap = metric.compute(graph, subgraph);
    assertThat(overlap, closeTo(24d / 37d, 1E-6));
  }

}