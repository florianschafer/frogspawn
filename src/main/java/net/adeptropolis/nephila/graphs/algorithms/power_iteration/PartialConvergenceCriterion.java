/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

/**
 * Criterion allowing for partial convergence of an eigenvector.
 */

public interface PartialConvergenceCriterion extends ConvergenceCriterion {

  /**
   * Postprocess a partially converged vector
   *
   * @param v Vector
   */

  void postprocess(double[] v);

}
