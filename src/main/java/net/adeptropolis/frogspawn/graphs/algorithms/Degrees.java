/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms;

import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.traversal.EdgeConsumer;

// TODO: Test
class Degrees implements EdgeConsumer {

  private final int[] degrees;

  private Degrees(Graph graph) {
    this.degrees = new int[graph.order()];
  }

  public static int[] of(Graph graph) {
    Degrees instance = new Degrees(graph);
    graph.traverseParallel(instance);
    return instance.degrees;
  }

  @Override
  public void accept(int u, int v, double weight) {
    degrees[u] += 1;
  }

}
