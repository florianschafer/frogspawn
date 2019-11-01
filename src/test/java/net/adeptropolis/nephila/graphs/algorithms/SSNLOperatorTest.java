package net.adeptropolis.nephila.graphs.algorithms;

import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class SSNLOperatorTest {

  private final double INV_SQRT_8 = 1.0 / Math.sqrt(8);
  private final double ONE_HALF = 1.0 / 2;

  /*
  This is what our default graph looks like:
   0 1 0 0 1 0
   1 0 1 1 1 0
   0 1 0 0 0 1
   0 1 0 0 1 0
   1 1 0 1 0 1
   0 0 1 0 1 0
  */

  private final Graph defaultGraph = new CompressedSparseGraphBuilder()
    .add(0, 1, 1)
    .add(0, 4, 1)
    .add(1, 2, 1)
    .add(1, 3, 1)
    .add(1, 4, 1)
    .add(2, 5, 1)
    .add(3, 4, 1)
    .add(4, 5, 1)
    .build();

  @Test
  public void v0() {
    double[] expected = new double[]{ INV_SQRT_8, ONE_HALF, INV_SQRT_8, INV_SQRT_8, ONE_HALF, INV_SQRT_8 };
    double[] v0 = SSNLOperator.computeV0(defaultGraph.computeWeights());
    for (int i = 0; i < expected.length; i++) {
      assertThat(v0[i], closeTo(expected[i], 1E-6));
    }
  }

  @Test
  public void defaultGraphEigenvector() {
    new SSNLOperator(defaultGraph).apply()


  }

//  @Test
//  public void simpleNormalizedLaplacian() {
//    Graph graph = new CompressedSparseGraphBuilder()
//            .add(0, 1, 1)
//            .add(0, 4, 1)
//            .add(1, 2, 1)
//            .add(1, 3, 1)
//            .add(1, 4, 1)
//            .add(2, 5, 1)
//            .add(3, 4, 1)
//            .add(4, 5, 1)
//            .build();
//
//    SSNLOperator laplacian = new SSNLOperator(graph);
//
//
//    double[] foo = laplacian.apply(new double[]{1d, 0, 0, 0, 0, 0});
//    for (int i = 0; i < foo.length; i++) System.out.println(String.format("%d: %.3f", i, foo[i]));
//
//
////    double invSqrt8 = -1.0 / Math.sqrt(8);
////    double quarter = -1.0 / 4;
////    double half = -1.0 / 2;
////    verifyNormLaplacianCol(laplacian, graph.size(), 0, 1, invSqrt8, 0, 0, invSqrt8, 0);
////    verifyNormLaplacianCol(laplacian, graph.size(), 1, invSqrt8, 1, invSqrt8, invSqrt8, quarter, 0);
////    verifyNormLaplacianCol(laplacian, graph.size(), 2, 0, invSqrt8, 1, 0, 0, half);
////    verifyNormLaplacianCol(laplacian, graph.size(), 3, 0, invSqrt8, 0, 1, invSqrt8, 0);
////    verifyNormLaplacianCol(laplacian, graph.size(), 4, invSqrt8, quarter, 0, invSqrt8, 1, invSqrt8);
////    verifyNormLaplacianCol(laplacian, graph.size(), 5, 0, 0, half, 0, invSqrt8, 1);
////    verifyV0(laplacian, graph.size(), 3.5355e-01, 5.0000e-01, 3.5355e-01, 3.5355e-01, 5.0000e-01, 3.5355e-01);
//  }

//  private void verifyNormLaplacianCol(SSNLOperator laplacian, int size, int col, double... expected) {
//    double[] v = new double[size];
//    for (int i = 0; i < size; i++) v[i] = i == col ? 1 : 0;
//    double[] result = laplacian.apply(v);
//    for (int i = 0; i < size; i++)
//      assertThat("(" + i + ", " + col + ") should match",
//              result[i], closeTo(expected[i], 1E-5));
//  }
//
//  private void verifyV0(SSNLOperator laplacian, int indicesSize, double... expected) {
//    for (int i = 0; i < indicesSize; i++)
//      assertThat("v0 entry at " + i + " should match", laplacian.getV0()[i], closeTo(expected[i], 1E-5));
//  }



}