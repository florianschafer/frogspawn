/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ConsistencyGuard;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;

/**
 * <p>Ensures the consistency of a cluster during postprocessing</p>
 * <p>
 * This is very much akin to the in-flight consistency guard, but guarantees that only vertices from the
 * cluster's remainder are shifted upwards.
 * </p>
 * <b>Note:</b> This postprocessor is unsafe inasfar as it does not guarantee the consistency of
 * child clusters once a particular cluster has been modified. Always wrap into a CascadingSafetyPostprocessor.
 *
 * @see ConsistencyGuard
 * @see CascadingPostprocessorWrapper
 */


class UnsafeConsistencyGuardingPostprocessor implements Postprocessor {

  private final Graph graph;
  private final int minClusterSize;
  private final double minClusterLikelihood;

  public UnsafeConsistencyGuardingPostprocessor(Graph graph, int minClusterSize, double minClusterLikelihood) {
    this.graph = graph;
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
  }

  @Override
  public boolean apply(Cluster cluster) {
    Cluster parent = cluster.getParent();
    if (parent == null) {
      return false;
    }
    IntRBTreeSet clusterVertices = new IntRBTreeSet(cluster.getRemainder());
    Graph clusterGraph = cluster.aggregateGraph(graph);
    IntRBTreeSet survivors = initSurvivors(clusterGraph);
    for (Graph subgraph = clusterGraph; true; subgraph = inducedSubgraph(survivors)) {
      int prevSize = clusterVertices.size();
      shiftInconsistentVertices(clusterVertices, parent, survivors, subgraph);
      if (clusterVertices.size() < minClusterSize) {
        parent.addToRemainder(clusterVertices.iterator());
        assignChildrenToParent(cluster, parent);
        return true;
      } else if (clusterVertices.size() == prevSize) {
        break;
      }
    }
    if (clusterVertices.size() == cluster.getRemainder().size()) {
      return false;
    } else {
      cluster.setRemainder(new IntArrayList(clusterVertices));
      return true;
    }
  }

  private void shiftInconsistentVertices(IntRBTreeSet clusterVertices, Cluster parent, IntRBTreeSet survivors, Graph subgraph) {
    double[] likelihoods = subgraph.relativeWeights(graph);
    VertexIterator it = subgraph.vertexIterator();
    while (it.hasNext()) {
      if (likelihoods[it.localId()] < minClusterLikelihood) {
        if (clusterVertices.contains(it.globalId())) {
          parent.addToRemainder(it.globalId());
          clusterVertices.remove(it.globalId());
        }
        survivors.remove(it.globalId());
      }
    }
  }

  private Graph inducedSubgraph(IntRBTreeSet survivors) {
    return graph.inducedSubgraph(survivors.iterator());
  }

  private IntRBTreeSet initSurvivors(Graph candidate) {
    IntRBTreeSet remainingVertices = new IntRBTreeSet();
    VertexIterator vertexIt = candidate.vertexIterator();
    while (vertexIt.hasNext()) {
      remainingVertices.add(vertexIt.globalId());
    }
    return remainingVertices;
  }

  private void assignChildrenToParent(Cluster cluster, Cluster parent) {
    parent.getChildren().remove(cluster);
    parent.addChildren(cluster.getChildren());
    for (Cluster child : cluster.getChildren()) {
      child.setParent(parent);
    }
  }

}
