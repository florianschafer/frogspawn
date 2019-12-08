/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;

import java.util.PriorityQueue;
import java.util.Set;

/**
 * A wrapper for any postprocessor that guarantees that any changes to the original cluster
 * trigger re-postprocessing on the set of child clusters.
 */

class CascadingPostprocessorWrapper implements Postprocessor {

  private final Postprocessor postprocessor;

  public CascadingPostprocessorWrapper(Postprocessor postprocessor) {
    this.postprocessor = postprocessor;
  }

  @Override
  public boolean apply(Cluster cluster) {
    if (!postprocessor.apply(cluster)) {
      return false;
    }
    PriorityQueue<Cluster> queue = OrderedBTT.queue(cluster);
    while (!queue.isEmpty()) {
      Cluster ptr = queue.poll();
      Set<Cluster> siblings = getSiblings(ptr); // Used to verify that the cluster has not been completely removed in the process
      if (apply(ptr) && (siblings == null || siblings.contains(ptr))) {
        enqueueChildren(queue, ptr);
      }
    }
    return true;
  }

  private Set<Cluster> getSiblings(Cluster ptr) {
    if (ptr.getParent() != null) {
      return ptr.getParent().getChildren();
    } else {
      return null;
    }
  }

  private void enqueueChildren(PriorityQueue<Cluster> queue, Cluster cluster) {
    cluster.getChildren().forEach(child -> {
      if (!queue.contains(child)) {
        queue.add(child);
      }
    });
  }
}
