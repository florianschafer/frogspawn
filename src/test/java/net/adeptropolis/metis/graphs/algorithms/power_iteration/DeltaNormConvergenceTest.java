/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms.power_iteration;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DeltaNormConvergenceTest {

  private double[] prev;
  private double[] current;

  @Test
  public void fullyConverged() {
    prev = new double[]{1, 2};
    current = new double[]{1, 2};
    boolean terminate = new DeltaNormConvergence(1E-9).satisfied(prev, current, 0);
    assertThat(terminate, is(true));
  }

  @Test
  public void notConverged() {
    prev = new double[]{1, 2};
    current = new double[]{1, 2.01};
    boolean terminate = new DeltaNormConvergence(1E-3).satisfied(prev, current, 0);
    assertThat(terminate, is(false));
  }

  @Test
  public void defaultInstance() {
    prev = new double[]{1, 2};
    current = new double[]{1, 2 + 1E-8};
    boolean terminate = new DeltaNormConvergence().satisfied(prev, current, 0);
    assertThat(terminate, is(false));
  }

}