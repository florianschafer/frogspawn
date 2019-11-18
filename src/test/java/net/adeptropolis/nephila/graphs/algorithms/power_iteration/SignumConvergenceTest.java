package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import org.junit.Test;

import static net.adeptropolis.nephila.graphs.algorithms.power_iteration.SignumConvergence.DEFAULT_MIN_ITERATIONS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SignumConvergenceTest {

  @Test
  public void minIterations() {
    SignumConvergence conv = new SignumConvergence(0, 10);
    double[] v = new double[]{2, 3};
    assertFalse(conv.satisfied(v, v, 9));
    assertTrue(conv.satisfied(v, v, 10));
  }

  @Test
  public void maxUnstable() {
    SignumConvergence conv = new SignumConvergence(0.25, 0);
    double[] v = new double[]{2, 3, 5, 7};
    assertFalse(conv.satisfied(v, new double[]{-11, -13, -17, -19}, 100));
    assertFalse(conv.satisfied(v, new double[]{-11, -13, -17, 19}, 100));
    assertFalse(conv.satisfied(v, new double[]{-11, 13, -17, 19}, 100));
    assertTrue(conv.satisfied(v, new double[]{23, 29, 31, -37}, 100));
    assertTrue(conv.satisfied(v, new double[]{23, 29, 31, 37}, 100));
  }

  @Test
  public void zeroEntries() {
    SignumConvergence conv = new SignumConvergence(0, 0);
    double[] v = new double[]{0, 0};
    assertFalse(conv.satisfied(v, new double[]{-1, -1}, 100));
    assertFalse(conv.satisfied(v, new double[]{-1, 0}, 100));
    assertFalse(conv.satisfied(v, new double[]{0, 1}, 100));
    assertTrue(conv.satisfied(v, new double[]{0, 0}, 100));
  }

  @Test
  public void defaultMinIterations() {
    SignumConvergence conv = new SignumConvergence(0);
    double[] v = new double[]{2, 3};
    assertFalse(conv.satisfied(v, v, DEFAULT_MIN_ITERATIONS - 1));
    assertTrue(conv.satisfied(v, v, DEFAULT_MIN_ITERATIONS));

  }

}