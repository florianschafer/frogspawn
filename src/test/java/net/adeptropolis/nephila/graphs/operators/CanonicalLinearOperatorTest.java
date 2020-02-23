/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.operators;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CanonicalLinearOperatorTest {

  @Test
  public void simple() {
    double[] y = new CanonicalLinearOperator(defaultGraph()).apply(new double[]{17, 19, 23});
    assertThat(y[0], is(206.0));
    assertThat(y[1], is(437.0));
    assertThat(y[2], is(593.0));
  }

  @Test
  public void subset() {
    double[] y = new CanonicalLinearOperator(defaultGraph().inducedSubgraph(IntIterators.wrap(new int[]{0, 2})))
            .apply(new double[]{29, 31});
    assertThat(y[0], is(213.0));
    assertThat(y[1], is(548.0));
  }

  @Test
  public void reusability() {
    CanonicalLinearOperator op = new CanonicalLinearOperator(defaultGraph());
    double[] r1 = op.apply(new double[]{13, 19, 27});
    assertThat(r1[0], closeTo(218, 1E-6));
    assertThat(r1[1], closeTo(469, 1E-6));
    assertThat(r1[2], closeTo(625, 1E-6));
    double[] r2 = op.apply(new double[]{61, 67, 71});
    assertThat(r2[0], closeTo(678, 1E-6));
    assertThat(r2[1], closeTo(1433, 1E-6));
    assertThat(r2[2], closeTo(1965, 1E-6));
  }

  private Graph defaultGraph() {
    return new CompressedSparseGraphBuilder()
            .add(0, 0, 2)
            .add(0, 1, 3)
            .add(0, 2, 5)
            .add(1, 1, 7)
            .add(1, 2, 11)
            .add(2, 2, 13)
            .build();
  }


}