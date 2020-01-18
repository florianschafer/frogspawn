/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.structuring;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.adeptropolis.nephila.graphs.Graph;

public class MetaNode {

  private final Graph graph;
  private final Int2ObjectOpenHashMap<MetaCluster> childClusters;

  public MetaNode(Graph graph, Int2ObjectOpenHashMap<MetaCluster> childClusters) {
    this.graph = graph;
    this.childClusters = childClusters;
  }

  public Graph getGraph() {
    return graph;
  }

  public boolean isCluster(int v) {
    return childClusters.containsKey(v);
  }

  public MetaCluster getCluster(int v) {
    return childClusters.get(v);
  }

}
