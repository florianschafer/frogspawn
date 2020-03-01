/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.helpers;

import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class VectorsTest {

  @Test
  public void scalarProduct() {
    double[] v = new double[]{2, 3, 5};
    double[] w = new double[]{7, 11, 13};
    assertThat(Vectors.scalarProduct(v, w), closeTo(112, 1E-9));
  }

  @Test
  public void normalize2() {
    double[] v = new double[]{-2, 3, 5};
    Vectors.normalize2Sig(v);
    assertThat(v[0], closeTo(0.32444, 1E-5));
    assertThat(v[1], closeTo(-0.48666, 1E-5));
    assertThat(v[2], closeTo(-0.81111, 1E-5));
  }

  @Test
  public void normalize2Sig() {
    double[] v = new double[]{0, 3, 5};
    Vectors.normalize2Sig(v);
    assertThat(v[0], closeTo(0, 1E-5));
    assertThat(v[1], closeTo(0.51450, 1E-5));
    assertThat(v[2], closeTo(0.85749, 1E-5));
  }

}