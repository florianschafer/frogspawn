package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import net.adeptropolis.nephila.graphs.operators.LinearGraphOperator;
import net.adeptropolis.nephila.helpers.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerIteration {

  private static final Logger LOG = LoggerFactory.getLogger(PowerIteration.class);

  private final LinearGraphOperator op;
  private final IterationTerminator terminator;

  private final double[] initialVector;
  private final int maxIterations;

  /**
   * <p>Apply the power iteration method to any Operator to find the eigenvector associated with its' largest eigenvalue</p>
   * <p>The initial vector needs to satisfy ||x|| = 1</p>
   * @param op
   * @param terminator
   * @param initialVector
   */

  public PowerIteration(LinearGraphOperator op, IterationTerminator terminator, double[] initialVector, int maxIterations) {
    this.op = op;
    this.terminator = terminator;
    this.initialVector = initialVector;
    this.maxIterations = maxIterations;
  }

  double[] powerIteration() {
    double[] x = new double[op.size()];
    double[] y;
    System.arraycopy(initialVector, 0, x, 0, op.size());
    for (int i = 0; ; i++) {
      if (i >= maxIterations) {
        LOG.warn("Exceeded maximum number of iterations ({})", maxIterations);
        return null;
      }
      y = op.apply(x);
      Vectors.normalize2(y);
      if (terminator.terminate(x, y)) {
        return y;
      }
      System.arraycopy(y, 0, x, 0, op.size());
    }
  }

}
