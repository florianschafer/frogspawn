/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import net.adeptropolis.nephila.helpers.Vectors;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

public class RandomInitialVectorsTest {

  @Test
  public void basic() {
    double[] vec = RandomInitialVectors.generate(4);
    assertThat(Vectors.norm2(vec), closeTo(1.0, 1E-9));
    double last = -1;
    for (int i = 0; i < vec.length; i++) {
      assertThat(vec[i], not(closeTo(last, 1E-9)));
      last = vec[i];
    }
  }

}