package net.adeptropolis.nephila.graphs.algorithms;


/*
 * Spectrally shifting solver for the normalized Laplacian eigenvalue problem
 * ATTENTION: The Laplacian is supposed to be that of a BIPARTITE GRAPH!
 *
 *
 * The Maths
 * ==========
 *
 * This solver finds the eigenvector associated with the second-smallest eigenvalue of the Normalized Laplacian
 * of the graph.
 *
 * */

import net.adeptropolis.nephila.graphs.implementations.View;

import java.util.function.Function;

public class BipartiteSSNLSolver {

  private final View view;
  private final DeprecatedNormalizedLaplacian normalizedLaplacian;
  private final double[] x;
  private final double[] prevY;

  public BipartiteSSNLSolver(View view) {
    this.view = view;
    this.normalizedLaplacian = new DeprecatedNormalizedLaplacian(view);
    this.x = new double[view.size()];
    this.prevY = new double[view.size()];
  }

  public double[] approxV2(double precision) {
    return powerIteration((iterations) -> {
      double maxDist = 0;
      for (int i = 0; i < view.size(); i++) {
        double d = Math.abs(prevY[i] - x[i]);
        if (d > maxDist) maxDist = d;
      }
      return maxDist < precision;
    });
  }

  // TODO: Find better initial vector, must be ||.|| == 1
  private synchronized double[] powerIteration(Function<Integer, Boolean> terminator) {
    long startTime = System.nanoTime();
    double initialEntry = 1.0 / Math.sqrt(view.size());
    for (int i = 0; i < view.size(); i++) x[i] = prevY[i] = initialEntry;
    int iterations = 0;
    while (true) {
      double[] y = multiply(x);
      normVec(y, x, view.size());
      iterations++;
      if (terminator.apply(iterations)) break;
      System.arraycopy(x, 0, prevY, 0, view.size());
    }

    long duration = System.nanoTime() - startTime;
    // TODO: Logging (TRACE/DEBUG)
    //System.out.printf("%d entries converged after %d iterations in %dms\n", view.size(), iterations, duration / 1000000);
    return x;
  }

  synchronized double[] multiply(double[] x) {
    double mu = 0;
    for (int i = 0; i < view.size(); i++) mu += normalizedLaplacian.getV0()[i] * x[i];
    double[] y = normalizedLaplacian.multiply(x);
    for (int i = 0; i < view.size(); i++) y[i] += 2 * (mu * normalizedLaplacian.getV0()[i] - x[i]);
    return y;
  }

  // Normalize vector <x> into <multResult>
  // Also normalizes the signum of <x>, s.t. the first entry is always positive
  private void normVec(double[] vec, double[] result, int size) {
    double sig = Math.signum(vec[0]);
    double sum = 0;
    for (int i = 0; i < size; i++) sum += vec[i] * vec[i];
    double norm = Math.sqrt(sum);
    for (int i = 0; i < size; i++) result[i] = sig * vec[i] / norm;
  }

  public double[] approxV2Signatures(double maxAlternations, int minIterations, int maxIterations) {
    return powerIteration((iterations) -> {
      long signumDist = 0;
      for (int i = 0; i < view.size(); i++) {
        byte prevSig = (byte) Math.signum(prevY[i]);
        byte sig = (byte) Math.signum(x[i]);
        signumDist += sig == prevSig ? 0 : 1;
      }
      if (iterations >= maxIterations) {
        System.out.printf("WARN: Terminated after %d iterations. Alternation ratio is %f\n", iterations, signumDist / (double) view.size());
        return true;
      }
      return iterations >= minIterations && signumDist / (double) view.size() <= maxAlternations;
    });
  }

}
