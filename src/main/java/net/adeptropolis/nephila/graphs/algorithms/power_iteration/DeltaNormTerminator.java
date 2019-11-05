package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

public class DeltaNormTerminator implements IterationTerminator {

  private static final double DEFAULT_PRECISION = 1E-9;

  private final double precision;

  public DeltaNormTerminator(double precision) {
    this.precision = precision;
  }

  public DeltaNormTerminator() {
    this (DEFAULT_PRECISION);
  }

  @Override
  public boolean terminate(double[] prev, double[] current) {
    double sum = 0;
    for (int i = 0; i < current.length; i++) {
      double d = current[i] - prev[i];
      sum += d * d;
    }
    sum = Math.sqrt(sum);
    return sum <= precision;
  }

}
