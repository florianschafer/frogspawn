/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.meta;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.WeightSortedVertexSet;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;

public class ContractedLeafView {

  private final Int2ObjectOpenHashMap<WeightSortedVertexSet> leafClusters;
  private final Graph graph;
  private final IntOpenHashSet depletedRepresentatives;

  public ContractedLeafView(Cluster root, Graph rootGraph, IntOpenHashSet depletedRepresentatives) {
    this.depletedRepresentatives = depletedRepresentatives;
    this.leafClusters = new Int2ObjectOpenHashMap<>();
    this.graph = collapseGraph(root, rootGraph);
  }

  private Graph collapseGraph(Cluster root, Graph rootGraph) {
    Int2IntOpenHashMap collapseMapping = new Int2IntOpenHashMap();
    root.traverseLeafs(leaf -> {
      WeightSortedVertexSet vSet = new WeightSortedVertexSet(leaf.remainderGraph(rootGraph));
      int rep = selectUniqueRepresentative(vSet);
      leafClusters.put(rep, vSet);
      for (int v : vSet.getVertices()) {
        collapseMapping.put(v, rep);
      }
    });
    return buildGraph(rootGraph, collapseMapping);
  }

  private int selectUniqueRepresentative(WeightSortedVertexSet vSet) {
    Preconditions.checkState(vSet.size() > 0);
    for (int v : vSet.getVertices()) {
      if (!depletedRepresentatives.contains(v)) {
        depletedRepresentatives.add(v);
        return v;
      }
    }
    // TODO: Although extremely unlikely, this *might* actually happen and should be handled more gracefully!
    throw new RuntimeException("Failed to elect a unique cluster representative");
  }

  private Graph buildGraph(Graph rootGraph, Int2IntOpenHashMap collapseMapping) {
    CompressedSparseGraphBuilder builder = CompressedSparseGraph.builder();
    IntOpenHashSet usedVertices = new IntOpenHashSet();
    rootGraph.traverseEdgesSequential((u, v, w) -> {
      int uP = collapse(u, collapseMapping);
      int vP = collapse(v, collapseMapping);
      builder.add(uP, vP, w);
      usedVertices.add(uP);
      usedVertices.add(vP);
    });
    return builder.build().inducedSubgraph(usedVertices.iterator());
  }

  private int collapse(int u, Int2IntOpenHashMap collapseMapping) {
    return collapseMapping.getOrDefault(u, u);
  }

  public WeightSortedVertexSet getCluster(int id) {
    return leafClusters.get(id);
  }

  public Int2ObjectOpenHashMap<WeightSortedVertexSet> getLeafClusters() {
    return leafClusters;
  }

  public Graph getGraph() {
    return graph;
  }
}
