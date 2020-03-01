/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms.power_iteration;

import net.adeptropolis.metis.graphs.operators.LinearGraphOperator;
import net.adeptropolis.metis.helpers.Vectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classic power method implementation supporting variable convergence criteria
 */

public class PowerIteration {

  private static final Logger LOG = LoggerFactory.getLogger(PowerIteration.class.getSimpleName());

  private PowerIteration() {
  }

  /**
   * <p>Apply the power iteration method to any Operator to find the eigenvector associated with its' largest eigenvalue</p>
   * <p>The initial vector needs to satisfy ||x|| = 1</p>
   *
   * @param op                   The operator operator
   * @param convergenceCriterion A given convergence criterion for the iteration
   * @param initialVector        Initial vector for the iteration
   * @param maxIterations        Maximum number of iterations.
   * @return Either the converged eigenvector or <code>null</code> if the number of allowed iterations has been exhausted.
   * @see ConvergenceCriterion
   */

  public static double[] apply(LinearGraphOperator op, ConvergenceCriterion convergenceCriterion, double[] initialVector, int maxIterations) throws MaxIterationsExceededException {
    double[] x = new double[op.size()];
    double[] y = initialVector;
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    for (int i = 0; ; i++) {
      System.arraycopy(y, 0, x, 0, op.size());
      if (i >= maxIterations) {
        throw new MaxIterationsExceededException();
      }
      y = op.apply(x);
      Vectors.normalize2Sig(y);
      if (convergenceCriterion.satisfied(x, y, i)) {
        stopWatch.stop();
        LOG.trace("Power iteration for operator size {} finished after {} rounds in {}", op.size(), i + 1, stopWatch);
        return y;
      }
    }
  }

  /**
   * Exception for handling an excess in the number of allowed iterations
   */

  public static class MaxIterationsExceededException extends Exception {
    MaxIterationsExceededException() {
      super();
    }
  }

}