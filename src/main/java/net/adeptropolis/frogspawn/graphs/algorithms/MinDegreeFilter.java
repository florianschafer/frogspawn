/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms;

import net.adeptropolis.frogspawn.graphs.Graph;

public class MinDegreeFilter {

  public static Graph apply(Graph graph, int minDegree) {
    int prevOrder = -1;
    while (true) {
      int[] degrees = Degrees.of(graph);
      graph = graph.subgraph(v -> degrees[v] >= minDegree);
      if (graph.order() == prevOrder) {
        return graph;
      }
      prevOrder = graph.order();
    }
  }

}
