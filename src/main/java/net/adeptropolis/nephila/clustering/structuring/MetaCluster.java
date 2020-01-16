/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.structuring;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class MetaCluster {

  private final WeightSortedVertexSet vertexSet;
  private final Int2ObjectOpenHashMap<WeightSortedVertexSet> children; // Indexed by lowest child id

  public MetaCluster(WeightSortedVertexSet vertexSet, Int2ObjectOpenHashMap<WeightSortedVertexSet> children) {
    this.vertexSet = vertexSet;
    this.children = children;
  }

  public MetaCluster(WeightSortedVertexSet vertexSet) {
    this(vertexSet, new Int2ObjectOpenHashMap<>());
  }


}
