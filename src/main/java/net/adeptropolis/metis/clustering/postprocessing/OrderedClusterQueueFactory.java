/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Factory for depth-ordered cluster priority queues
 */

class OrderedClusterQueueFactory {

  /**
   * Constructor
   */

  private OrderedClusterQueueFactory() {
  }

  /**
   * @return A new cluster priority queue, ordered by depth (bottom to top)
   */

  static PriorityQueue<Cluster> bottomUpQueue() {
    return new PriorityQueue<>(Comparator.comparingInt(Cluster::depth)
            .reversed()
            .thenComparingInt(cluster -> -cluster.getRemainder().size()));
  }

  /**
   * @param root Root cluster
   * @return A new priority queue containing the hierarchy's clusters, ordered by depth (bottom to top)
   */

  static PriorityQueue<Cluster> bottomUpQueue(Cluster root) {
    PriorityQueue<Cluster> queue = bottomUpQueue();
    root.traverse(queue::add);
    return queue;
  }

}
