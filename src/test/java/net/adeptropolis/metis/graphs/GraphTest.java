/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class GraphTest {

  @Test
  public void weights() {
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
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
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .build();
    assertThat(graph.totalWeight(), closeTo(24, 1E-6));
  }

  @Test
  public void relativeWeights() {
    Graph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .add(1, 3, 6)
            .add(3, 2, 7)
            .build();
    Graph subgraph = graph.inducedSubgraph(IntIterators.wrap(new int[]{0, 1, 2}));
    double[] relWeights = subgraph.relativeWeights(graph);
    assertThat(relWeights.length, is(3));
    assertThat(relWeights[0], closeTo(1, 1E-6));
    assertThat(relWeights[1], closeTo(7.0 / 13, 1E-6));
    assertThat(relWeights[2], closeTo(9.0 / 16, 1E-6));
  }

  @Test
  public void overlap() {
    Graph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .add(1, 3, 6)
            .add(3, 2, 7)
            .add(3, 4, 8)
            .build();
    Graph subgraph = graph.inducedSubgraph(IntIterators.wrap(new int[]{0, 1, 2}));
    double overlap = subgraph.overlap(graph);
    assertThat(overlap, closeTo(24d / 37d, 1E-6));
  }

}