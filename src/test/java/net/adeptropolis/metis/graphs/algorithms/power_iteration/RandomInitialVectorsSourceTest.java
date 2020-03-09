/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms.power_iteration;

import net.adeptropolis.metis.helpers.Vectors;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

public class RandomInitialVectorsSourceTest {

  @Test
  public void basic() {
    double[] vec = new RandomInitialVectorsSource(1337).generate(4);
    assertThat(Vectors.norm2(vec), closeTo(1.0, 1E-9));
    double last = -1;
    for (double v : vec) {
      assertThat(v, not(closeTo(last, 1E-9)));
      last = v;
    }
  }

  @Test
  public void sameSeedsProduceSameValues() {
    double[] a = new RandomInitialVectorsSource(1337).generate(2);
    double[] b = new RandomInitialVectorsSource(1337).generate(2);
    assertThat(a[0], Matchers.closeTo(b[0], 1E-5));
  }

  @Test
  public void differentSeedsProduceDifferentValues() {
    double[] a = new RandomInitialVectorsSource(1337).generate(2);
    double[] b = new RandomInitialVectorsSource(1338).generate(2);
    assertThat(a[0], not(Matchers.closeTo(b[0], 1E-5)));
  }

}