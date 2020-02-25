/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.clustering.labeling;

import it.unimi.dsi.fastutil.Arrays;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.helpers.Arr;

/**
 * Returns the labels of the full aggregated subgraph, sorted by frequency
 */

public class TopWeightsAggregateLabeling implements Labeling {

  private final int maxLabels;
  private final Graph rootGraph;

  public TopWeightsAggregateLabeling(int maxLabels, Graph rootGraph) {
    this.maxLabels = maxLabels;
    this.rootGraph = rootGraph;
  }

  @Override
  public Labels label(Cluster cluster) {
    Graph graph = cluster.aggregateGraph(rootGraph);
    int[] vertices = graph.collectVertices();
    double[] weights = graph.weights();
    double[] likelihoods = graph.relativeWeights(rootGraph);
    WeightSortOps weightSortOps = new WeightSortOps(vertices, weights, likelihoods);
    Arrays.mergeSort(0, graph.order(), weightSortOps, weightSortOps);
    return new Labels(
            Arr.shrink(vertices, maxLabels),
            Arr.shrink(weights, maxLabels),
            Arr.shrink(likelihoods, maxLabels),
            graph.order()
    );
  }

}
