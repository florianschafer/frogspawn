package net.adeptropolis.nephila.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LinearOperatorTest {

  @Test
  public void simple() {
    double[] y = new LinearOperator(defaultGraph()).apply(new double[]{17, 19, 23});
    assertThat(y[0], is(206.0));
    assertThat(y[1], is(437.0));
    assertThat(y[2], is(593.0));
  }

  @Test
  public void subset() {
    double[] y = new LinearOperator(defaultGraph().inducedSubgraph(IntIterators.wrap(new int[]{0, 2})))
            .apply(new double[]{29, 31});
    assertThat(y[0], is(213.0));
    assertThat(y[1], is(548.0));
  }

  private Graph defaultGraph() {
    return new CompressedSparseGraphBuilder()
            .add(0, 0, 2)
            .add(0, 1, 3)
            .add(0, 2, 5)
            .add(1, 1, 7)
            .add(1, 2, 11)
            .add(2, 2, 13)
            .build();
  }


}