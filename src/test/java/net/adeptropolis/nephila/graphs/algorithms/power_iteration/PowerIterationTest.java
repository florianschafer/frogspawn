package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.operators.CanonicalLinearOperator;
import net.adeptropolis.nephila.graphs.operators.SSNLOperator;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class PowerIterationTest extends GraphTestBase {

  @Test
  public void matrix() {
    double[] expected = new double[]{ 0.35596, 0.33434, 0.34380, 0.30277, 0.27799, 0.29129, 0.32165, 0.27372, 0.29246, 0.35439 };
    CanonicalLinearOperator op = new CanonicalLinearOperator(SOME_10_GRAPH);
    IterationTerminator terminator = new DeltaNormTerminator(1E-6);
    double[] iv = ConstantInitialVectors.generate(10);
    double[] r = new PowerIteration(op, terminator, iv, 10000).powerIteration();
    for (int i = 0; i < op.size(); i++) {
      assertThat(r[i], closeTo(expected[i], 1E-5));
    }
  }

  @Test
  public void iterationsExcess() {
    CanonicalLinearOperator op = new CanonicalLinearOperator(SOME_10_GRAPH);
    IterationTerminator terminator = new DeltaNormTerminator(1E-18);
    double[] iv = ConstantInitialVectors.generate(10);
    double[] r = new PowerIteration(op, terminator, iv, 5).powerIteration();
    assertNull(r);
  }

  @Test
  public void normalizedLaplacian() {
    SSNLOperator op = new SSNLOperator(EIGEN_REF_GRAPH);
    IterationTerminator terminator = new DeltaNormTerminator(1E-9);
    double[] iv = ConstantInitialVectors.generate(op.size());
    double[] r = new PowerIteration(op, terminator, iv, 1000000).powerIteration();
    String x = Arrays.stream(r).mapToObj(String::valueOf).collect(Collectors.joining(", "));
    TODO: Find out why this bastard won't converge
    System.out.println(x);
//    assertThat(r[0], closeTo(1, 1E-8));

  }

}