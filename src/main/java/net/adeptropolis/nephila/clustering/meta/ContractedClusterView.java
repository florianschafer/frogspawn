/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.meta;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.WeightSortedVertexSet;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ContractedClusterView {

  private static final Logger LOG = LoggerFactory.getLogger(CompressedSparseGraph.class.getSimpleName());

  private final List<WeightSortedVertexSet> clusterVertices;
  private final Graph graph;

  public ContractedClusterView(Cluster root, Graph rootGraph) {
    clusterVertices = Lists.newArrayList();
    graph = createGraph(root, rootGraph);
  }

  private static List<Cluster> flattenClusters(Cluster root) {
    List<Cluster> clusters = Lists.newArrayList();
    root.traverse(clusters::add);
    return clusters;
  }

  private CompressedSparseGraph createGraph(Cluster root, Graph rootGraph) {
    List<Cluster> clusters = flattenClusters(root);
    Int2IntOpenHashMap vertexMap = buildMaps(clusters, rootGraph);
    CompressedSparseGraphBuilder builder = CompressedSparseGraph.builder();
    rootGraph.traverseEdgesSequential((u, v, w) -> {
      // TODO: If the vertexMap condition below would always hold true, vertexMap could be a list (s. lost vertices bug)
      if (u < v && vertexMap.containsKey(u) && vertexMap.containsKey(v)) { // Canonical edge (TODO: check!)
        builder.add(vertexMap.get(u), vertexMap.get(v), w);
      }
    });
    return builder.build();
  }

  /**
   * Note: Two concerns are mixed up here: a) build the vertex map and b) populate the clusterVertices map
   *
   * @param clusters
   * @param rootGraph
   * @return
   */

  private Int2IntOpenHashMap buildMaps(List<Cluster> clusters, Graph rootGraph) {
    Int2IntOpenHashMap vertexMap = new Int2IntOpenHashMap();
    for (Cluster cluster : clusters) {
      IntArrayList remainder = cluster.getRemainder();
      if (!remainder.isEmpty()) {
        WeightSortedVertexSet remainderSet = new WeightSortedVertexSet(cluster.remainderGraph(rootGraph));
        for (int v : remainder) {
          vertexMap.put(v, clusterVertices.size());
        }
        clusterVertices.add(remainderSet);
      }
    }
    return vertexMap;
  }

  public Graph getGraph() {
    return graph;
  }

  public List<WeightSortedVertexSet> getClusterVertices() {
    return clusterVertices;
  }

}
