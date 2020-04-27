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

public class OrderedBTTQueueFactory {

  private static final Comparator<Cluster> BTT_COMPARATOR = Comparator.comparingInt(Cluster::depth)
          .thenComparingInt(Cluster::remainderSize)
          .thenComparingInt(Cluster::getId)
          .reversed();

  /**
   * Constructor
   */

  private OrderedBTTQueueFactory() {
  }

  /**
   * @return A new priority queue containing the hierarchy's clusters, ordered by depth (bottom to top)
   */

  public static PriorityQueue<Cluster> queue() {
    return new PriorityQueue<>(BTT_COMPARATOR);
  }

  /**
   * @param root Root cluster
   * @return A new priority queue containing the hierarchy's clusters, ordered by depth (bottom to top)
   */

  static PriorityQueue<Cluster> queue(Cluster root) {
    PriorityQueue<Cluster> queue = queue();
    root.traverse(queue::add);
    return queue;
  }

}
