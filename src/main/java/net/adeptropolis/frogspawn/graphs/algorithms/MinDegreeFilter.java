/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms;

import net.adeptropolis.frogspawn.graphs.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinDegreeFilter {

  private static final Logger LOG = LoggerFactory.getLogger(MinDegreeFilter.class.getSimpleName());

  public static Graph apply(Graph graph, int minDegree) {
    int originalOrder = graph.order();
    int prevOrder = graph.order();
    while (true) {
      int[] degrees = Degrees.of(graph);
      graph = graph.subgraph(v -> degrees[v] >= minDegree);
      if (prevOrder - graph.order() < 2) {
        LOG.debug("Reduced graph from {} to {}", originalOrder, graph.order());
        return graph;
      }
      prevOrder = graph.order();
    }
  }

}
