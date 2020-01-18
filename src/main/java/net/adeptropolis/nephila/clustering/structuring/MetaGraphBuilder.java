/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.structuring;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;

import java.util.function.Consumer;

public class MetaGraphBuilder {

  // TODO: Change constructor to accept MetaGraphs as input
  public static MetaNode build(Cluster root, Graph rootGraph) {

    // The mapping below is (any id from the cluster) -> cluster
    Int2ObjectOpenHashMap<MetaCluster> childClusters = new Int2ObjectOpenHashMap<>();
    // Reduces any cluster member to the above id
    Int2IntOpenHashMap reductionMap = new Int2IntOpenHashMap();

    traverseLeafClusters(root, leaf -> {
      WeightSortedVertexSet leafVertices = new WeightSortedVertexSet(rootGraph.inducedSubgraph(leaf.iterator()));
      MetaCluster metaCluster = new MetaCluster(leafVertices);
      int leafId = leafVertices.getVertices()[0]; // Any, really.
      childClusters.put(leafId, metaCluster);
      updateReductionMap(reductionMap, leafId, leafVertices);
    });

    Graph graph = buildGraph(rootGraph, reductionMap);
    return new MetaNode(graph, childClusters);
  }

  private static void updateReductionMap(Int2IntOpenHashMap reductionMap, int leafId, WeightSortedVertexSet leafVertices) {
    for (int i = 0; i < leafVertices.size(); i++) {
      reductionMap.put(leafVertices.getVertices()[i], leafId);
    }
  }

  private static Graph buildGraph(Graph rootGraph, Int2IntOpenHashMap reductionMapping) {
    CompressedSparseGraphBuilder builder = CompressedSparseGraph.builder();
    IntOpenHashSet usedVertices = new IntOpenHashSet();
    for (int i = 0; i < rootGraph.size(); i++) {
      rootGraph.traverse(i, (u, v, w) -> {
//        if (!(reductionMapping.containsKey(u) || reductionMapping.containsKey(v))) {
//          builder.add(u, v, w);
//          usedVertices.add(u);
//          usedVertices.add(v);
//        }
        int uRed = reductionMapping.getOrDefault(u, u);
        int vRed = reductionMapping.getOrDefault(v, v);
        usedVertices.add(uRed);
        usedVertices.add(vRed);
        builder.add(uRed, vRed, w);
      });
    }
    return builder.build().inducedSubgraph(usedVertices.iterator());
  }

  private static void traverseLeafClusters(Cluster root, Consumer<IntArrayList> consumer) {
    root.traverse(cluster -> {
      if (cluster.getChildren().isEmpty() && !cluster.getRemainder().isEmpty()) {
        consumer.accept(cluster.getRemainder());
      }
    });
  }

}
