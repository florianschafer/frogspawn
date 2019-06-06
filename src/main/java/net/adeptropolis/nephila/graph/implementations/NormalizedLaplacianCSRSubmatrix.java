package net.adeptropolis.nephila.graph.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: The code in this class requires A LOT love!

public class NormalizedLaplacianCSRSubmatrix extends CSRSubmatrix {

  private static final Logger LOGGER = LoggerFactory.getLogger(NormalizedLaplacianCSRSubmatrix.class);
  private final double[] v0; // The first eigenvector of the original Laplacian (i.e. with corr. eigenval 0)
  private final double[] invDegSqrts; // Inverse square roots of degrees

  // TODO: Create a vector D^(-0.5)v and use that one for multiplication (instead of invDegSqrts.get(row) * value)

  public NormalizedLaplacianCSRSubmatrix(CSRStorage data, int[] indices) {
    super(data, indices);
    invDegSqrts = new double[indices.length];
    v0 = new double[indices.length];
    computeAuxData();
  }

  private void computeAuxData() {

    // Compute degrees. Store in scratch. Abuse v0 for storing the ones
    double[] scratch = new double[indices.length];
    for (int i = 0; i < indices.length; i++) v0[i] = 1;
    super.multiply(v0, scratch);

    // Compute v0 (the original eigenvector corresponding to eigenvalue 0) AND the inverse degree square roots all at once
    double sum = 0;
    for (int i = 0; i < indices.length; i++) {
      double degSqrt = Math.sqrt(scratch[i]);
      invDegSqrts[i] = 1.0 / degSqrt;
      sum += degSqrt * degSqrt;
    }
    double norm = Math.sqrt(sum);
    for (int i = 0; i < indices.length; i++) v0[i] = Math.sqrt(scratch[i]) / norm;
  }

  public void multiplyNormalizedLaplacian(double[] v, double[] result) {
    // Put weighted argument vector into scratch (D^-1/2 v)
    // for (int i = 0; i < indices.size(); i++) scratch.set(i, invDegSqrts.get(i) * v.get(i));
    NormalizedLaplacianProduct prod = new NormalizedLaplacianProduct(result, invDegSqrts);
    multiply(v, prod);
  }

  // TODO: caller should allocate x. Probably transition to arrays!
  // TODO: Error computation is not optimal!
  public double[] bipartiteLambda2Eigenvector(double precision) {
    double[] x = new double[size()];
    double[] multRes = new double[size()];
    double[] lastResult = new double[size()];
    double initialEntryVal = 1.0 / Math.sqrt(size());
    for (int i = 0; i < size(); i++) {
      // TODO: Find better initial vector, must be ||.|| == 1
      x[i] = initialEntryVal;
      lastResult[i] = initialEntryVal;
    }
    while (true) {
      multiplySpectrallyShiftedNormalizedLaplacian(x, multRes);
      normVec(multRes, x);
      double maxDist = 0;
      for (int i = 0; i < size(); i++) {
        double d = Math.abs(lastResult[i] - x[i]);
        if (d > maxDist) maxDist = d;
      }
      if (maxDist < precision) break;
      for (int i = 0; i < size(); i++) lastResult[i] = x[i];
    }
    return x;
  }

  public int size() {
    return (int) indices.length;
  }

  // NOTE: Biparite graphs only!!! ||v|| must be 1 !!!
  public void multiplySpectrallyShiftedNormalizedLaplacian(double[] v, double[] result) {
//    https://math.stackexchange.com/questions/2214641/shifting-eigenvalues-of-a-matrix
    SpectrallyShiftingNormalizedLaplacianProduct prod = new SpectrallyShiftingNormalizedLaplacianProduct(v, v0, result, invDegSqrts);
    multiply(v, prod);
  }

  // Also normalizes signum, s.t. the first entry is always positive
  private void normVec(double[] x, double[] result) {
    double primSig = Math.signum(x[0]);
    double sum = 0;
    for (int i = 0; i < x.length; i++) sum += x[i] * x[i];
    double norm = Math.sqrt(sum);
    for (int i = 0; i < x.length; i++) result[i] = primSig * x[i] / norm;
  }

  // TODO: call should allocate x. Probably transition to arrays!
  // TODO: Error computation is not optimal!
  public byte[] bipartiteLambda2EigenvectorSignums(double maxAlternations, int minIterations) {
    double[] x = new double[size()];
    double[] multRes = new double[size()];
    byte[] lastSignums = new byte[size()];
    double initialEntryVal = 1.0 / Math.sqrt(size());
    for (int i = 0; i < size(); i++) {
      // TODO: Find better initial value, must be ||.|| == 1
      x[i] = initialEntryVal;
      lastSignums[i] = 1;
    }
    long iterations = 0;
    while (true) {
      multiplySpectrallyShiftedNormalizedLaplacian(x, multRes);
      normVec(multRes, x);
      long signumDist = 0;
      for (int i = 0; i < size(); i++) {
        byte sig = (byte) Math.signum(x[i]);
        signumDist += sig == lastSignums[i] ? 0 : 1;
        lastSignums[i] = sig;
      }
      if (iterations >= minIterations && signumDist / (double) size() <= maxAlternations) break;
      iterations++;
      if (iterations % 1000 == 0) System.out.println("Iterations: " + iterations);
    }

    System.out.println("Iterations: " + iterations);
    for (int i = 0; i < size(); i++) lastSignums[i] = (byte) Math.signum(x[i]);
    return lastSignums;
  }

  static class NormalizedLaplacianProduct implements Product {

    private final double[] resultBuf;
    private final double[] invDegSqrts;

    NormalizedLaplacianProduct(final double[] resultBuf, final double[] invDegSqrts) {
      this.resultBuf = resultBuf;
      this.invDegSqrts = invDegSqrts;
    }

    @Override
    public double innerProduct(int i, int j, double aij, double vj) {
      return -aij * vj * invDegSqrts[j];
    }

    @Override
    public void createResultEntry(int row, double value, double[] arg) {
      // Latter term comes from diagonals
      resultBuf[row] = invDegSqrts[row] * value + arg[row];
    }
  }

  // NOTE!!!: This ONLY works for bipartite graphs!!!
  static class SpectrallyShiftingNormalizedLaplacianProduct implements Product {

    private final double[] resultBuf;
    private final double[] invDegSqrts;
    private final double[] v0;
    private final double scal;

    // NOTE: ||v|| MUST BE 1
    SpectrallyShiftingNormalizedLaplacianProduct(double[] v, double[] v0, double[] resultBuf, double[] invDegSqrts) {
      this.resultBuf = resultBuf;
      this.invDegSqrts = invDegSqrts;
      this.v0 = v0;
      double sum = 0;
      for (int i = 0; i < v.length; i++) sum += v0[i] * v[i];
      this.scal = sum;
    }

    @Override
    public double innerProduct(int i, int j, double aij, double vj) {
      return -aij * vj * invDegSqrts[j];
    }

    @Override
    public void createResultEntry(int row, double value, double[] arg) {
      resultBuf[row] = invDegSqrts[row] * value + 2 * (v0[row] * scal) - arg[row];
    }
  }

}
