/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;
import net.adeptropolis.nephila.graphs.WeightSortedVertexSet;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;

public class ContractedLeafView {

  private final Int2ObjectOpenHashMap<WeightSortedVertexSet> leafClusters;
  private final Graph graph;

  public ContractedLeafView(Cluster root, Graph rootGraph) {
    this.leafClusters = new Int2ObjectOpenHashMap<>();
    this.graph = collapseGraph(root, rootGraph);
  }

  private Graph collapseGraph(Cluster root, Graph rootGraph) {
    Int2IntOpenHashMap collapseMapping = new Int2IntOpenHashMap();
    root.traverseLeafs(leaf -> {
      WeightSortedVertexSet vSet = new WeightSortedVertexSet(leaf.remainderGraph(rootGraph));
      Preconditions.checkState(vSet.size() > 0);
      int rep = vSet.getVertices()[0];
      leafClusters.put(rep, vSet);
      for (int v : vSet.getVertices()) {
        collapseMapping.put(v, rep);
      }
    });
    return buildGraph(rootGraph, collapseMapping);
  }

  private Graph buildGraph(Graph rootGraph, Int2IntOpenHashMap collapseMapping) {
    CompressedSparseGraphBuilder builder = CompressedSparseGraph.builder();
    VertexIterator it = rootGraph.vertexIterator();
    rootGraph.traverseEdgesSequential((u, v, w) -> builder.add(collapse(u, collapseMapping), collapse(v, collapseMapping), w));
    IntArrayList usedVertices = new IntArrayList();
    while (it.hasNext()) {
      usedVertices.add(collapse(it.globalId(), collapseMapping));
    }
    return builder.build().inducedSubgraph(usedVertices.iterator());
  }

  private int collapse(int u, Int2IntOpenHashMap collapseMapping) {
    return collapseMapping.getOrDefault(u, u);
  }

  public boolean isCluster(int v) {
    return leafClusters.containsKey(v);
  }

  public WeightSortedVertexSet getCluster(int id) {
    return leafClusters.get(id);
  }

  public Graph getGraph() {
    return graph;
  }
}
