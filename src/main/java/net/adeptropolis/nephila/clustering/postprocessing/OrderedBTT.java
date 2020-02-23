/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Traverse a cluster hierarchy from bottom to top with a guarantee that all clusters
 * are consumed in the order of their depth
 */

class OrderedBTT {

  private OrderedBTT() {
  }

  static PriorityQueue<Cluster> queue(Cluster root) {
    PriorityQueue<Cluster> queue = new PriorityQueue<>(Comparator.comparingInt(Cluster::depth).reversed());
    root.traverse(queue::add);
    return queue;
  }

}
