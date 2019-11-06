package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeltaNormTerminatorTest {

  private double[] prev;
  private double[] current;

  @Test
  public void fullyConverged() {
    prev = new double[]{1, 2};
    current = new double[]{1, 2};
    boolean terminate = new DeltaNormTerminator(1E-9).terminate(prev, current);
    assertTrue(terminate);
  }

  @Test
  public void notConverged() {
    prev = new double[]{1, 2};
    current = new double[]{1, 2.01};
    boolean terminate = new DeltaNormTerminator(1E-3).terminate(prev, current);
    assertFalse(terminate);
  }

  @Test
  public void defaultInstance() {
    prev = new double[]{1, 2};
    current = new double[]{1, 2 + 1E-8};
    boolean terminate = new DeltaNormTerminator().terminate(prev, current);
    assertFalse(terminate);
  }

}