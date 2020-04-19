/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;

import java.util.PriorityQueue;

/**
 * Ensures that every cluster approximates a given number of child clusters.
 * <p>Note: This does interfere with the structure induced by the min-cut
 * approach, but may be desired at times.</p>
 */

public class ClusterShapePostprocessor {

  private final Cluster root;
  private final PriorityQueue<Cluster> queue;
  private final int minChildren;

  /**
   * Constructor
   *
   * @param root        Root cluster
   * @param minChildren Minimum number of child clusters
   */

  public ClusterShapePostprocessor(Cluster root, int minChildren) {
    this.root = root;
    this.queue = OrderedClusterQueueFactory.bottomUpQueue(root);
    this.minChildren = minChildren;
  }

  /**
   * Apply postprocessing
   *
   * @return The original root cluster
   */

  public Cluster postprocess() {
    while (!queue.isEmpty()) {
      Cluster next = queue.poll();
      processCluster(next);
    }
    return root;
  }

  /**
   * Check the grandparent of a cluster to exhibit the required amount of children and annex
   * the cluster if necessary.
   *
   * @param cluster
   */

  private void processCluster(Cluster cluster) {
    if (cluster.getParent() == null || cluster.getParent().getParent() == null) {
      return;
    }
    Cluster grandparent = cluster.getParent().getParent();
    if (grandparent.getChildren().size() < minChildren) {
      grandparent.annex(cluster);
    }

  }

}
