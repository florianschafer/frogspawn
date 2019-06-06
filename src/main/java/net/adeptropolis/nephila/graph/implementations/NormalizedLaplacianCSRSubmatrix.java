package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.primitives.Doubles;
import net.adeptropolis.nephila.graph.implementations.primitives.IntBuffer;
import net.adeptropolis.nephila.graph.implementations.primitives.arrays.ArrayDoubles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: The code in this class requires A LOT love!

public class NormalizedLaplacianCSRSubmatrix extends CSRSubmatrix {

  private static final Logger LOGGER = LoggerFactory.getLogger(NormalizedLaplacianCSRSubmatrix.class);
  private final Doubles v0; // The first eigenvector of the original Laplacian (i.e. with corr. eigenval 0)
  private final Doubles invDegSqrts; // Inverse square roots of degrees

  // TODO: Create a vector D^(-0.5)v and use that one for multiplication (instead of invDegSqrts.get(row) * value)

  public NormalizedLaplacianCSRSubmatrix(CSRStorage data, IntBuffer indices) {
    super(data, indices);
    invDegSqrts = new ArrayDoubles(indices.size());
    v0 = new ArrayDoubles(indices.size());
    computeAuxData();
  }

  private void computeAuxData() {

    // Compute degrees. Store in scratch. Abuse v0 for storing the ones
    ArrayDoubles scratch = new ArrayDoubles(indices.size());
    for (int i = 0; i < indices.size(); i++) v0.set(i, 1);
    super.multiply(v0, scratch);

    // Compute v0 (the original eigenvector corresponding to eigenvalue 0) AND the inverse degree square roots all at once
    double sum = 0;
    for (int i = 0; i < indices.size(); i++) {
      double degSqrt = Math.sqrt(scratch.get(i));
      invDegSqrts.set(i, 1.0 / degSqrt);
      sum += degSqrt * degSqrt;
    }
    double norm = Math.sqrt(sum);
    for (int i = 0; i < indices.size(); i++) v0.set(i, Math.sqrt(scratch.get(i)) / norm);
    scratch.free();

  }

  public void multiplyNormalizedLaplacian(Doubles v, Doubles result) {
    // Put weighted argument vector into scratch (D^-1/2 v)
    // for (int i = 0; i < indices.size(); i++) scratch.set(i, invDegSqrts.get(i) * v.get(i));
    NormalizedLaplacianProduct prod = new NormalizedLaplacianProduct(result, invDegSqrts);
    multiply(v, prod);
  }

  // NOTE: Biparite graphs only!!! ||v|| must be 1 !!!
  public void multiplySpectrallyShiftedNormalizedLaplacian(Doubles v, Doubles result) {
//    https://math.stackexchange.com/questions/2214641/shifting-eigenvalues-of-a-matrix
    SpectrallyShiftingNormalizedLaplacianProduct prod = new SpectrallyShiftingNormalizedLaplacianProduct(v, v0, result, invDegSqrts);
    multiply(v, prod);
  }


  // TODO: caller should allocate x. Probably transition to arrays!
  // TODO: Error computation is not optimal!
  public Doubles bipartiteLambda2Eigenvector(double precision) {
    Doubles x = new ArrayDoubles(size());
    Doubles multRes = new ArrayDoubles(size());
    Doubles lastResult = new ArrayDoubles(size());
    double initialEntryVal = 1.0 / Math.sqrt(size());
    for (int i = 0; i < size(); i++) {
      // TODO: Find better initial vector, must be ||.|| == 1
      x.set(i, initialEntryVal);
      lastResult.set(i, initialEntryVal);
    }
    while (true) {
      multiplySpectrallyShiftedNormalizedLaplacian(x, multRes);
      normVec(multRes, x);
      double maxDist = 0;
      for (int i = 0; i < size(); i++) {
        double d = Math.abs(lastResult.get(i) - x.get(i));
        if (d > maxDist) maxDist = d;
      }
      if (maxDist < precision) break;
      for (int i = 0; i < size(); i++) lastResult.set(i, x.get(i));
    }
    lastResult.free();
    multRes.free();
    return x;
  }

  // TODO: call should allocate x. Probably transition to arrays!
  // TODO: Error computation is not optimal!
  public byte[] bipartiteLambda2EigenvectorSignums(double maxAlternations, int minIterations) {
    Doubles x = new ArrayDoubles(size());
    Doubles multRes = new ArrayDoubles(size());
    byte[] lastSignums = new byte[size()];
    double initialEntryVal = 1.0 / Math.sqrt(size());
    for (int i = 0; i < size(); i++) {
      // TODO: Find better initial value, must be ||.|| == 1
      x.set(i, initialEntryVal);
      lastSignums[i] = 1;
    }
    long iterations = 0;
    while (true) {
      multiplySpectrallyShiftedNormalizedLaplacian(x, multRes);
      normVec(multRes, x);
      long signumDist = 0;
      for (int i = 0; i < size(); i++) {
        byte sig = (byte) Math.signum(x.get(i));
        signumDist += sig == lastSignums[i] ? 0 : 1;
        lastSignums[i] = sig;
      }
      if (iterations >= minIterations && signumDist / (double) size() <= maxAlternations) break;
      iterations++;
      if (iterations % 1000 == 0) System.out.println("Iterations: " + iterations);
    }

    System.out.println("Iterations: " + iterations);
    multRes.free();
    for (int i = 0; i < size(); i++) lastSignums[i] = (byte) Math.signum(x.get(i));
    return lastSignums;
  }

    // Also normalizes signum, s.t. the first entry is always positive
  private void normVec(Doubles x, Doubles result) {
    double primSig = Math.signum(x.get(0));
    double sum = 0;
    for (int i = 0; i < x.size(); i++) sum += x.get(i) * x.get(i);
    double norm = Math.sqrt(sum);
    for (int i = 0; i < x.size(); i++) result.set(i, primSig * x.get(i) / norm);
  }

  public int size() {
    return (int) indices.size();
  }

  @Override
  public void free() {
    super.free();
    v0.free();
    invDegSqrts.free();
  }

  static class NormalizedLaplacianProduct implements Product {

    private final Doubles resultBuf;
    private final Doubles invDegSqrts;

    NormalizedLaplacianProduct(final Doubles resultBuf, final Doubles invDegSqrts) {
      this.resultBuf = resultBuf;
      this.invDegSqrts = invDegSqrts;
    }

    @Override
    public double innerProduct(int i, int j, double aij, double vj) {
      return -aij * vj * invDegSqrts.get(j);
    }

    @Override
    public void createResultEntry(int row, double value, Doubles arg) {
      // Latter term comes from diagonals
      resultBuf.set(row, invDegSqrts.get(row) * value + arg.get(row));
    }
  }

  // NOTE!!!: This ONLY works for bipartite graphs!!!
  static class SpectrallyShiftingNormalizedLaplacianProduct implements Product {

    private final Doubles resultBuf;
    private final Doubles invDegSqrts;
    private final Doubles v0;
    private final double scal;

    // NOTE: ||v|| MUST BE 1
    SpectrallyShiftingNormalizedLaplacianProduct(Doubles v, Doubles v0, Doubles resultBuf, Doubles invDegSqrts) {
      this.resultBuf = resultBuf;
      this.invDegSqrts = invDegSqrts;
      this.v0 = v0;
      double sum = 0;
      for (int i = 0; i < v.size(); i++) sum += v0.get(i) * v.get(i);
      this.scal = sum;
    }

    @Override
    public double innerProduct(int i, int j, double aij, double vj) {
      return -aij * vj * invDegSqrts.get(j);
    }

    @Override
    public void createResultEntry(int row, double value, Doubles arg) {
      resultBuf.set(row, invDegSqrts.get(row) * value + 2 * (v0.get(row) * scal) - arg.get(row));
    }
  }

}
