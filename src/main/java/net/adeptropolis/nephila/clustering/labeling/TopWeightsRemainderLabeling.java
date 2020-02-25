/*
 * Copyright (c) Florian Schaefer 2020.
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

public class TopWeightsRemainderLabeling implements Labeling {

  private final int maxLabels;
  private final Graph rootGraph;

  public TopWeightsRemainderLabeling(int maxLabels, Graph rootGraph) {
    this.maxLabels = maxLabels;
    this.rootGraph = rootGraph;
  }


  @Override
  public Labels label(Cluster cluster) {
    Graph graph = rootGraph.inducedSubgraph(cluster.getRemainder().iterator());
    int[] vertices = graph.collectVertices();
    double[] weights = graph.weights();
    double[] likelihoods = graph.relativeWeights(rootGraph);
    WeightSortOps altWeightSortOps = new WeightSortOps(vertices, weights, likelihoods);
    Arrays.mergeSort(0, graph.order(), altWeightSortOps, altWeightSortOps);
    return new Labels(
            Arr.shrink(vertices, maxLabels),
            Arr.shrink(weights, maxLabels),
            Arr.shrink(likelihoods, maxLabels),
            graph.order()
    );
  }

}
