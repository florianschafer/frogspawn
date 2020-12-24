/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.filters;

import net.adeptropolis.frogspawn.graphs.Graph;

/**
 * Filter a graph using minimum vertex degrees
 */

public class MinDegreeFilter implements GraphFilter {

  private final long minDegree;

  /**
   * Constructor
   *
   * @param minDegree Minimum vertex degree
   */

  public MinDegreeFilter(long minDegree) {
    this.minDegree = minDegree;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Graph apply(Graph graph) {
    long[] degrees = graph.degrees();
    return graph.subgraph(v -> degrees[v] >= minDegree);
  }

}
