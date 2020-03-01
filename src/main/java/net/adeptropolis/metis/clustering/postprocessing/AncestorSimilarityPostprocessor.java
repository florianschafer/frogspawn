/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.Graph;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// NOTE: Requires consistency guarding pp to run afterwards
class AncestorSimilarityPostprocessor implements Postprocessor {

  private static final Logger LOG = LoggerFactory.getLogger(AncestorSimilarityPostprocessor.class.getSimpleName());

  private final double minAncestorOverlap;
  private final Graph rootGraph;

  public AncestorSimilarityPostprocessor(double minAncestorOverlap, Graph rootGraph) {
    this.minAncestorOverlap = minAncestorOverlap;
    this.rootGraph = rootGraph;
  }

  @Override
  public boolean apply(Cluster cluster) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    if (cluster.getParent() == null || cluster.getParent().getParent() == null) {
      return false;
    }
    Cluster ancestor = nearestAncestorSatisfyingOverlap(cluster);
    if (ancestor == null || ancestor == cluster.getParent()) {
      return false;
    }
    ancestor.annex(cluster);
    stopWatch.stop();
    LOG.trace("Finished after {}", stopWatch);
    return true;
  }

  private Cluster nearestAncestorSatisfyingOverlap(Cluster cluster) {
    Cluster ancestor = cluster.getParent();
    while (ancestor != null && overlap(cluster, ancestor) < minAncestorOverlap) {
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