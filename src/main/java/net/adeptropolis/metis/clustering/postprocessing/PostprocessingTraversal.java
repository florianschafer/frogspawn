/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;

import java.util.PriorityQueue;

/**
 * Helper class for applying postprocessors, depending on a specific postprocessor's required mode of traversal and application
 */

public class PostprocessingTraversal {

  private PostprocessingTraversal() {
  }

  /**
   * Apply a postprocessor to the cluster tree
   *
   * @param postprocessor Postprocessor to use
   * @param rootCluster   Root cluster
   * @return <code>true</code> if the cluster hierarchy has been changed. <code>false</code> otherwise.
   */

  public static boolean apply(Postprocessor postprocessor, Cluster rootCluster) {
    switch (postprocessor.traversalMode()) {
      case LOCAL_BOTTOM_TO_TOP:
        return processQueueBTT(postprocessor, rootCluster);
      case GLOBAL_CUSTOM:
        return postprocessor.apply(rootCluster);
      default:
        throw new PostprocessingException(String.format("Unsupported tree traversal type: %s", postprocessor.traversalMode()));
    }
  }

  /**
   * Process the cluster queue using a given local postprocessor in bottom-to-top mode
   *
   * @param postprocessor Postprocessor
   * @return true if the cluster hierarchy has been changed, else false
   */

  private static boolean processQueueBTT(Postprocessor postprocessor, Cluster rootCluster) {
    PriorityQueue<Cluster> queue = OrderedBTTQueueFactory.queue(rootCluster);
    boolean changed = false;
    while (!queue.isEmpty()) {
      Cluster cluster = queue.poll();
      if (rootCluster.aggregateClusters().contains(cluster)) {
        changed |= postprocessor.apply(cluster);
      }
    }
    return changed;
  }


}
