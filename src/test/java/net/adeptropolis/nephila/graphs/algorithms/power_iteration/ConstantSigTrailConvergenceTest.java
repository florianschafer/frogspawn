/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstantSigTrailConvergenceTest extends GraphTestBase {

  private static final Graph K3 = completeGraph(3);

  @Test
  public void basicConvergence() {
    ConstantSigTrailConvergence conv = new ConstantSigTrailConvergence(K3, 3, 1.0);
    assertFalse(conv.satisfied(null, new double[]{1, -1, 1}, 0));
    assertFalse(conv.satisfied(null, new double[]{1, -1, -1}, 1));
    assertFalse(conv.satisfied(null, new double[]{1, -1, -1}, 2));
    assertTrue(conv.satisfied(null, new double[]{1, -1, -1}, 3));
  }

  @Test
  public void convergeAfterInitialPertubations() {
    ConstantSigTrailConvergence conv = new ConstantSigTrailConvergence(K3, 3, 1.0);
    assertFalse(conv.satisfied(null, new double[]{1, -1, 1}, 0));
    assertFalse(conv.satisfied(null, new double[]{1, -1, 1}, 1));
    assertFalse(conv.satisfied(null, new double[]{-1, -1, -1}, 2));
    assertFalse(conv.satisfied(null, new double[]{1, -1, -1}, 3));
    assertFalse(conv.satisfied(null, new double[]{1, -1, -1}, 4));
    assertTrue(conv.satisfied(null, new double[]{1, -1, -1}, 5));
  }

  @Test
  public void postprocessing() {
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 10)
            .add(1, 2, 1)
            .add(2, 3, 10)
            .build();
    ConstantSigTrailConvergence conv = new ConstantSigTrailConvergence(graph, 3, 0.49);
    for (int i = 0; i < 3; i++) {
      assertFalse(conv.satisfied(null, new double[]{-1, alternatingValue(i), 1, alternatingValue(i + 1)}, i));
    }
    System.out.println(alternatingValue(3));
    double[] v = new double[]{-1, alternatingValue(3), 1, alternatingValue(3 + 1)};
    assertTrue(conv.satisfied(null, v, 3));
    conv.postprocess(v);
    MatcherAssert.assertThat(v[0], closeTo(-1, 1E-6));
    MatcherAssert.assertThat(v[1], closeTo(-1, 1E-6));
    MatcherAssert.assertThat(v[2], closeTo(1, 1E-6));
    MatcherAssert.assertThat(v[3], closeTo(1, 1E-6));
  }

  private int alternatingValue(int i) {
    return 2 * (i % 2) - 1;
  }

}