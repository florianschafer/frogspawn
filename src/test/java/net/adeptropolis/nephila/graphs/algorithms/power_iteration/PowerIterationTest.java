package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.operators.CanonicalLinearOperator;
import net.adeptropolis.nephila.graphs.operators.SSNLOperator;
import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.*;

public class PowerIterationTest extends GraphTestBase {

  @Test
  public void matrix() {
    double[] expected = new double[]{0.35596, 0.33434, 0.34380, 0.30277, 0.27799, 0.29129, 0.32165, 0.27372, 0.29246, 0.35439};
    CanonicalLinearOperator op = new CanonicalLinearOperator(SOME_10_GRAPH);
    ConvergenceCriterion convergenceCriterion = new DeltaNormConvergence(1E-6);
    double[] iv = ConstantInitialVectors.generate(10);
    double[] r = PowerIteration.apply(op, convergenceCriterion, iv, 10000);
    assertNotNull(r);
    for (int i = 0; i < op.size(); i++) {
      assertThat(r[i], closeTo(expected[i], 1E-5));
    }
  }

  @Test
  public void iterationsExcess() {
    CanonicalLinearOperator op = new CanonicalLinearOperator(SOME_10_GRAPH);
    ConvergenceCriterion convergenceCriterion = new DeltaNormConvergence(1E-18);
    double[] iv = ConstantInitialVectors.generate(10);
    double[] r = PowerIteration.apply(op, convergenceCriterion, iv, 5);
    assertNull(r);
  }

  @Test
  public void normalizedLaplacian() {
    SSNLOperator op = new SSNLOperator(EIGEN_REF_GRAPH);
    ConvergenceCriterion convergenceCriterion = new DeltaNormConvergence(1E-9);
    double[] iv = ConstantInitialVectors.generate(op.size());
    double[] r = PowerIteration.apply(op, convergenceCriterion, iv, 1000);
    assertNotNull(r);
    assertThat(r[0], closeTo(0.33423, 1E-5));
    assertThat(r[1], closeTo(0.18452, 1E-5));
    assertThat(r[2], closeTo(-0.59518, 1E-5));
    assertThat(r[3], closeTo(0.33423, 1E-5));
    assertThat(r[4], closeTo(0.18452, 1E-5));
    assertThat(r[5], closeTo(-0.59518, 1E-5));
  }

}