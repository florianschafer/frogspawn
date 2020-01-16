/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.structuring;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.adeptropolis.nephila.graphs.Graph;

public class MetaGraph {

  private final Graph graph;
  private final Int2ObjectOpenHashMap<MetaCluster> childClusters;

  public MetaGraph(Graph graph, Int2ObjectOpenHashMap<MetaCluster> childClusters) {
    this.graph = graph;
    this.childClusters = childClusters;
  }

  public Graph getGraph() {
    return graph;
  }

  public ObjectCollection<MetaCluster> getChildClusters() {
    return childClusters.values();
  }

  public Int2ObjectOpenHashMap<MetaCluster> getChildClustersMap() {
    return childClusters;
  }

}
