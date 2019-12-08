/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms;

import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.implementations.View;

@Deprecated
public class CSRVectorProduct implements EdgeConsumer {

  private final View view;
  private final double[] result;
  private double[] argument;

  public CSRVectorProduct(View view) {
    this.view = view;
    this.result = new double[view.size()]; // Preallocate a single, reusable instance
  }

  public synchronized double[] multiply(double[] argument) {
    this.argument = argument;
    view.traverse(this);
    return result;
  }

  @Override
  public void accept(int u, int v, double weight) {
    // TODO: Do something against range checks on the LHS (extend visitor interface to rows)
    result[u] += weight * argument[v];
  }

}
