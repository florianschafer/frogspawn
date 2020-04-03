/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class WeightSortOpsTest {

  @Test
  public void basicFunctionality() {
    int[] vertices = new int[]{1, 2};
    double[] weights = new double[]{1d, 2d};
    double[] scores = new double[]{10d, 20d};
    WeightSortOps ops = new WeightSortOps(vertices, weights, scores);
    assertThat(ops.compare(0, 0), is(0));
    assertThat(ops.compare(0, 1), is(1));
    assertThat(ops.compare(1, 0), is(-1));
    assertThat(ops.compare(1, 1), is(0));
    ops.swap(0, 1);
    assertThat(vertices[0], is(2));
    assertThat(vertices[1], is(1));
    assertThat(weights[0], closeTo(2, 1E-6));
    assertThat(weights[1], closeTo(1, 1E-6));
    assertThat(scores[0], closeTo(20, 1E-6));
    assertThat(scores[1], closeTo(10, 1E-6));
  }

}