/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms.power_iteration;

/**
 * Criterion allowing for partial convergence of an eigenvector.
 */

public interface PartialConvergenceCriterion extends ConvergenceCriterion {

  /**
   * Postprocess a partially converged vector
   *
   * @param v Vector
   * @throws PartialConvergencePostprocessingException If either the maximum number of iterations was exceeded or the graph is empty
   */

  void postprocess(double[] v) throws PartialConvergencePostprocessingException;

}
