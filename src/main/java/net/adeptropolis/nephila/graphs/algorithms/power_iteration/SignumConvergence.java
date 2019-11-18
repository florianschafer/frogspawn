package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

/**
 * <p>A relaxed convergence criterion implementation.</p>
 * <p>It only requires convergence of the vector entry signs instead of their values</p>
 */

public class SignumConvergence implements ConvergenceCriterion {

  static final int DEFAULT_MIN_ITERATIONS = 20;

  private final int minIterations;
  private final double maxUnstable;

  public SignumConvergence(double maxUnstable, int minIterations) {
    this.maxUnstable = maxUnstable;
    this.minIterations = minIterations;
  }

  public SignumConvergence(double maxUnstable) {
    this(maxUnstable, DEFAULT_MIN_ITERATIONS);
  }

  @Override
  public boolean satisfied(double[] previous, double[] current, int iterations) {
    if (iterations < minIterations) {
      return false;
    }
    int unstable = 0;
    for (int i = 0; i < current.length; i++) {
      if (Math.signum(current[i]) != Math.signum(previous[i])) {
        unstable += 1;
      }
    }
    return unstable / (double) current.length <= maxUnstable;
  }

}
