/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.filters;

import net.adeptropolis.frogspawn.graphs.Graph;

@FunctionalInterface
public interface GraphFilter {

  /**
   * Apply the filter iteratively until no further changes are made
   *
   * @param graph Input graph
   * @return Filtered graph
   */

  default Graph applyIteratively(Graph graph) {
    int prevOrder = graph.order();
    while (true) {
      graph = apply(graph);
      if (prevOrder == graph.order()) {
        return graph;
      }
      prevOrder = graph.order();
    }
  }

  /**
   * Apply a filter to a graph
   *
   * @param graph Input graph
   * @return Filtered graph
   */

  Graph apply(Graph graph);

}
