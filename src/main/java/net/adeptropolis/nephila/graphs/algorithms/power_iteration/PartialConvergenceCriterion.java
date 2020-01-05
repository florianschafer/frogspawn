/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

/**
 * A relaxed convergence criterion for the power iteration
 */

public interface PartialConvergenceCriterion extends ConvergenceCriterion {

  /**
   * Postprocess a partially converged vector
   *
   * @param v A vector
   */

  void postprocess(double[] v);

}
