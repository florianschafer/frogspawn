/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.sinks;

import com.google.common.collect.Lists;
import net.adeptropolis.nephila.clustering.ClusteringTemplate;
import net.adeptropolis.nephila.clustering.DeprecatedCluster;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class TopLeafSink implements HierarchySink<List<String>> {

  private final int numLabels;

  public TopLeafSink(int numLabels) {
    this.numLabels = numLabels;
  }

  @Override
  public List<String> consume(ClusteringTemplate template, DeprecatedCluster root, String[] labelMap) {
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
