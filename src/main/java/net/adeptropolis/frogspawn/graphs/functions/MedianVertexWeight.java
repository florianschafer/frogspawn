/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.functions;

import net.adeptropolis.frogspawn.graphs.Graph;

import java.util.Arrays;

public class MedianVertexWeight implements GraphFunction<Double> {

  @Override
  public Double apply(Graph graph) {

    if (graph.order() == 0) {
      return 0d;
    }

    double[] weights = Arrays.copyOf(graph.weights(), graph.weights().length);
    Arrays.parallelSort(weights);
    return weights[weights.length >> 1];
  }

}
