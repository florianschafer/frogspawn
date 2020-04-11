/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.consistency;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class RelativeWeightConsistencyMetricTest {

  private Graph graph;
  private Graph subgraph;
  private RelativeWeightConsistencyMetric metric;

  @Before
  public void setup() {
    metric = new RelativeWeightConsistencyMetric();
    graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .add(1, 3, 6)
            .add(3, 2, 7)
            .build();
    subgraph = graph.inducedSubgraph(IntIterators.wrap(new int[]{0, 1, 2}));
  }

  @Test
  public void standardMetric() {
    double[] relWeights = metric.compute(graph, subgraph);
    assertThat(relWeights.length, is(3));
    assertThat(relWeights[0], closeTo(1, 1E-6));
    assertThat(relWeights[1], closeTo(7.0 / 13, 1E-6));
    assertThat(relWeights[2], closeTo(9.0 / 16, 1E-6));
  }

  @Test
  public void restrictedMetric() {
    Graph subsubgraph = subgraph.inducedSubgraph(IntIterators.wrap(new int[]{1, 2}));
    double[] relWeights = metric.compute(graph, subgraph, subsubgraph);
    assertThat(relWeights.length, is(2));
    assertThat(relWeights[0], closeTo(7.0 / 13, 1E-6));
    assertThat(relWeights[1], closeTo(9.0 / 16, 1E-6));
  }

}