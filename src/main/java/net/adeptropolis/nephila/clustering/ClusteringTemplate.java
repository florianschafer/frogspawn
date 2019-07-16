package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graph.implementations.CSRStorage;
import net.adeptropolis.nephila.graph.implementations.CSRStorage.View;
import net.adeptropolis.nephila.graph.implementations.RowWeights;
import net.adeptropolis.nephila.helpers.Arr;

import java.util.Arrays;


public class ClusteringTemplate {

  private final View rootView;
  private final double[] rootWeights;

  public ClusteringTemplate(CSRStorage graph) {
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
      refWeight += refWeights[refPartition.getIndex(partition.get(i))];
      weight += weights[i];
      System.out.printf("%f -- %f", refWeight, weight);
    }

    return (refWeight > 0) ? weight / refWeight : 0.0;
  }

  // !!! |refPartition| > |partition|
  private double[] relOverlap(View partition, double[] weights, View refPartition, double[] refWeights) {
    double[] cuts = new double[partition.size()];
    for (int i = 0; i < partition.size(); i++) {
      double refWeight = refWeights[refPartition.getIndex(partition.get(i))];
      double weight = weights[i];
      cuts[i] = (refWeight > 0) ? weight / refWeight : 0;
    }
    return cuts;
  }

  public ClusterMetrics aggregateMetrics(Cluster cluster) {

    int[] aggregateVertices = cluster.aggregateVertices().toIntArray();
    Arrays.parallelSort(aggregateVertices);
    View aggregateView = rootView.subview(aggregateVertices);

    double[] aggregateWeights = new RowWeights(aggregateView).get();

    double[] aggregateConsistencies = relOverlap(aggregateView, aggregateWeights, rootView, rootWeights);
    double[] scores = new double[aggregateVertices.length];
    for (int i = 0; i < aggregateView.size(); i++) scores[i] = Math.log(aggregateWeights[i]) * aggregateConsistencies[i];

    it.unimi.dsi.fastutil.Arrays.mergeSort(0, aggregateVertices.length,
            (i, j) -> Double.compare(scores[j], scores[i]),
            (i, j) -> {
              Arr.swap(aggregateVertices, i, j);
              Arr.swap(scores, i, j); });

    return new ClusterMetrics(aggregateVertices, scores);

  }

  View getRootView() {
    return rootView;
  }
}
