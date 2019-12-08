/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms;

import net.adeptropolis.nephila.graphs.implementations.View;

@Deprecated
public class DeprecatedNormalizedLaplacian {

  private final View view;
  private final RowWeights rowWeights;
  private final double[] multArgument;
  private final CSRVectorProduct matProduct; // Standard matrix product

  private final double[] v0; // The first eigenvector of the original Laplacian (i.e. with corr. eigenval 0)
  private final double[] invDegSqrts; // Inverse square roots of row weights

  public DeprecatedNormalizedLaplacian(View view) {
    this.view = view;
    this.rowWeights = new RowWeights(view);
    this.multArgument = new double[view.size()]; // Preallocate a single, reusable instance
    this.v0 = new double[view.size()];
    this.invDegSqrts = new double[view.size()];
    this.matProduct = new CSRVectorProduct(this.view);
    computeAuxVectors();
  }

  private void computeAuxVectors() {
    double[] weights = rowWeights.get();
    double sqrSum = 0;
    for (int i = 0; i < view.size(); i++) {
      double degSqrt = Math.sqrt(weights[i]);
      invDegSqrts[i] = 1.0 / degSqrt;
      sqrSum += degSqrt * degSqrt;
    }
    double norm = Math.sqrt(sqrSum);
    for (int i = 0; i < view.size(); i++) v0[i] = Math.sqrt(weights[i]) / norm;
  }

  public synchronized double[] multiply(double[] x) {
    for (int i = 0; i < view.size(); i++) multArgument[i] = -invDegSqrts[i] * x[i];
    double[] multResult = matProduct.multiply(multArgument);
    for (int i = 0; i < view.size(); i++) multResult[i] = invDegSqrts[i] * multResult[i] + x[i];
    return multResult;
  }

  public double[] getV0() {
    return v0;
  }

}
