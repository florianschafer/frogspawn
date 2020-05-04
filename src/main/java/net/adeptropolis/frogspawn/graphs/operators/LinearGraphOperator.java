/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.operators;


/**
 * <p>A linear graph operator</p>
 */

public interface LinearGraphOperator {

  /**
   * @param argument A vertex-indexed vector
   * @return Apply the given operator to the argument vector and return the result.
   */

  double[] apply(double[] argument);

  /**
   * @return Size of the operator
   */
  int size();
}
