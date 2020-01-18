/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.structuring;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.adeptropolis.nephila.Clustering;
import net.adeptropolis.nephila.ClusteringSettings;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ConsistencyMetric;
import net.adeptropolis.nephila.graphs.Graph;

import java.util.function.Consumer;

public class MetaClustering {

  private final Cluster sourceRootCluster;
  private final Graph sourceGraph;
  private final ConsistencyMetric metric;
  private final ClusteringSettings settings;

  public MetaClustering(Cluster sourceRootCluster, Graph sourceGraph, ConsistencyMetric metric, ClusteringSettings settings) {
    this.sourceRootCluster = sourceRootCluster;
    this.sourceGraph = sourceGraph;
    this.metric = metric;
    this.settings = settings;
  }

  public void run(Consumer<MetaCluster> consumer) {
    MetaNode metaNode = MetaGraphBuilder.build(sourceRootCluster, sourceGraph);
    Cluster metaRoot = Clustering.run(metaNode.getGraph(), metric, settings);

    metaRoot.traverseLeafs(leaf -> {
      Graph subgraph = metaNode.getGraph().inducedSubgraph(leaf.getRemainder().iterator());
      WeightSortedVertexSet vertices = new WeightSortedVertexSet(subgraph);
      int[] ids = vertices.getVertices();
      Int2ObjectOpenHashMap<MetaCluster> children = new Int2ObjectOpenHashMap<>();
      for (int i = 0; i < ids.length; i++) {
        if (metaNode.isCluster(ids[i])) {
          children.put(ids[i], metaNode.getCluster(ids[i]));
        }
      }
      consumer.accept(new MetaCluster(vertices, children));
    });

  }

}
