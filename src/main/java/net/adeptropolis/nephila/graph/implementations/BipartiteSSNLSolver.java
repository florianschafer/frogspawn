package net.adeptropolis.nephila.graph.implementations;


/*
* Spectrally shifting solver for the normalized Laplacian eigenvalue problem
* ATTENTION: The Laplacian is supposed to be that of a BIPARTITE GRAPH!
*
* */

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public class BipartiteSSNLSolver {

  private final CSRStorage.View view;
  private final NormalizedLaplacian normalizedLaplacian;
  private final double[] x;
  private final double[] prevY;

  public BipartiteSSNLSolver(CSRStorage.View view) {
    this.view = view;
    this.normalizedLaplacian = new NormalizedLaplacian(view);
    this.x = new double[view.maxSize()];
    this.prevY = new double[view.maxSize()];
  }

  public double[] approxV2(double precision) {
    return powerIteration((iterations) -> {
      double maxDist = 0;
      for (int i = 0; i < view.indicesSize; i++) {
        double d = Math.abs(prevY[i] - x[i]);
        if (d > maxDist) maxDist = d;
      }
      return maxDist < precision;
    });
  }

  public double[] approxV2Signatures(double maxAlternations, int minIterations) {
    return powerIteration((iterations) -> {
      long signumDist = 0;
      for (int i = 0; i < view.indicesSize; i++) {
        byte prevSig = (byte) Math.signum(prevY[i]);
        byte sig = (byte) Math.signum(x[i]);
        signumDist += sig == prevSig ? 0 : 1;
      }
      return iterations >= minIterations && signumDist / (double) view.indicesSize <= maxAlternations;
    });
  }

  // TODO: Find better initial vector, must be ||.|| == 1
  private synchronized double[] powerIteration(Function<Integer, Boolean> terminator) {
    double initialEntry = 1.0 / Math.sqrt(view.indicesSize);
    for (int i = 0; i < view.indicesSize; i++) x[i] = prevY[i] = initialEntry;
    int iterations = 0;
    while (true) {
      double[] y = multiply(x);
      normVec(y, x, view.indicesSize);
      iterations++;
      if (terminator.apply(iterations)) break;
      System.arraycopy(x, 0, prevY, 0, view.indicesSize);
      if (iterations % 100 == 0) System.out.printf("%d iterations\n", iterations);
    }
    System.out.printf("Solver finished after %d iterations\n", iterations);
    return x;
  }

  public void update() {
    normalizedLaplacian.update();
  }

  // !!!! ATTENTION! ||x|| is expected to have length 1 !!!!
  synchronized double[] multiply(double[] x) {
    double mu = 0;
    for (int i = 0; i < view.indicesSize; i++) mu += normalizedLaplacian.getV0()[i] * x[i];
    double[] y = normalizedLaplacian.multiply(x);
    for (int i = 0; i < view.indicesSize; i++) y[i] += 2 * (mu * normalizedLaplacian.getV0()[i] - x[i]);
    return y;
  }

  // Normalize vector <x> into <multResult>
  // Also normalizes the signum of <x>, s.t. the first entry is always positive
  private void normVec(double[] x, double[] result, int size) {
    double sig = Math.signum(x[0]);
    double sum = 0;
    for (int i = 0; i < size; i++) sum += x[i] * x[i];
    double norm = Math.sqrt(sum);
    for (int i = 0; i < size; i++) result[i] = sig * x[i] / norm;
  }

}