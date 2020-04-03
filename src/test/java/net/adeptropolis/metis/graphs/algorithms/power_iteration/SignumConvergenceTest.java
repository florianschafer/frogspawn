/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms.power_iteration;

import org.junit.Test;

import static net.adeptropolis.metis.graphs.algorithms.power_iteration.SignumConvergence.DEFAULT_MIN_ITERATIONS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SignumConvergenceTest {

  @Test
  public void minIterations() {
    SignumConvergence conv = new SignumConvergence(0, 10);
    double[] v = new double[]{2, 3};
    assertThat(conv.satisfied(v, v, 9), is(false));
    assertThat(conv.satisfied(v, v, 10), is(true));
  }

  @Test
  public void maxUnstable() {
    SignumConvergence conv = new SignumConvergence(0.25, 0);
    double[] v = new double[]{2, 3, 5, 7};
    assertThat(conv.satisfied(v, new double[]{-11, -13, -17, -19}, 100), is(false));
    assertThat(conv.satisfied(v, new double[]{-11, -13, -17, 19}, 100), is(false));
    assertThat(conv.satisfied(v, new double[]{-11, 13, -17, 19}, 100), is(false));
    assertThat(conv.satisfied(v, new double[]{23, 29, 31, -37}, 100), is(true));
    assertThat(conv.satisfied(v, new double[]{23, 29, 31, 37}, 100), is(true));
  }

  @Test
  public void zeroEntries() {
    SignumConvergence conv = new SignumConvergence(0, 0);
    double[] v = new double[]{0, 0};
    assertThat(conv.satisfied(v, new double[]{-1, -1}, 100), is(false));
    assertThat(conv.satisfied(v, new double[]{-1, 0}, 100), is(false));
    assertThat(conv.satisfied(v, new double[]{0, 1}, 100), is(false));
    assertThat(conv.satisfied(v, new double[]{0, 0}, 100), is(true));
  }

  @Test
  public void defaultMinIterations() {
    SignumConvergence conv = new SignumConvergence(0);
    double[] v = new double[]{2, 3};
    assertThat(conv.satisfied(v, v, DEFAULT_MIN_ITERATIONS - 1), is(false));
    assertThat(conv.satisfied(v, v, DEFAULT_MIN_ITERATIONS), is(true));

  }

}