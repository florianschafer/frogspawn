/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.structuring;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.adeptropolis.nephila.Clustering;
import net.adeptropolis.nephila.ClusteringSettings;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ConsistencyMetric;
import net.adeptropolis.nephila.graphs.Graph;

import java.util.List;

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

  public List<MetaCluster> run() {
    MetaGraph metaGraph = MetaGraphBuilder.build(sourceRootCluster, sourceGraph);
    Cluster metaRoot = Clustering.run(metaGraph.getGraph(), metric, settings);



    //    List<MetaCluster> metaClusters = Lists.newArrayList();
//    metaRoot.traverseLeafs(leaf -> {
//      metaClusters.add(processLeaf(metaGraph, leaf));
//    });
//    return metaClusters;
  }

  private MetaCluster processLeaf(Graph graph, MetaGraph metaGraph, Cluster metaLeaf) {

    Graph subgraph = metaGraph.getGraph().inducedSubgraph(metaLeaf.getRemainder().iterator());
    Int2ObjectOpenHashMap<WeightSortedVertexSet> children = new Int2ObjectOpenHashMap<>();
    WeightSortedVertexSet vertices = new WeightSortedVertexSet(subgraph, v -> {
      List<IntArrayList> sourceLeafClusters = metaGraph.getSourceLeafClusters();
      if (v < sourceLeafClusters.size()) {
        return sourceLeafClusters.get(v).getInt(0);
      } else {
        return metaGraph.getInverseMap().get(v);
      }
    });

    IntArrayList regularVertices = new IntArrayList();
    DoubleArrayList regularWeights = new DoubleArrayList();

    List<IntArrayList> clusterVertices = Lists.newArrayList();
    List<DoubleArrayList> clusterWeights = Lists.newArrayList();

    for (IntListIterator leafIt = metaLeaf.getRemainder().iterator(); leafIt.hasNext();) {
      int metaGlobalId = leafIt.nextInt();
      if (metaGlobalId < metaGraph.getSourceLeafClusters().size()) {
        IntArrayList sourceLeafCluster = metaGraph.getSourceLeafClusters().get(metaGlobalId);
        clusterVertices.add(sourceLeafCluster);
        Graph sourceLeafGraph = graph.inducedSubgraph(sourceLeafCluster.iterator());

        sourceLeafGraph.weights()

      clusterWeights.add();
      } else {
        regularVertices.add(metaGraph.getInverseMap().get(metaGlobalId));
        double weight = subgraph.weights()[subgraph.localVertexId(metaGlobalId)];
        regularWeights.add(weight);
      }
    }
    sort(regularVertices, regularWeights);
    for (int i = 0; i < clusterVertices.size(); i++) {
      sort(clusterVertices.get(i), clusterWeights.get(i));
    }
    return new MetaCluster(regularVertices, regularWeights, clusterVertices, clusterWeights);
  }

  private void sort(IntArrayList vertices, DoubleArrayList weights) {
    Arrays.mergeSort(0, vertices.size(),
            (a,b) -> Double.compare(weights.getDouble(b), weights.getDouble(a)),
            (a,b) -> swap(vertices, weights, a, b));
  }

  private static void swap(IntArrayList vertices, DoubleArrayList weights, int a, int b) {
    int tmp = vertices.getInt(a);
    vertices.set(a, vertices.getInt(b));
    vertices.set(b, tmp);
    double tmpd = weights.getDouble(a);
    weights.set(a, weights.getDouble(b));
    weights.set(b, tmpd);
  }

}
