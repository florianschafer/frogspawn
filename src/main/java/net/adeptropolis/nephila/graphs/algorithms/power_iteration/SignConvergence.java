package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

/**
 * <p>A relaxed convergence criterion implementation.</p>
 * <p>It only requires convergence of the vector entry signs instead of their values</p>
 */

public class SignConvergence implements ConvergenceCriterion {

  private final int minIterations;
  private final double maxUnstable;

  public SignConvergence(int minIterations, double maxUnstable) {
    this.minIterations = minIterations;
    this.maxUnstable = maxUnstable;
  }

  @Override
  public boolean satisfied(double[] previous, double[] current, int iterations) {
    if (iterations < minIterations) {
      return false;
    }
    int unstable = 0;
    for (int i = 0; i < current.length; i++) {
      if (sig(current[i]) != sig(previous[i])) {
        unstable += 1;
      }
    }
    return unstable / (double) current.length <= maxUnstable;
  }

  /**
   * <p>A slightly modified signum function. That is, <code>sig(x) = {1 if x = 0, Math.signum(x) otherwise}</code> </p>
   * @param x Any real number
   * @return -1 if x < 0, 1 otherwise
   */

  private byte sig(double x) {
    if (x >= 0) {
      return 1;
    } else {
      return -1;
    }
  }

}
