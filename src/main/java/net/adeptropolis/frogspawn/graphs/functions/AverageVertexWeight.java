/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.functions;

import net.adeptropolis.frogspawn.graphs.Graph;

public class AverageVertexWeight implements GraphFunction<Double> {

  @Override
  public Double apply(Graph graph) {
    // TODO: Might benefit from traversing only the lower diagonal
    return graph.totalWeight() / graph.order();
  }

}
