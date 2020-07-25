/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms.power_iteration;

import net.adeptropolis.frogspawn.graphs.matrices.SquareMatrix;
import net.adeptropolis.frogspawn.helpers.Vectors;
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
   * <p>Apply the power iteration method to any square matrix to find the eigenvector associated with
   * its largest eigenvalue</p>
   * <p>Note: The initial vector needs to satisfy ||x|| = 1</p>
   *
   * @param matrix                    Matrix
   * @param convergenceCriterion      A given convergence criterion for the iteration
   * @param initialVector             Initial vector for the iteration
   * @param maxIterations             Maximum number of iterations.
   * @param expectNegativeEigenvalues Whether to expect any negative eigenvalues.
   * @return Either the converged eigenvector or <code>null</code> if the number of allowed iterations has been exhausted.
   * @throws MaxIterationsExceededException If the maximum number of iterations has been exceeded
   * @see ConvergenceCriterion
   */

  public static double[] apply(SquareMatrix matrix, ConvergenceCriterion convergenceCriterion,
                               double[] initialVector, int maxIterations, boolean expectNegativeEigenvalues) throws MaxIterationsExceededException {
    double[] x = new double[matrix.size()];
    double[] y = initialVector;
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    for (int i = 0; ; i++) {
      System.arraycopy(y, 0, x, 0, matrix.size());
      if (i >= maxIterations) {
        throw new MaxIterationsExceededException(String.format("Exceeded maximum number of iterations (%d)", maxIterations));
      }
      y = matrix.multiply(x);
      if (expectNegativeEigenvalues) {
        Vectors.normalize2Sig(y);
      } else {
        Vectors.normalize2(y);
      }
      if (convergenceCriterion.satisfied(x, y, i)) {
        stopWatch.stop();
        LOG.trace("Power iteration for size {} finished after {} rounds in {}", matrix.size(), i + 1, stopWatch);
        return y;
      }
    }
  }

  /**
   * Exception for handling an excess in the number of allowed iterations
   */

  public static class MaxIterationsExceededException extends PowerIterationException {
    MaxIterationsExceededException(String message) {
      super(message);
    }
  }

}
