/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.functions;

import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class AverageEdgeWeightTest extends GraphTestBase {

  @Test
  public void simpleCase() {
    SparseGraph graph = new SparseGraphBuilder()
            .add(0, 1, 1)
            .add(1, 2, 3)
            .build();
    Double avg = new AverageEdgeWeight().apply(graph);
    assertThat(avg, closeTo(2, 1E-3));
  }

  @Test
  public void averageWeight() {
    Double avg = new AverageEdgeWeight().apply(K43);
    assertThat(avg, closeTo(42.667, 1E-3));
  }


}