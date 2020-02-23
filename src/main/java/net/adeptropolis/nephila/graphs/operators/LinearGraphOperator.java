/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.operators;


/**
 * <p>A linear graph operator</p>
 */

public interface LinearGraphOperator {

  /**
   * @param argument A vertex-indexed vector
   * @return Apply the given operator to the argument vector and return the result.
   */

  // TODO: Supply the preallocated result vector as second argument, so it may be re-used at a higher level
  double[] apply(double[] argument);

  /**
   * Return size
   *
   * @return Size of the operator
   */
  int size();
}
