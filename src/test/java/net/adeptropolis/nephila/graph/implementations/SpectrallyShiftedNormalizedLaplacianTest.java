package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.old.NormalizedLaplacianCSRSubmatrix;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SpectrallyShiftedNormalizedLaplacianTest {

  @Test
  public void simpleNormalizedLaplacian() {
    int[] indices = {0, 1, 2, 3, 4, 5};
    CSRStorage storage = new CSRStorageBuilder()
            .add(0, 1, 1)
            .add(0, 4, 1)
            .add(1, 0, 1)
            .add(1, 2, 1)
            .add(1, 3, 1)
            .add(1, 4, 1)
            .add(2, 1, 1)
            .add(2, 5, 1)
            .add(3, 1, 1)
            .add(3, 4, 1)
            .add(4, 0, 1)
            .add(4, 1, 1)
            .add(4, 3, 1)
            .add(4, 5, 1)
            .add(5, 2, 1)
            .add(5, 4, 1)
            .build();
    SpectrallyShiftedNormalizedLaplacian lap = new SpectrallyShiftedNormalizedLaplacian(storage.defaultView());
    double invSqrt8 = -1.0 / Math.sqrt(8);
    double quarter = -1.0 / 4;
    double half = -1.0 / 2;
    verifyNormLaplacianCol(lap, 0, 1, invSqrt8, 0, 0, invSqrt8, 0);
    verifyNormLaplacianCol(lap, 1, invSqrt8, 1, invSqrt8, invSqrt8, quarter, 0);
    verifyNormLaplacianCol(lap, 2, 0, invSqrt8, 1, 0, 0, half);
    verifyNormLaplacianCol(lap, 3, 0, invSqrt8, 0, 1, invSqrt8, 0);
    verifyNormLaplacianCol(lap, 4, invSqrt8, quarter, 0, invSqrt8, 1, invSqrt8);
    verifyNormLaplacianCol(lap, 5, 0, 0, half, 0, invSqrt8, 1);
    storage.free();
  }

  private List<Double> verifyNormLaplacianCol(SpectrallyShiftedNormalizedLaplacian lap, int col, double... expected) {
    double[] v = new double[6]; // TODO: Change SIZE!
    for (int i = 0; i < 6; i++) v[i] = i == col ? 1 : 0; // TODO: Change SIZE!
    lap.update();
    lap.multiply(v);

    List<Double> colVec = IntStream.range(0, 6) // TODO: Change SIZE!
            .mapToObj(i -> Math.round(v[i] * 10000) / 10000.0)
            .collect(Collectors.toList());
    List<Double> expectedList = Arrays.stream(expected)
            .mapToObj(x -> Math.round(x * 10000) / 10000.0)
            .collect(Collectors.toList());
    assertThat("Columns should agree", colVec, is(expectedList));
    return colVec;
  }

}