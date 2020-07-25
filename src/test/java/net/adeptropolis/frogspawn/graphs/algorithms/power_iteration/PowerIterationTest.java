/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms.power_iteration;

import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.matrices.AdjacencyMatrix;
import net.adeptropolis.frogspawn.graphs.matrices.ShiftedNormalizedLaplacian;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThrows;

public class PowerIterationTest extends GraphTestBase {

  private static final RandomInitialVectorsSource RANDOM_IV = new RandomInitialVectorsSource(42133742L);

  @Test
  public void matrix() throws PowerIteration.MaxIterationsExceededException {
    double[] expected = new double[]{0.35596, 0.33434, 0.34380, 0.30277, 0.27799, 0.29129, 0.32165, 0.27372, 0.29246, 0.35439};
    AdjacencyMatrix op = new AdjacencyMatrix(SOME_10_GRAPH);
    ConvergenceCriterion convergenceCriterion = new DeltaNormConvergence(1E-6);
    double[] iv = RANDOM_IV.generate(10);
    double[] r = PowerIteration.apply(op, convergenceCriterion, iv, 10000, true);
    assertThat(r, is(notNullValue()));
    for (int i = 0; i < op.size(); i++) {
      assertThat(r[i], closeTo(expected[i], 1E-5));
    }
  }

  @Test
  public void iterationsExcess() {
    AdjacencyMatrix op = new AdjacencyMatrix(SOME_10_GRAPH);
    ConvergenceCriterion convergenceCriterion = new DeltaNormConvergence(1E-18);
    double[] iv = RANDOM_IV.generate(10);
    assertThrows(PowerIteration.MaxIterationsExceededException.class, () -> {
      PowerIteration.apply(op, convergenceCriterion, iv, 5, true);
    });
  }

  @Test
  public void normalizedLaplacian() throws PowerIteration.MaxIterationsExceededException {
    ShiftedNormalizedLaplacian op = new ShiftedNormalizedLaplacian(EIGEN_REF_GRAPH);
    ConvergenceCriterion convergenceCriterion = new DeltaNormConvergence(1E-9);
    double[] iv = RANDOM_IV.generate(op.size());
    double[] r = PowerIteration.apply(op, convergenceCriterion, iv, 1000, true);
    assertThat(r, is(notNullValue()));
    assertThat(r[0], closeTo(0.33423, 1E-5));
    assertThat(r[1], closeTo(0.18452, 1E-5));
    assertThat(r[2], closeTo(-0.59518, 1E-5));
    assertThat(r[3], closeTo(0.33423, 1E-5));
    assertThat(r[4], closeTo(0.18452, 1E-5));
    assertThat(r[5], closeTo(-0.59518, 1E-5));
  }

  @Test
  public void weightedK20NormalizedLaplacian() throws PowerIteration.MaxIterationsExceededException {
    ShiftedNormalizedLaplacian op = new ShiftedNormalizedLaplacian(WEIGHTED_K20);
    ConvergenceCriterion convergenceCriterion = new DeltaNormConvergence(1E-9);
    double[] iv = RANDOM_IV.generate(op.size());
    double[] r = PowerIteration.apply(op, convergenceCriterion, iv, 10000, true);
    assertThat(r, is(notNullValue()));
    double[] expected = new double[]{0.422554, -0.102237, 0.287406, 0.100775, -0.178143, -0.115820, -0.033225, -0.029026, -0.107263, -0.208185, -0.271339, 0.262986, 0.152637, 0.060499, 0.319281, -0.189389, -0.366464, 0.155779, 0.221442, -0.318028};
    for (int i = 0; i < expected.length; i++) {
      assertThat(r[i], closeTo(expected[i], 1E-5));
    }
  }


}