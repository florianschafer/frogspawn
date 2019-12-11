/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.Graph;

public class RelativeWeightConsistencyMetric implements ConsistencyMetric {

  @Override
  public double[] compute(Graph graph, Graph subgraph) {
    return subgraph.relativeWeights(graph);
  }

}
