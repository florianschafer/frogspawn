package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import net.adeptropolis.nephila.graphs.operators.LinearGraphOperator;
import net.adeptropolis.nephila.helpers.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerIteration {

  private static final Logger LOG = LoggerFactory.getLogger(PowerIteration.class.getSimpleName());

  /**
   * <p>Apply the power iteration method to any Operator to find the eigenvector associated with its' largest eigenvalue</p>
   * <p>The initial vector needs to satisfy ||x|| = 1</p>
   * @param op The operator operator
   * @param convergenceCriterion A given convergence criterion for the iteration
   * @param initialVector Initial vector for the iteration
   * @param maxIterations Maximum number of iterations.
   * @return Either the converged eigenvector or <code>null</code> if the number of allowed iterations has been exhausted.
   */

  public static double[] apply(LinearGraphOperator op, ConvergenceCriterion convergenceCriterion, double[] initialVector, int maxIterations) {
    double[] x = new double[op.size()];
    double[] y = initialVector;
    for (int i = 0; ; i++) {
      System.arraycopy(y, 0, x, 0, op.size());
      if (i >= maxIterations) {
        LOG.warn("Exceeded maximum number of iterations ({})", maxIterations);
        return null;
      }
      y = op.apply(x);
      Vectors.normalize2(y);
      if (convergenceCriterion.satisfied(x, y, i)) {
        LOG.debug("Power iteration for operator size {} finished after {} rounds.", op.size(), i + 1);
        return y;
      }
    }
  }

}
