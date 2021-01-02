/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.filters;

import net.adeptropolis.frogspawn.graphs.Graph;

/**
 * Filter a graph using minimum vertex degrees
 */

public class DegreeFilter implements GraphFilter {

  private final long minDegree;
  private final long maxDegree;

  /**
   * Constructor
   *
   * @param minDegree Minimum vertex degree
   * @param maxDegree Maximum vertex degree. May be 0 (no max degree filtering)
   */

  public DegreeFilter(long minDegree, long maxDegree) {
    this.minDegree = minDegree;
    this.maxDegree = maxDegree;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Graph apply(Graph graph) {
    long[] degrees = graph.degrees();
    return graph.subgraph(v -> degrees[v] >= minDegree && (maxDegree <= 0 || degrees[v] <= maxDegree));
  }

}
