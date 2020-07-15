/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.VertexWeights;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class VertexWeightsTest {

  @Test
  public void emptyGraph() {
    Graph graph = SparseGraph.builder().build();
    double[] weights = VertexWeights.compute(graph);
    assertThat(weights.length, is(0));
  }

  @Test
  public void simpleGraphWithGaps() {
    Graph graph = SparseGraph.builder()
            .add(0, 1, 2)
            .add(0, 3, 3)
            .add(2, 1, 5)
            .add(5, 6, 7)
            .build();
    double[] weights = VertexWeights.compute(graph);
    assertThat("Array length mismatch", weights.length, is(7));
    double[] expected = new double[]{5d, 7d, 5d, 3d, 0d, 7d, 7d};
    for (int i = 0; i < expected.length; i++) {
      assertThat("Vertex weight mismatch", weights[i], closeTo(expected[i], 1E-6));
    }
  }

}