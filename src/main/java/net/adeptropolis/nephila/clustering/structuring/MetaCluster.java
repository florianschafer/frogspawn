/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.structuring;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MetaCluster {

  private final WeightSortedVertexSet vertexSet;
  private final Int2ObjectOpenHashMap<MetaCluster> children;

  public MetaCluster(WeightSortedVertexSet vertexSet, Int2ObjectOpenHashMap<MetaCluster> children) {
    this.vertexSet = vertexSet;
    this.children = children;
  }

  public MetaCluster(WeightSortedVertexSet vertexSet) {
    this(vertexSet, new Int2ObjectOpenHashMap<>());
  }

  public <T> String stringify(int maxSize, T[] dict) {
    return Arrays.stream(vertexSet.getVertices()).mapToObj(v -> {
      if (children.containsKey(v)) {
        return String.format("{%s}", children.get(v).stringify(maxSize, dict));
      }
      return dict[v].toString();
    }).limit(maxSize).collect(Collectors.joining(", "));
  }


}
