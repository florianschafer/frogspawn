/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.Graph;

/**
 * <p>Parent similarity postprocessor</p>
 * This postprocessor computes two cumulative vertex weights:
 * <ul>
 *   <li><code>w</code>: The total weight of the graph created from the cluster's vertices and that of its descendants</li>
 *   <li><code>p</code>: Similarly, that of the cluster's parent, but only counting vertices that are also contained in the cluster itself</li>
 * </ul>
 * <p>Subsequently, all clusters are moved upwards in the cluster tree until the quotient <code>w/p</code> is above a given threshold.
 * This prevents the "shaving" phenomenon, where (due to the fact that per recursion step only a single cluster is being split off)
 * the position of semantically unrelated clusters within the hierarchy suggests a much higher similarity and specificity than
 * it is actually the case. As a very benign side effect, the artificial restriction to binary cluster trees is also
 * lifted.
 * </p>
 * <p><b>NOTE:</b>After applying this postprocessor to the full cluster tree, the consistency guard <b>must</b> be re-applied too!
 * Otherwise, the consistency of clusters that have been lept over during the "shift up" phase is undefined and may even
 * cause null pointer exceptions.
 * </p>
 *
 * @see ConsistencyGuardingPostprocessor
 */

class ParentSimilarityPostprocessor implements Postprocessor {

  private final double minParentOverlap;
  private final Graph rootGraph;

  /**
   * Constructor
   *
   * @param minParentOverlap Minimum parent overlap (<code>w/p</code> from above)
   * @param rootGraph        The root graph
   */

  public ParentSimilarityPostprocessor(double minParentOverlap, Graph rootGraph) {
    this.minParentOverlap = minParentOverlap;
    this.rootGraph = rootGraph;
  }

  /**
   * Move the cluster up in the tree until its parent overlap is at least <code>minParentOverlap</code>
   *
   * @param cluster A cluster. Not necessarily root.
   * @return true if the underlying cluster has been modified, else false
   */

  @Override
  public boolean apply(Cluster cluster) {
    if (cluster.getParent() == null || cluster.getParent().getParent() == null) {
      return false;
    }
    Cluster ancestor = nearestAncestorSatisfyingOverlap(cluster);
    if (ancestor == null || ancestor == cluster.getParent()) {
      return false;
    }
    ancestor.annex(cluster);
    return true;
  }

  /**
   * Moving upwards, find the first ancestor whose overlap with the given cluster is at least <code>minParentOverlap</code>
   *
   * @param cluster The Cluster
   * @return The ancestor matching the condition
   */

  private Cluster nearestAncestorSatisfyingOverlap(Cluster cluster) {
    Cluster ancestor = cluster.getParent();
    while (ancestor != null && overlap(cluster, ancestor) < minParentOverlap) {
      if (ancestor.getParent() == null) {
        break;
      }
      ancestor = ancestor.getParent();
    }
    return ancestor;
  }

  /**
   * Compute the overlap score between a cluster and one of its ancestors
   *
   * @param cluster  The cluster
   * @param ancestor The cluster's ancestor
   * @return Overlap between the cluster and its ancestor
   */

  private double overlap(Cluster cluster, Cluster ancestor) {
    Graph clusterGraph = cluster.aggregateGraph(rootGraph);
    Graph ancestorGraph = ancestor.aggregateGraph(rootGraph);
    return clusterGraph.overlap(ancestorGraph);
  }

}
