/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.operators;

import net.adeptropolis.nephila.graphs.GraphTestBase;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class SSNLOperatorTest extends GraphTestBase {

  private final double INV_SQRT_8 = 1.0 / Math.sqrt(8);
  private final double ONE_HALF = 1.0 / 2;

  private static double[] getAsMatrix(SSNLOperator op, int n) {
    double[] matrix = new double[n * n];
    for (int j = 0; j < n; j++) {
      double[] col = getAsMatrixCol(op, j, n);
      for (int i = 0; i < n; i++) {
        matrix[i * n + j] = col[i];
      }
    }
    return matrix;
  }

  private static double[] getAsMatrixCol(SSNLOperator op, int col, int n) {
    double[] basisVec = IntStream.range(0, n).mapToDouble(i -> i == col ? 1 : 0).toArray();
    return op.apply(basisVec);
  }

  @Test
  public void v0() {
    double[] expected = new double[]{INV_SQRT_8, ONE_HALF, INV_SQRT_8, INV_SQRT_8, ONE_HALF, INV_SQRT_8};
    double[] v0 = SSNLOperator.computeV0(EIGEN_REF_GRAPH);
    for (int i = 0; i < expected.length; i++) {
      assertThat(v0[i], closeTo(expected[i], 1E-6));
    }
  }

  @Test
  public void someOperatorMatrix() {
    double[] expected = new double[]{
            0.609756, -0.069140, -0.487805, -0.071344, 0.505583, -0.351761,
            -0.069140, 0.829268, 0.055312, -0.204063, -0.214024, -0.232668,
            -0.487805, 0.055312, 0.390244, 0.057075, -0.404466, 0.281409,
            -0.071344, -0.204063, 0.057075, 0.756098, -0.255807, -0.278092,
            0.505583, -0.214024, -0.404466, -0.255807, 0.731707, -0.291665,
            -0.351761, -0.232668, 0.281409, -0.278092, -0.291665, 0.682927,
    };
    int n = SOME_BIPARTITE_GRAPH.size();
    double[] m = getAsMatrix(new SSNLOperator(SOME_BIPARTITE_GRAPH), n);
    for (int i = 0; i < n * n; i++) {
      assertThat(m[i], closeTo(expected[i], 1E-6));
    }
  }

  @Test
  public void K43OperatorMatrix() {
    double[] expected = new double[]{
            0.93554688, -0.02515966, -0.12239410, -0.01261441, -0.14412157, 0.02817973, -0.15667666,
            -0.02515966, 0.80468750, -0.00222013, -0.26637074, -0.00954878, -0.29361907, 0.02086804,
            -0.12239410, -0.00222013, 0.76757812, -0.00154911, -0.27368153, 0.00288216, -0.29752319,
            -0.01261441, -0.26637074, -0.00154911, 0.63671875, 0.00604270, -0.40044302, 0.00084095,
            -0.14412157, -0.00954878, -0.27368153, 0.00604270, 0.67773438, 0.00086983, -0.35033967,
            0.02817973, -0.29361907, 0.00288216, -0.40044302, 0.00086983, 0.55859375, -0.01464412,
            -0.15667666, 0.02086804, -0.29752319, 0.00084095, -0.35033967, -0.01464412, 0.61914062,
    };
    int n = K43.size();
    double[] m = getAsMatrix(new SSNLOperator(K43), n);
    for (int i = 0; i < n * n; i++) {
      assertThat(m[i], closeTo(expected[i], 1E-6));
    }
  }

  @Test
  public void someOperatorMatrixMultiplication() {
    double[] expected = new double[]{41.6784, 47.2580, -33.3428, 1.0678, 23.3906, -57.1306};
    int n = SOME_BIPARTITE_GRAPH.size();
    double[] arg = new double[]{353, 359, 367, 373, 379, 383};
    double[] r = new SSNLOperator(SOME_BIPARTITE_GRAPH).apply(arg);
    for (int i = 0; i < n; i++) {
      assertThat(r[i], closeTo(expected[i], 1E-4));
    }
  }

  @Test
  public void K43OperatorMatrixMultiplication() {
    double[] expected = new double[]{782.760, 349.970, 111.494, -57.916, -144.658, -180.255, -276.041};
    int n = K43.size();
    double[] arg = new double[]{1579, 1583, 1597, 1601, 1607, 1609, 1613};
    double[] r = new SSNLOperator(K43).apply(arg);
    for (int i = 0; i < n; i++) {
      assertThat(r[i], closeTo(expected[i], 1E-3));
    }
  }

  @Test
  public void reusability() {
    SSNLOperator op = new SSNLOperator(K12);
    double[] r1 = op.apply(new double[]{13, 19, 27});
    assertThat(r1[0], closeTo(0, 1E-4));
    assertThat(r1[1], closeTo(-1.8272, 1E-4));
    assertThat(r1[2], closeTo(1.4919, 1E-4));
    double[] r2 = op.apply(new double[]{61, 67, 71});
    assertThat(r1[0], closeTo(0, 1E-4));
    assertThat(r1[1], closeTo(5.4172, 1E-4));
    assertThat(r1[2], closeTo(-4.4232, 1E-4));
  }

}