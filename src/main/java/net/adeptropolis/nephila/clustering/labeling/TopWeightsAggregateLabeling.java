/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.labeling;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;
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
    int[] vertices = collectVertices(graph);
    double[] weights = graph.weights();
    double[] likelihoods = graph.relativeWeights(rootGraph);
    CandidateSortOps candidateSortOps = new CandidateSortOps(vertices, weights, likelihoods);
    Arrays.mergeSort(0, graph.size(), candidateSortOps, candidateSortOps);
    return new Labels(
            Arr.shrink(vertices, maxLabels),
            Arr.shrink(weights, maxLabels),
            Arr.shrink(likelihoods, maxLabels),
            graph.size()
    );
  }

  TODO: Create another implementation with below remainder-only version and kill the recent meta stuff
  Also: Think about if the ancestor postfilter could be applied as an in-place criterion
  Same for singleton postfilter. Make threadsafe while at it.

//  @Override
//  public Labels label(Cluster cluster) {
//    Graph graph = rootGraph.inducedSubgraph(cluster.getRemainder().iterator());
//    int[] vertices = collectVertices(graph);
//    double[] weights = graph.weights();
//    double[] likelihoods = graph.relativeWeights(rootGraph);
//    CandidateSortOps altCandidateSortOps = new CandidateSortOps(vertices, weights, likelihoods);
//    Arrays.mergeSort(0, graph.size(), altCandidateSortOps, altCandidateSortOps);
//    return new Labels(
//            Arr.shrink(vertices, maxLabels),
//            Arr.shrink(weights, maxLabels),
//            Arr.shrink(likelihoods, maxLabels),
//            graph.size()
//    );
//  }

  private int[] collectVertices(Graph graph) {
    int[] vertices = new int[graph.size()];
    VertexIterator it = graph.vertexIterator();
    while (it.hasNext()) {
      vertices[it.localId()] = it.globalId();
    }
    return vertices;
  }

  private static class CandidateSortOps implements IntComparator, Swapper {

    private final int[] vertices;
    private final double[] weights;
    private final double[] likelihoods;

    private CandidateSortOps(int[] vertices, double[] weights, double[] likelihoods) {
      this.vertices = vertices;
      this.weights = weights;
      this.likelihoods = likelihoods;
    }

    @Override
    public void swap(int i, int j) {
      Arr.swap(vertices, i, j);
      Arr.swap(weights, i, j);
      Arr.swap(likelihoods, i, j);
    }

    @Override
    public int compare(int i, int j) {
      return Double.compare(weights[j], weights[i]);
    }

  }

}
