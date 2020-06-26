/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import com.google.common.collect.Lists;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.OrderedBTTQueueFactory;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Redistribute trivial clusters. That is, identify chains of clusters with trivial branching behaviour
 * (single children) and attach them to the next-highest cluster node with regular branching.
 *
 * <p>
 * Note: This postprocessor may affect the consistency state of vertices and thus <b>requires</b>
 * that the consistency guarding postprocessor must be run afterwards.
 * </p>
 */

public class SingletonRedistributionPostprocessor implements Postprocessor {

  /**
   * Process the queue
   */

  private static PostprocessingState processQueue(PriorityQueue<Cluster> queue, Set<Cluster> enqueued) {
    PostprocessingState state = new PostprocessingState();
    while (!queue.isEmpty()) {
      Cluster cluster = queue.poll();
      state.update(processCluster(cluster, queue, enqueued));
    }
    return state;
  }

  /**
   * Locate all leaf nodes in a cluster tree and enqueue them
   */

  private static void enqueueLeafs(Cluster root, PriorityQueue<Cluster> queue, Set<Cluster> enqueued) {
    root.traverse(cluster -> {
      if (cluster.getChildren().isEmpty()) {
        enqueue(cluster, queue, enqueued);
      }
    });
  }

  /**
   * Process a cluster, i.e. move upwards until a non-trivial ancestor is found and attach all
   * clusters that have been traversed in the process as children to that ancestor.
   * Also enqueue the non-trivial ancestor.
   *
   * @param cluster Cluster from which to start
   */

  private static PostprocessingState processCluster(Cluster cluster, PriorityQueue<Cluster> queue, Set<Cluster> enqueued) {

    if (cluster.getParent() == null) {
      return PostprocessingState.UNCHANGED;
    }

    Cluster nearestNonTrivialAncestor = findClosestNonTrivialAncestor(cluster);

    if (nearestNonTrivialAncestor == null) {
      return PostprocessingState.UNCHANGED;
    }

    enqueue(nearestNonTrivialAncestor, queue, enqueued);

    if (nearestNonTrivialAncestor.equals(cluster.getParent())) {
      return PostprocessingState.UNCHANGED;
    }

    redistributeChain(cluster, nearestNonTrivialAncestor);
    return PostprocessingState.CHANGED;
  }

  /**
   * Moving upwards, attach all clusters in a chain to a designated new common ancestor
   *
   * @param cluster          Starting cluster
   * @param designatedParent New common parent to which all clusters from the chain should be attached
   */

  private static void redistributeChain(Cluster cluster, Cluster designatedParent) {
    List<Cluster> chain = Lists.newArrayList();
    for (Cluster ptr = cluster; !ptr.getParent().equals(designatedParent); ptr = ptr.getParent()) {
      chain.add(ptr);
    }
    for (Cluster ptr : chain) {
      designatedParent.annex(ptr);
    }
  }

  /**
   * Moving upwards in the cluster hierarchy, find the first ancestor with non-trivial branches,
   * i.e. at least two child nodes
   *
   * @param cluster Cluster from which to start searching
   * @return First ancestor satisfying the criterion or <code>null</code> if no such cluster could be found
   */

  private static Cluster findClosestNonTrivialAncestor(Cluster cluster) {
    for (Cluster ancestor = cluster.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
      if (ancestor.getChildren().size() >= 2) {
        return ancestor;
      }
    }
    return null;
  }

  /**
   * Add a new cluster to the queue, making sure that none is enqueued multiple times
   * when coming from different branches
   *
   * @param cluster Cluster to enqueue
   */

  private static void enqueue(Cluster cluster, PriorityQueue<Cluster> queue, Set<Cluster> enqueued) {
    if (!enqueued.contains(cluster)) {
      queue.add(cluster);
      enqueued.add(cluster);
    }
  }

  /**
   * Apply postprocessing
   *
   * @return State after applying this postprocessor
   */

  public PostprocessingState apply(Cluster root) {
    PriorityQueue<Cluster> queue = OrderedBTTQueueFactory.queue();
    Set<Cluster> enqueued = new HashSet<>();
    enqueueLeafs(root, queue, enqueued);
    return processQueue(queue, enqueued);
  }

  /**
   * @return This class implements a custom cluster tree traversal mechanism
   */

  @Override
  public TreeTraversalMode traversalMode() {
    return TreeTraversalMode.GLOBAL_CUSTOM;
  }

  /**
   * This postprocessor may compromise cluster vertex affinity scores by relocating clusters within
   * the hierarchy
   *
   * @return <code>true</code>
   */

  @Override
  public boolean compromisesVertexAffinity() {
    return true;
  }

  /**
   * This kind of postprocessor does not require checking for idempotency
   *
   * @return <code>false</code>
   */

  @Override
  public boolean requiresIdempotency() {
    return false;
  }


}
