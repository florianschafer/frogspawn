package net.adeptropolis.nephila.graph.implementations;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

// TODOS:
// 1) Signum test should use a bigger matrix
// 2) Subset selection and re-use should also be tested

public class BipartiteSSNLSolverTest {

  @Test
  public void spectrallyShiftedProduct() {
    withBipartiteAdjacencyMatrix(view -> {
      BipartiteSSNLSolver solver = new BipartiteSSNLSolver(view);
      double[] v = {43, 47, 53, 59, 61, 67};
      double[] y = solver.multiply(v);
      assertThat(y[0], closeTo(-17.3263, 1E-4));
      assertThat(y[1], closeTo(-2.0033, 1E-4));
      assertThat(y[2], closeTo(8.5853, 1E-4));
      assertThat(y[3], closeTo(-2.8083, 1E-4));
      assertThat(y[4], closeTo(1.1999, 1E-4));
      assertThat(y[5], closeTo(1.1598, 1E-4));
    });
  }

  private void withBipartiteAdjacencyMatrix(Consumer<CSRStorage.View> viewConsumer) {
    CSRStorage storage = new CSRStorageBuilder()
            .addSymmetric(0, 3, 2)
            .addSymmetric(0, 4, 3)
            .addSymmetric(0, 5, 5)
            .addSymmetric(1, 3, 7)
            .addSymmetric(1, 4, 11)
            .addSymmetric(1, 5, 13)
            .addSymmetric(2, 3, 17)
            .addSymmetric(2, 4, 19)
            .addSymmetric(2, 5, 23)
            .addSymmetric(3, 0, 2)
            .addSymmetric(3, 1, 7)
            .addSymmetric(3, 2, 17)
            .addSymmetric(4, 0, 3)
            .addSymmetric(4, 1, 11)
            .addSymmetric(4, 2, 19)
            .addSymmetric(5, 0, 5)
            .addSymmetric(5, 1, 13)
            .addSymmetric(5, 2, 23)
            .build();
    viewConsumer.accept(storage.defaultView());
    storage.free();
  }

  @Test
  public void v2() {
    withBipartiteAdjacencyMatrix(view -> {
      BipartiteSSNLSolver solver = new BipartiteSSNLSolver(view);
      double[] v2 = solver.approxV2(1E-9);
      assertThat(v2[0], closeTo(0.470144, 1E-6));
      assertThat(v2[1], closeTo(0.316409, 1E-6));
      assertThat(v2[2], closeTo(-0.422907, 1E-6));
      assertThat(v2[3], closeTo(-0.573978, 1E-6));
      assertThat(v2[4], closeTo(0.052957, 1E-6));
      assertThat(v2[5], closeTo(0.409567, 1E-6));
    });
  }

  @Test
  public void v2Signums() {
    withBipartiteAdjacencyMatrix(view -> {
      BipartiteSSNLSolver solver = new BipartiteSSNLSolver(view);
      double[] v2 = solver.approxV2Signatures(1E-6, 100);
      int[] signatures = Arrays.stream(v2).mapToInt(x -> (int) Math.signum(x)).toArray();
      assertThat(signatures[0], is(1));
      assertThat(signatures[1], is(1));
      assertThat(signatures[2], is(-1));
      assertThat(signatures[3], is(-1));
      assertThat(signatures[4], is(1));
      assertThat(signatures[5], is(1));
    });
  }

}