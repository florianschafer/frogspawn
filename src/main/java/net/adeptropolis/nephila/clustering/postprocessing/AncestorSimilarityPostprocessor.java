/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;

class AncestorSimilarityPostprocessor implements Postprocessor {

  private final double minAncestorOverlap;
  private final Graph rootGraph;

  public AncestorSimilarityPostprocessor(double minAncestorOverlap, Graph rootGraph) {
    this.minAncestorOverlap = minAncestorOverlap;
    this.rootGraph = rootGraph;
  }

  @Override
  public boolean apply(Cluster cluster) {
    if (cluster.getParent() == null || cluster.getParent().getParent() == null) {
      return false;
    }
    Cluster ancestor = nearestAncestorSatisfyingOverlap(cluster);
    if (ancestor == cluster.getParent()) {
      return false;
    }
    cluster.getParent().getChildren().remove(cluster);
    ancestor.getChildren().add(cluster);
    cluster.setParent(ancestor);
    return true;
  }

  private Cluster nearestAncestorSatisfyingOverlap(Cluster cluster) {
    Cluster ancestor = cluster.getParent();
    while (ancestor != null && overlap(cluster, ancestor) >= minAncestorOverlap) {
      if (ancestor.getParent() == null) {
        break;
      }
      ancestor = ancestor.getParent();
    }
    return ancestor;
  }

  private double overlap(Cluster cluster, Cluster ancestor) {
    Graph clusterGraph = cluster.aggregateGraph(rootGraph);
    Graph ancestorGraph = ancestor.aggregateGraph(rootGraph);
    return clusterGraph.overlap(ancestorGraph);
  }

}
