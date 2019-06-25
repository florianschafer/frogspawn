package net.adeptropolis.nephila.graph.implementations;

public class SpectrallyShiftedNormalizedLaplacian {

  private final CSRStorage.View view;
  private final CSRVectorProduct csrVectorProduct;
  private final RowWeights rowWeights;
  private final double[] v0; // The first eigenvector of the original Laplacian (i.e. with corr. eigenval 0)
  private final double[] invDegSqrts; // Inverse square roots of row weights

  private final double[] multArgument;

  public SpectrallyShiftedNormalizedLaplacian(CSRStorage.View view) {
    this.view = view;
    this.multArgument = new double[view.maxSize()];
    this.csrVectorProduct = new CSRVectorProduct(this.view);
    this.rowWeights = new RowWeights(view);

    this.v0 = new double[view.maxSize()];
    this.invDegSqrts = new double[view.maxSize()];
  }

  public SpectrallyShiftedNormalizedLaplacian update() {
    rowWeights.update();
    updateAuxiliaryVectors();
    return this;
  }

  // !!!! ATTENTION! ||x|| is expected to have length 1 !!!!
  // !!!! ATTENTION! Modifies v in place !!!!
  // !!!! ATTENTION! Matrix diagonals are expected to be 0 !!!!
  public synchronized void multiply(double[] x) {
    for (int i = 0; i < view.indicesSize; i++) multArgument[i] = -invDegSqrts[i] * x[i];
    double[] multResult = csrVectorProduct.multiply(multArgument);
    double mu = 0;
    for (int i = 0; i < view.indicesSize; i++) mu += v0[i] * x[i];
    for (int i = 0; i < view.indicesSize; i++) x[i] = invDegSqrts[i] * multResult[i] + mu * v0[i] - x[i];
//    for (int i = 0; i < view.indicesSize; i++) x[i] = invDegSqrts[i] * multResult[i] + 2 * ( mu * v0[i]);// - x[i];
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

  private void updateAuxiliaryVectors() {
    // Compute v0 (the original eigenvector corresponding to eigenvalue 0) AND the inverse degree square roots all at once
    double[] deg = rowWeights.get();
    double sqrSum = 0;
    for (int i = 0; i < view.indicesSize; i++) {
      double degSqrt = Math.sqrt(deg[i]);
      invDegSqrts[i] = 1.0 / degSqrt;
      sqrSum += degSqrt * degSqrt;
    }
    double norm = Math.sqrt(sqrSum);
    for (int i = 0; i < view.indicesSize; i++) v0[i] = Math.sqrt(deg[i]) / norm;
  }

}
