package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.arrays.ArrayDoubleBuffer;

public class NormalizedLaplacianCSRSubmatrix extends CSRSubmatrix {

  private final DoubleBuffer v0; // The first eigenvector of the original Laplacian (i.e. with corr. eigenval 0)
  private final DoubleBuffer invDegSqrts; // Inverse square roots of degrees

  // TODO: Either move scratch to computeAuxData or copy D^(-0.5)v into it
  private final DoubleBuffer scratch; // Holds first degrees and later the weighted argument vector

  public NormalizedLaplacianCSRSubmatrix(CSRStorage data, IntBuffer indices) {
    super(data, indices);
    invDegSqrts = new ArrayDoubleBuffer(indices.size());
    scratch = new ArrayDoubleBuffer(indices.size());
    v0 = new ArrayDoubleBuffer(indices.size());
    computeAuxData();
  }

  private void computeAuxData() {

    // Compute degrees. Store in scratch. Abuse v0 for storing the ones
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

  }

  void multiplyNormalizedLaplacian(DoubleBuffer v, DoubleBuffer result) {
    // Put weighted argument vector into scratch (D^-1/2 v)
//    for (int i = 0; i < indices.size(); i++) scratch.set(i, invDegSqrts.get(i) * v.get(i));
    NormalizedLaplacianProduct prod = new NormalizedLaplacianProduct(result, invDegSqrts);
    multiply(v, prod);
  }

  public int size() {
    return (int) indices.size();
  }

  @Override
  public void free() {
    super.free();
    scratch.free();
    v0.free();
    invDegSqrts.free();
  }

  static class NormalizedLaplacianProduct implements Product {

    private final DoubleBuffer resultBuf;
    private final DoubleBuffer invDegSqrts;

    NormalizedLaplacianProduct(DoubleBuffer resultBuf, DoubleBuffer invDegSqrts) {
      System.out.println("================================");
      this.resultBuf = resultBuf;
      this.invDegSqrts = invDegSqrts;
    }

    @Override
    public double innerProduct(int i, int j, double aij, double vj) {
//      System.out.printf("Inner %d/%d -> %f\n", i, j, -aij * vj * invDegSqrts.get(j));
      return -aij * vj * invDegSqrts.get(j);
    }

    @Override
    public void createResultEntry(int row, double value, DoubleBuffer arg) {
      // Latter term comes from diagonals

      System.out.println("====== PRE-PROD =========");
      System.out.printf("%d -> %f -- %f -- %f\n", row, value, arg.get(row), invDegSqrts.get(row));


      resultBuf.set(row, invDegSqrts.get(row) * value + arg.get(row));
    }
  }

}
