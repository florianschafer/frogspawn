/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.helpers;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class VectorsTest {

  @Test
  public void scalarProduct() {
    double[] v = new double[]{2, 3, 5};
    double[] w = new double[]{7, 11, 13};
    assertThat(Vectors.scalarProduct(v, w), closeTo(112, 1E-9));
  }

  @Test
  public void norm2() {
    double[] v = new double[]{-2, 3, 5};
    Vectors.normalize2Sig(v);
    assertThat(v[0], closeTo(0.32444, 1E-5));
    assertThat(v[1], closeTo(-0.48666, 1E-5));
    assertThat(v[2], closeTo(-0.81111, 1E-5));
  }

  @Test
  public void normalize2() {
    double[] v = new double[]{-1, 3, -5};
    Vectors.normalize2(v);
    assertThat(v[0], closeTo(-0.16903, 1E-5));
    assertThat(v[1], closeTo(0.50709, 1E-5));
    assertThat(v[2], closeTo(-0.84515, 1E-5));
  }

  @Test
  public void normalize2Sig() {
    double[] v = new double[]{-1, 3, -5};
    Vectors.normalize2Sig(v);
    assertThat(v[0], closeTo(0.16903, 1E-5));
    assertThat(v[1], closeTo(-0.50709, 1E-5));
    assertThat(v[2], closeTo(0.84515, 1E-5));
  }

}