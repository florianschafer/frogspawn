/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.helpers;

import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class VectorsTest {

  @Test
  public void L1Norm() {
    double[] v = new double[]{2, 3, 5};
    assertThat(Vectors.L1Norm(v), closeTo(10, 1E-9));
  }

  @Test
  public void scalarProduct() {
    double[] v = new double[]{2, 3, 5};
    double[] w = new double[]{7, 11, 13};
    assertThat(Vectors.scalarProduct(v, w), closeTo(112, 1E-9));
  }

  @Test
  public void normalize2() {
    double[] v = new double[]{-2, 3, 5};
    Vectors.normalize2(v);
    assertThat(v[0], closeTo(0.32444, 1E-5));
    assertThat(v[1], closeTo(-0.48666, 1E-5));
    assertThat(v[2], closeTo(-0.81111, 1E-5));
  }

  @Test
  public void normalize2ZeroHead() {
    double[] v = new double[]{0, 3, 5};
    Vectors.normalize2(v);
    assertThat(v[0], closeTo(0, 1E-5));
    assertThat(v[1], closeTo(0.51450, 1E-5));
    assertThat(v[2], closeTo(0.85749, 1E-5));
  }

}