/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms.power_iteration;

import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.GraphTestBase;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;


public class ConstantSigTrailConvergenceTest extends GraphTestBase {

  private static final Graph K3 = completeGraph(3);

  @Test
  public void basicConvergence() {
    ConstantSigTrailConvergence conv = new ConstantSigTrailConvergence(K3, 3, 1.0);
    assertThat(conv.satisfied(null, new double[]{1, -1, 1}, 0), is(false));
    assertThat(conv.satisfied(null, new double[]{1, -1, -1}, 1), is(false));
    assertThat(conv.satisfied(null, new double[]{1, -1, -1}, 2), is(false));
    assertThat(conv.satisfied(null, new double[]{1, -1, -1}, 3), is(true));
  }

  @Test
  public void convergeAfterInitialPertubations() {
    ConstantSigTrailConvergence conv = new ConstantSigTrailConvergence(K3, 3, 1.0);
    assertThat(conv.satisfied(null, new double[]{1, -1, 1}, 0), is(false));
    assertThat(conv.satisfied(null, new double[]{1, -1, 1}, 1), is(false));
    assertThat(conv.satisfied(null, new double[]{-1, -1, -1}, 2), is(false));
    assertThat(conv.satisfied(null, new double[]{1, -1, -1}, 3), is(false));
    assertThat(conv.satisfied(null, new double[]{1, -1, -1}, 4), is(false));
    assertThat(conv.satisfied(null, new double[]{1, -1, -1}, 5), is(true));
  }

  @Test
  public void postprocessing() {
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 10)
            .add(1, 2, 1)
            .add(2, 3, 10)
            .build();
    ConstantSigTrailConvergence conv = new ConstantSigTrailConvergence(graph, 4, 0.49);
    for (int i = 0; i < 3; i++) {
      assertThat(conv.satisfied(null, new double[]{-1, alternatingValue(i), 1, alternatingValue(i + 1)}, i), is(false));
    }
    double[] v = new double[]{-1, alternatingValue(3), 1, alternatingValue(3 + 1)};
    assertThat(conv.satisfied(null, v, 3), is(true));
    conv.postprocess(v);
    assertThat(v[0], closeTo(-1, 1E-6));
    assertThat(v[1], closeTo(-1, 1E-6));
    assertThat(v[2], closeTo(1, 1E-6));
    assertThat(v[3], closeTo(1, 1E-6));
  }

  private int alternatingValue(int i) {
    return 2 * (i % 2) - 1;
  }

}