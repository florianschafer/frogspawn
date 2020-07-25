/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.matrices;


/**
 * <p>Square matrix</p>
 */

public interface SquareMatrix {

  /**
   * @param argument A vertex-indexed vector
   * @return Matrix-vector product
   */

  double[] multiply(double[] argument);

  /**
   * @return Matrix size
   */
  int size();

}
