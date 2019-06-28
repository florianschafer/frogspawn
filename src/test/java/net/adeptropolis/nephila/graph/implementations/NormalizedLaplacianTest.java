package net.adeptropolis.nephila.graph.implementations;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class NormalizedLaplacianTest {

  @Test
  public void simpleNormalizedLaplacian() {
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

    CSRStorage.View view = storage.defaultView();
    NormalizedLaplacian laplacian = new NormalizedLaplacian(view);
    double invSqrt8 = -1.0 / Math.sqrt(8);
    double quarter = -1.0 / 4;
    double half = -1.0 / 2;
    verifyNormLaplacianCol(laplacian, view.size(), 0, 1, invSqrt8, 0, 0, invSqrt8, 0);
    verifyNormLaplacianCol(laplacian, view.size(), 1, invSqrt8, 1, invSqrt8, invSqrt8, quarter, 0);
    verifyNormLaplacianCol(laplacian, view.size(), 2, 0, invSqrt8, 1, 0, 0, half);
    verifyNormLaplacianCol(laplacian, view.size(), 3, 0, invSqrt8, 0, 1, invSqrt8, 0);
    verifyNormLaplacianCol(laplacian, view.size(), 4, invSqrt8, quarter, 0, invSqrt8, 1, invSqrt8);
    verifyNormLaplacianCol(laplacian, view.size(), 5, 0, 0, half, 0, invSqrt8, 1);
    verifyV0(laplacian, view.size(), 3.5355e-01, 5.0000e-01, 3.5355e-01, 3.5355e-01, 5.0000e-01 , 3.5355e-01);
    storage.free();
  }

  @Test
  public void normalizedLaplacianSubset() {
    CSRStorage storage = new CSRStorageBuilder()
            .addSymmetric(0, 1, 20)
            .addSymmetric(0, 4, 2)
            .addSymmetric(1, 1, 30)
            .addSymmetric(1, 2, 80)
            .addSymmetric(1, 3, 90)
            .addSymmetric(1, 4, 100)
            .addSymmetric(1, 5, 100)
            .addSymmetric(2, 5, 1)
            .addSymmetric(3, 4, 1)
            .addSymmetric(4, 5, 1)
            .build();

    CSRStorage.View view = storage.view(new int[]{0, 2, 3, 4, 5});
    NormalizedLaplacian laplacian = new NormalizedLaplacian(view);
    double inv2Sqrt8 = -2.0 / Math.sqrt(8);
    double inv1Sqrt8 = -1.0 / Math.sqrt(8);
    double inv1Sqrt2 = -1.0 / Math.sqrt(2);
    double inv1Sqrt4 = -1.0 / Math.sqrt(4);
    verifyNormLaplacianCol(laplacian, view.size(), 0, 1, 0, 0, inv2Sqrt8, 0);
    verifyNormLaplacianCol(laplacian, view.size(), 1, 0, 1, 0, 0, inv1Sqrt2);
    verifyNormLaplacianCol(laplacian, view.size(), 2, 0, 0, 1, inv1Sqrt4, 0);
    verifyNormLaplacianCol(laplacian, view.size(), 3, inv2Sqrt8, 0, inv1Sqrt4, 1, inv1Sqrt8);
    verifyNormLaplacianCol(laplacian, view.size(), 4, 0, inv1Sqrt2, 0, inv1Sqrt8, 1);
    verifyV0(laplacian, view.size(), 0.44721, 0.31623, 0.31623, 0.63246, 0.44721);
    storage.free();
  }

  private void verifyNormLaplacianCol(NormalizedLaplacian laplacian, int size, int col, double... expected) {
    double[] v = new double[size];
    for (int i = 0; i < size; i++) v[i] = i == col ? 1 : 0;
    double[] result = laplacian.multiply(v);
    for (int i = 0; i < size; i++) assertThat("(" + i + ", " + col + ") should match",
            result[i], closeTo(expected[i], 1E-5));
  }

  private void verifyV0(NormalizedLaplacian laplacian, int indicesSize, double... expected) {
    for (int i = 0; i < indicesSize; i++) assertThat("v0 entry at " + i + " should match", laplacian.getV0()[i], closeTo(expected[i], 1E-5));
  }

}