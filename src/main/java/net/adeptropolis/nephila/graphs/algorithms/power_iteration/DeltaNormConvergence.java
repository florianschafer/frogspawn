package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

/**
 * <p>Standard delta norm convergence criterion.</p>
 * <p>Convergence is reached whenever <code>|| v - v<sub>i-1</sub> || <= precision</code></p>
 */

public class DeltaNormConvergence implements ConvergenceCriterion {

  private static final double DEFAULT_PRECISION = 1E-9;

  private final double precision;

  public DeltaNormConvergence(double precision) {
    this.precision = precision;
  }

  public DeltaNormConvergence() {
    this (DEFAULT_PRECISION);
  }

  @Override
  public boolean satisfied(double[] previous, double[] current, int iterations) {
    double sum = 0;
    for (int i = 0; i < current.length; i++) {
      double d = current[i] - previous[i];
      sum += d * d;
    }
    sum = Math.sqrt(sum);
    return sum <= precision;
  }

}
