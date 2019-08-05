package net.adeptropolis.nephila.clustering;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TopLeafSink implements HierarchySink<List<String>> {

  private final int numLabels;

  public TopLeafSink(int numLabels) {
    this.numLabels = numLabels;
  }

  @Override
  public List<String> consume(ClusteringTemplate template, Cluster root, String[] labelMap) {
    List<String> topRepresentatives = Lists.newArrayList();
    root.traverseSubclusters(cluster -> {
      if (cluster.getChildren().size() == 0) {
        int[] sortedVertices = template.aggregateMetrics(cluster).getSortedVertices();
        String label = Arrays.stream(sortedVertices)
                .limit(numLabels)
                .mapToObj(sortedVertex -> labelMap[sortedVertex])
                .collect(Collectors.joining(", "));
        topRepresentatives.add(String.format("%d: %s", sortedVertices.length, label));
      }
    });
    return topRepresentatives;
  }

}
