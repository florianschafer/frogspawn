package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.old.NormalizedLaplacianCSRSubmatrix;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class SpectrallyShiftedNormalizedLaplacianTest {

  @Test
  public void spectrallyShiftedProduct() {
    withBipartiteAdjacencyMatrix(view -> {

      SpectrallyShiftedNormalizedLaplacian mat = new SpectrallyShiftedNormalizedLaplacian(view);
      mat.update();
      double[] v = {43, 47, 53, 59, 61, 67};
      mat.multiply(v);
      assertThat(v[0], closeTo(-17.3263, 1E-4));
      assertThat(v[1], closeTo(-2.0033, 1E-4));
      assertThat(v[2], closeTo(8.5853, 1E-4));
      assertThat(v[3], closeTo(-2.8083, 1E-4));
      assertThat(v[4], closeTo(1.1999, 1E-4));
      assertThat(v[5], closeTo(1.1598, 1E-4));
    });
  }

  private void withBipartiteAdjacencyMatrix(Consumer<CSRStorage.View> viewConsumer) {
//    int[] indices = {0, 1, 2, 3, 4, 5};
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


//  @Test
//  public void simpleNormalizedLaplacian() {
//    int[] indices = {0, 1, 2, 3, 4, 5};
//    CSRStorage storage = new CSRStorageBuilder()
//            .add(0, 1, 1)
//            .add(0, 4, 1)
//            .add(1, 0, 1)
//            .add(1, 2, 1)
//            .add(1, 3, 1)
//            .add(1, 4, 1)
//            .add(2, 1, 1)
//            .add(2, 5, 1)
//            .add(3, 1, 1)
//            .add(3, 4, 1)
//            .add(4, 0, 1)
//            .add(4, 1, 1)
//            .add(4, 3, 1)
//            .add(4, 5, 1)
//            .add(5, 2, 1)
//            .add(5, 4, 1)
//            .build();
//    SpectrallyShiftedNormalizedLaplacian lap = new SpectrallyShiftedNormalizedLaplacian(storage.defaultView());
//    double invSqrt8 = -1.0 / Math.sqrt(8);
//    double quarter = -1.0 / 4;
//    double half = -1.0 / 2;
//    verifyNormLaplacianCol(lap, 0, 1, invSqrt8, 0, 0, invSqrt8, 0);
//    verifyNormLaplacianCol(lap, 1, invSqrt8, 1, invSqrt8, invSqrt8, quarter, 0);
//    verifyNormLaplacianCol(lap, 2, 0, invSqrt8, 1, 0, 0, half);
//    verifyNormLaplacianCol(lap, 3, 0, invSqrt8, 0, 1, invSqrt8, 0);
//    verifyNormLaplacianCol(lap, 4, invSqrt8, quarter, 0, invSqrt8, 1, invSqrt8);
//    verifyNormLaplacianCol(lap, 5, 0, 0, half, 0, invSqrt8, 1);
//    storage.free();
//  }
//
//  private List<Double> verifyNormLaplacianCol(SpectrallyShiftedNormalizedLaplacian lap, int col, double... expected) {
//    double[] v = new double[6]; // TODO: Change SIZE!
//    for (int i = 0; i < 6; i++) v[i] = i == col ? 1 : 0; // TODO: Change SIZE!
//    lap.update();
//    lap.multiply(v);
//
//    List<Double> colVec = IntStream.range(0, 6) // TODO: Change SIZE!
//            .mapToObj(i -> Math.round(v[i] * 10000) / 10000.0)
//            .collect(Collectors.toList());
//    List<Double> expectedList = Arrays.stream(expected)
//            .mapToObj(x -> Math.round(x * 10000) / 10000.0)
//            .collect(Collectors.toList());
//    assertThat("Columns should agree", colVec, is(expectedList));
//    return colVec;
//  }

}