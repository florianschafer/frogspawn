package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graph.implementations.CSRStorage;
import net.adeptropolis.nephila.graph.implementations.CSRStorage.View;
import net.adeptropolis.nephila.graph.implementations.RowWeights;
import net.adeptropolis.nephila.helpers.Arr;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ClusteringTemplate {

  private final View rootView;
  private final double[] rootWeights;

  public ClusteringTemplate(CSRStorage graph) {
    this.rootView = graph.defaultView();
    this.rootWeights = new RowWeights(this.rootView).get();
  }

  public double[] computeVertexConsistencies(View partition) {
    double[] partitionWeights = new RowWeights(partition).get();
    return computeVertexConsistencies(partition, partitionWeights);
  }

  private double[] computeVertexConsistencies(View partition, double[] partitionWeights) {
    double[] cuts = new double[partition.size()];
    for (int i = 0; i < partition.size(); i++) {
      double weightRelToRoot = rootWeights[rootView.getIndex(partition.get(i))];
      double weightRelToChild = partitionWeights[i];
      cuts[i] = (weightRelToRoot > 0) ? weightRelToChild / weightRelToRoot : 0;
    }
    return cuts;
  }

  public ClusterMetrics aggregateMetrics(Cluster cluster) {

    int[] aggregateVertices = cluster.aggregateVertices().toIntArray();
    Arrays.parallelSort(aggregateVertices);
    View aggregateView = rootView.subview(aggregateVertices);

    double[] aggregateWeights = new RowWeights(aggregateView).get();

    double[] aggregateConsistencies = computeVertexConsistencies(aggregateView, aggregateWeights);
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
