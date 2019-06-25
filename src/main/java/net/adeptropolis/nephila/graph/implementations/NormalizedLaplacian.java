package net.adeptropolis.nephila.graph.implementations;

public class NormalizedLaplacian {

  private final CSRStorage.View view;
  private final RowWeights rowWeights;
  private final double[] multArgument;
  private final CSRVectorProduct halfProduct; // Standard matrix product

  private final double[] v0; // The first eigenvector of the original Laplacian (i.e. with corr. eigenval 0)
  private final double[] invDegSqrts; // Inverse square roots of row weights

  public NormalizedLaplacian(CSRStorage.View view) {
    this.view = view;
    this.rowWeights = new RowWeights(view);
    this.multArgument = new double[view.maxSize()]; // Preallocate a single, reusable instance
    this.v0 = new double[view.maxSize()];
    this.invDegSqrts = new double[view.maxSize()];
    this.halfProduct = new CSRVectorProduct(this.view);
  }

  public synchronized double[] multiply(double[] x) {
    for (int i = 0; i < view.indicesSize; i++) multArgument[i] = -invDegSqrts[i] * x[i];
    double[] multResult = halfProduct.multiply(multArgument);
    for (int i = 0; i < view.indicesSize; i++) multResult[i] = invDegSqrts[i] * multResult[i] + x[i];
    return multResult;
  }

  public double[] getV0() {
    return v0;
  }

  public void update() {
    rowWeights.update();
    double[] weights = rowWeights.get();
    double sqrSum = 0;
    for (int i = 0; i < view.indicesSize; i++) {
      double degSqrt = Math.sqrt(weights[i]);
      invDegSqrts[i] = 1.0 / degSqrt;
      sqrSum += degSqrt * degSqrt;
    }
    double norm = Math.sqrt(sqrSum);
    for (int i = 0; i < view.indicesSize; i++) v0[i] = Math.sqrt(weights[i]) / norm;
  }

}
