/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors.PostprocessingState;

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
   * @return State state after running this postprocessor
   */

  public static PostprocessingState apply(Postprocessor postprocessor, Cluster rootCluster) {
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
   * @return State after running this postprocessor
   */

  private static PostprocessingState processQueueBTT(Postprocessor postprocessor, Cluster rootCluster) {
    PriorityQueue<Cluster> queue = BottomUpQueueFactory.queue(rootCluster);
    PostprocessingState state = new PostprocessingState();
    while (!queue.isEmpty()) {
      Cluster cluster = queue.poll();
      state.update(postprocessor.apply(cluster));
    }
    return state;
  }


}
