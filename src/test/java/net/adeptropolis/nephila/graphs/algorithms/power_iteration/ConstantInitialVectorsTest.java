package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class ConstantInitialVectorsTest {

  @Test
  public void basic() {
    double[] iv = ConstantInitialVectors.generate(4);
    for (int i = 0; i < 3; i++) {
      assertThat(iv[i], closeTo(0.5, 1E-9));
    }
  }

}