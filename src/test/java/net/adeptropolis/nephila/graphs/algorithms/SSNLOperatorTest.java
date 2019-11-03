package net.adeptropolis.nephila.graphs.algorithms;

import net.adeptropolis.nephila.graphs.GraphTestBase;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

// TODO: Test multiplication too!

public class SSNLOperatorTest extends GraphTestBase {

  private final double INV_SQRT_8 = 1.0 / Math.sqrt(8);
  private final double ONE_HALF = 1.0 / 2;

  @Test
  public void v0() {
    double[] expected = new double[]{ INV_SQRT_8, ONE_HALF, INV_SQRT_8, INV_SQRT_8, ONE_HALF, INV_SQRT_8 };
    double[] v0 = SSNLOperator.computeV0(EIGEN_REF_GRAPH.computeWeights());
    for (int i = 0; i < expected.length; i++) {
      assertThat(v0[i], closeTo(expected[i], 1E-6));
    }
  }

  @Test
  public void someOperatorMatrix() {
    double[] expected = new double[]{
               0.609756, -0.069140, -0.487805, -0.071344,  0.505583, -0.351761,
              -0.069140,  0.829268,  0.055312, -0.204063, -0.214024, -0.232668,
              -0.487805,  0.055312,  0.390244,  0.057075, -0.404466,  0.281409,
              -0.071344, -0.204063,  0.057075,  0.756098, -0.255807, -0.278092,
               0.505583, -0.214024, -0.404466, -0.255807,  0.731707, -0.291665,
              -0.351761, -0.232668,  0.281409, -0.278092, -0.291665,  0.682927,
            };

    double[] m = getAsMatrix(new SSNLOperator(SOME_BIPARTITE_GRAPH), SOME_BIPARTITE_GRAPH.size());
    for (int i = 0; i < SOME_BIPARTITE_GRAPH.size() * SOME_BIPARTITE_GRAPH.size(); i++) {
      assertThat(m[i], closeTo(expected[i], 1E-6));
    }
  }

  @Test
  public void K43OperatorMatrix() {
    double[] expected = new double[]{
               0.93554688, -0.02515966, -0.12239410, -0.01261441, -0.14412157,  0.02817973, -0.15667666,
              -0.02515966,  0.80468750, -0.00222013, -0.26637074, -0.00954878, -0.29361907,  0.02086804,
              -0.12239410, -0.00222013,  0.76757812, -0.00154911, -0.27368153,  0.00288216, -0.29752319,
              -0.01261441, -0.26637074, -0.00154911,  0.63671875,  0.00604270, -0.40044302,  0.00084095,
              -0.14412157, -0.00954878, -0.27368153,  0.00604270,  0.67773438,  0.00086983, -0.35033967,
               0.02817973, -0.29361907,  0.00288216, -0.40044302,  0.00086983,  0.55859375, -0.01464412,
              -0.15667666,  0.02086804, -0.29752319,  0.00084095, -0.35033967, -0.01464412,  0.61914062,
            };

    double[] m = getAsMatrix(new SSNLOperator(K43), K43.size());


    IntStream.range(0, 7).forEach(i -> {
      String collect = IntStream.range(0, 7).mapToObj(j -> String.valueOf(m[7 * i + j])).collect(Collectors.joining(", "));
      System.out.println(collect);
    });


    for (int i = 0; i < K43.size() * K43.size(); i++) {
      assertThat(m[i], closeTo(expected[i], 1E-6));
    }
  }

//  @Test
//  public void defaultBipartiteEigenvector() {
//    for (int i = 0; i < 6; i++) {
//      int finalI = i;
//      double[] base = IntStream.range(0, 6).mapToDouble(j -> j != finalI ? 0 : 1).toArray();
//      double[] apply = new SSNLOperator(SOME_BIPARTITE_GRAPH).apply(base);
//      System.out.println(Arrays.stream(apply).mapToObj(String::valueOf).collect(Collectors.joining(", ")));
//    }
//
//
//  }

  private double[] getAsMatrix(SSNLOperator op, int n) {
    double[] matrix = new double[n * n];
    IntStream.range(0, n).forEach(i -> {
      double[] basisVec = IntStream.range(0, n).mapToDouble(j -> j != i ? 0 : 1).toArray();
      double[] r = op.apply(basisVec);
      IntStream.range(0, n).forEach(j -> matrix[i * n + j] = r[j]);
    });
    return matrix;
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