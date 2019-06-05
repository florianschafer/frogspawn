package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.arrays.ArrayDoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.arrays.ArrayIntBuffer;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class NormalizedLaplacianCSRSubmatrixTest {

  @Test
  public void simpleNormalizedLaplacian() {
    IntBuffer indices = new ArrayIntBuffer(6);
    for (int i = 0; i < 6; i++) indices.set(i, i);
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
    NormalizedLaplacianCSRSubmatrix mat = new NormalizedLaplacianCSRSubmatrix(storage, indices);
    double invSqrt8 = -1.0 / Math.sqrt(8);
    double quarter = -1.0 / 4;
    double half = -1.0 / 2;
    verifyNormLaplacianCol(mat,0, 1, invSqrt8, 0, 0, invSqrt8, 0);
    verifyNormLaplacianCol(mat, 1, invSqrt8, 1, invSqrt8, invSqrt8, quarter, 0);
    verifyNormLaplacianCol(mat, 2, 0, invSqrt8, 1, 0, 0, half);
    verifyNormLaplacianCol(mat, 3, 0, invSqrt8, 0, 1, invSqrt8, 0);
    verifyNormLaplacianCol(mat, 4, invSqrt8, quarter, 0, invSqrt8, 1, invSqrt8);
    verifyNormLaplacianCol(mat, 5, 0, 0, half, 0, invSqrt8, 1);
    indices.free();
    mat.free();
    storage.free();
  }

  @Test
  public void normalizedLaplacianSubset() {
    IntBuffer indices = new ArrayIntBuffer(5);
    indices.set(0, 0);
    for (int i = 1; i < 5; i++) indices.set(i, i + 1);
    CSRStorage storage = new CSRStorageBuilder()
            .addSymmetric(0, 1, 20) // *
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
    NormalizedLaplacianCSRSubmatrix mat = new NormalizedLaplacianCSRSubmatrix(storage, indices);
    double inv2Sqrt8 = -2.0 / Math.sqrt(8);
    double inv1Sqrt8 = -1.0 / Math.sqrt(8);
    double inv1Sqrt2 = -1.0 / Math.sqrt(2);
    double inv1Sqrt4 = -1.0 / Math.sqrt(4);
    verifyNormLaplacianCol(mat,0, 1, 0, 0, inv2Sqrt8, 0);
    verifyNormLaplacianCol(mat, 1, 0, 1, 0, 0, inv1Sqrt2);
    verifyNormLaplacianCol(mat, 2, 0, 0, 1, inv1Sqrt4, 0);
    verifyNormLaplacianCol(mat, 3, inv2Sqrt8, 0, inv1Sqrt4, 1, inv1Sqrt8);
    verifyNormLaplacianCol(mat, 4, 0, inv1Sqrt2, 0, inv1Sqrt8, 1);
    indices.free();
    mat.free();
    storage.free();
  }

  @Test
  public void spectrallyShiftedProduct() {
    withBipartiteAdjacencyMatrix(mat -> {
      DoubleBuffer v = new ArrayDoubleBuffer(6);
      v.set(0, 43);
      v.set(1, 47);
      v.set(2, 53);
      v.set(3, 59);
      v.set(4, 61);
      v.set(5, 67);
      DoubleBuffer result = new ArrayDoubleBuffer(6);
      mat.multiplySpectrallyShiftedNormalizedLaplacian(v, result);
      assertThat(result.get(0), closeTo(-17.3263, 1E-4));
      assertThat(result.get(1), closeTo(-2.0033, 1E-4));
      assertThat(result.get(2), closeTo(8.5853, 1E-4));
      assertThat(result.get(3), closeTo(-2.8083, 1E-4));
      assertThat(result.get(4), closeTo(1.1999, 1E-4));
      assertThat(result.get(5), closeTo(1.1598, 1E-4));
      result.free();
      v.free();
    });
  }

  @Test
  public void v2() {
    withBipartiteAdjacencyMatrix(mat -> {
      DoubleBuffer v2 = mat.bipartiteLambda2Eigenvector(1E-9);
      assertThat(v2.get(0), closeTo(0.470144, 1E-6));
      assertThat(v2.get(1), closeTo(0.316409, 1E-6));
      assertThat(v2.get(2), closeTo(-0.422907, 1E-6));
      assertThat(v2.get(3), closeTo(-0.573978, 1E-6));
      assertThat(v2.get(4), closeTo(0.052957, 1E-6));
      assertThat(v2.get(5), closeTo(0.409567, 1E-6));
      v2.free();
    });
  }

  private void withBipartiteAdjacencyMatrix(Consumer<NormalizedLaplacianCSRSubmatrix> matConsumer) {
    IntBuffer indices = new ArrayIntBuffer(6);
    for (int i = 0; i < 6; i++) indices.set(i, i);
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
    NormalizedLaplacianCSRSubmatrix mat = new NormalizedLaplacianCSRSubmatrix(storage, indices);
    matConsumer.accept(mat);
    indices.free();
    mat.free();
    storage.free();
  }


  private List<Double> verifyNormLaplacianCol(NormalizedLaplacianCSRSubmatrix mat, int col, double... expected) {
    DoubleBuffer resultsBuf = new ArrayDoubleBuffer(mat.size());
    DoubleBuffer v = new ArrayDoubleBuffer(mat.size());
    for (int i = 0; i < mat.size(); i++) v.set(i, i == col ? 1 : 0);
    mat.multiplyNormalizedLaplacian(v, resultsBuf);
    List<Double> colVec = IntStream.range(0, mat.size())
            .mapToObj(i -> Math.round(resultsBuf.get(i) * 10000) / 10000.0)
            .collect(Collectors.toList());
    List<Double> expectedList = Arrays.stream(expected)
            .mapToObj(x -> Math.round(x* 10000) / 10000.0)
            .collect(Collectors.toList());
    assertThat("Columns should agree", colVec, is(expectedList));
    v.free();
    resultsBuf.free();
    return colVec;
  }


}