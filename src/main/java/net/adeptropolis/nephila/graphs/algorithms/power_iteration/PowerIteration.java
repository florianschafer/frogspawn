/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import net.adeptropolis.nephila.graphs.operators.LinearGraphOperator;
import net.adeptropolis.nephila.helpers.Vectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   */

  public static double[] apply(LinearGraphOperator op, ConvergenceCriterion convergenceCriterion, double[] initialVector, int maxIterations) throws MaxIterationsExceededException {
    double[] x = new double[op.size()];
    double[] y = initialVector;
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
//    VectorDumper dumper = new VectorDumper(Math.abs(op.hashCode()));
    for (int i = 0; ; i++) {
      System.arraycopy(y, 0, x, 0, op.size());
      if (i >= maxIterations) {
//        dumper.close();
        throw new MaxIterationsExceededException();
      }
      y = op.apply(x);
      Vectors.normalize2(y);
//      dumper.dump(y);
      if (convergenceCriterion.satisfied(x, y, i)) {
        stopWatch.stop();
        LOG.trace("Power iteration for operator size {} finished after {} rounds in {}", op.size(), i + 1, stopWatch);
//        dumper.close();
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
