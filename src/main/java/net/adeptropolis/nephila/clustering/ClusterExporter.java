/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.PrintWriter;
import java.util.stream.Collectors;

public class ClusterExporter {

  private final Object2IntOpenHashMap<Cluster> clusterIndex;

  public ClusterExporter() {
    this.clusterIndex = new Object2IntOpenHashMap<>();
  }

  public void export(Cluster root, PrintWriter writer) {
    root.traverse(cluster -> {
      int idx = clusterIdx(cluster);
      int parent = (cluster.getParent() != null) ? clusterIdx(cluster.getParent()) : -1;
      String mappedRemainder = collectMappedRemainder(cluster);
      writer.printf("%d\t%s\t%s\n", idx, parent, mappedRemainder);
    });
  }

  private String collectMappedRemainder(Cluster cluster) {
    return cluster.getRemainder().stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
  }

  private String collectMappedChildren(Cluster cluster) {
    return cluster.getChildren().stream()
            .mapToInt(this::clusterIdx)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining(","));
  }

  private int clusterIdx(Cluster cluster) {
    if (clusterIndex.containsKey(cluster)) {
      return clusterIndex.getInt(cluster);
    } else {
      clusterIndex.put(cluster, clusterIndex.size());
      return clusterIdx(cluster);
    }
  }

}
