/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.Graph;

@FunctionalInterface
public interface ConsistencyMetric {

  double[] compute(Graph graph, Graph subgraph);

}
