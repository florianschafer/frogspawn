/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.algorithms.RowWeights;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphDatastore;
import net.adeptropolis.nephila.graphs.implementations.View;
import net.adeptropolis.nephila.helpers.Arr;

import java.util.Arrays;

@Deprecated
public class ClusteringTemplate {

  private final View rootView;
  private final double[] rootWeights;

  public ClusteringTemplate(CompressedSparseGraphDatastore graph) {
    this.rootView = graph.defaultView();
    this.rootWeights = new RowWeights(this.rootView).get();
  }

  public double[] globalOverlap(View partition) {
    double[] partitionWeights = new RowWeights(partition).get();
    return relOverlap(partition, partitionWeights, rootView, rootWeights);
  }

  // !!! |refPartition| > |partition|
  public double overlapScore(View partition, View refPartition) {
    double[] weights = new RowWeights(partition).get();
    double[] refWeights = new RowWeights(refPartition).get();

    double refWeight = 0;
    double weight = 0;

    for (int i = 0; i < partition.size(); i++) {
      int idx = refPartition.getIndex(partition.getVertex(i));
      if (idx >= 0) {
        refWeight += refWeights[idx];
        weight += weights[i];
      }
    }

    return (refWeight > 0) ? weight / refWeight : 0.0;
  }

  // !!! |refPartition| > |partition|
  private double[] relOverlap(View partition, double[] weights, View refPartition, double[] refWeights) {
    double[] cuts = new double[partition.size()];
    for (int i = 0; i < partition.size(); i++) {
      double refWeight = refWeights[refPartition.getIndex(partition.getVertex(i))];
      double weight = weights[i];
      cuts[i] = (refWeight > 0) ? weight / refWeight : 0;
    }
    return cuts;
  }

  public ClusterMetrics aggregateMetrics(DeprecatedCluster cluster) {

    int[] aggregateVertices = cluster.aggregateVertices().toIntArray();
    Arrays.parallelSort(aggregateVertices);
    View aggregateView = rootView.subview(aggregateVertices);

    double[] aggregateWeights = new RowWeights(aggregateView).get();

    double[] likelihood = relOverlap(aggregateView, aggregateWeights, rootView, rootWeights);
//    double[] scores = new double[aggregateVertices.length];

    // NOTE: The > 0 is actually a dirty hack around the fact that when Structure re-arranges a cluster
    // In the post-recursion step and a parent is overstepped, there is a chance that some adjacent vertices are no longer there
    // The proper solution would be to re-run ensureConsistency here (and thus take care of the fallout)
    // However, note that this doesn't really happen that often
    // DEBUG CODE:
    // long count = IntStream.range(0, aggregateView.size()).filter(i -> aggregateWeights[i] == 0).count();
    // System.out.printf("Lost %d / %d vertices\n", count, aggregateWeights.length);
//    for (int i = 0; i < aggregateView.size(); i++) scores[i] = aggregateWeights[i] > 0 ? aggregateWeights[i] /* Math.log(aggregateWeights[i]) * aggregateConsistencies[i] */ :  0;
    it.unimi.dsi.fastutil.Arrays.mergeSort(0, aggregateVertices.length,
            (i, j) -> Double.compare(aggregateWeights[j], aggregateWeights[i]),
            (i, j) -> {
              Arr.swap(aggregateVertices, i, j);
              Arr.swap(aggregateWeights, i, j);
            });

    return new ClusterMetrics(aggregateVertices, aggregateWeights, likelihood);

  }

  View getRootView() {
    return rootView;
  }
}
