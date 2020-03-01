/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms.power_iteration;

/**
 * <p>A relaxed convergence criterion implementation.</p>
 * <p>In contrast to more strict convergence criteria, this version only requires convergence of the vector entry signs instead of their values</p>
 */

public class SignumConvergence implements ConvergenceCriterion {

  static final int DEFAULT_MIN_ITERATIONS = 20;

  private final int minIterations;
  private final double maxUnstable;

  /**
   * Constructor
   *
   * @param maxUnstable   Maximum fraction of entries whose signum alternates between two successive iterations
   * @param minIterations Minimum number of iterations. Prevents premature termination for small graphs.
   *                      Mind that for small graphs, <code>maxUnstable</code> will most likely translate
   *                      into the requirement that all signums of both vectors match, but the random chance
   *                      for this event is still <code>2^-n</code>
   */

  public SignumConvergence(double maxUnstable, int minIterations) {
    this.maxUnstable = maxUnstable;
    this.minIterations = minIterations;
  }

  /**
   * Constructor with <code>maxUnstable = 20</code>
   *
   * @param maxUnstable Maximum fraction of entries whose signum alternates between two successive iterations
   */

  public SignumConvergence(double maxUnstable) {
    this(maxUnstable, DEFAULT_MIN_ITERATIONS);
  }

  /**
   * <p>Assess whether the power iteration has converged</p>
   *
   * @param previous   Result of the previous iteration
   * @param current    Result of the current iteration
   * @param iterations Number of iterations
   * @return True if and only if the convergence criterion is satisfied.
   */

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
