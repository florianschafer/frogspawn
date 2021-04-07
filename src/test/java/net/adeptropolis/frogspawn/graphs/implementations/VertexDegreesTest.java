/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.VertexDegrees;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class VertexDegreesTest {

  @Test
  public void emptyGraph() {
    Graph graph = SparseGraph.builder().build();
    long[] degrees = VertexDegrees.compute(graph);
    assertThat(degrees.length, is(0));
  }

  @Test
  public void simpleGraphWithGaps() {
    Graph graph = SparseGraph.builder()
            .add(0, 1, 2)
            .add(0, 3, 3)
            .add(2, 1, 5)
            .add(5, 6, 7)
            .build();
    long[] degrees = VertexDegrees.compute(graph);
    assertThat("Array length mismatch", degrees.length, is(7));
    long[] expected = new long[]{2L, 2L, 1L, 1L, 0L, 1L, 1L};
    for (int i = 0; i < expected.length; i++) {
      assertThat("Vertex weight mismatch", degrees[i], is(expected[i]));
    }
  }

}