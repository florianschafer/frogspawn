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
public class RowWeights implements EdgeConsumer {

  private final View view;
  private final double[] weights;

  public RowWeights(View view) {
    this.view = view;
    this.weights = new double[view.size()];
    view.traverse(this);
  }

  public double[] get() {
    return weights;
  }

  @Override
  public void accept(final int u, final int v, final double weight) {
    weights[u] += weight;
  }

}
