/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.OrderedBTTQueueFactory;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;

import java.util.PriorityQueue;

/**
 * <p>Parent similarity postprocessor</p>
 * This postprocessor uses a given graph similarity metric to compute the similarity between the aggregate graphs of
 * a given cluster and its parent. If the relationship of both graphs does not meet the given <code>minSimilarity</code>
 * threshold, the cluster assigned to its grandparent and re-added to the queue, which traverses the cluster tree
 * from bottom to top.
 * <p>
 * This prevents the "shaving" phenomenon, where (due to the fact that per recursion step only a single cluster is being split off)
 * the position of semantically unrelated clusters within the hierarchy suggests a much higher similarity and specificity than
 * it is actually the case. As a very benign side effect, the artificial restriction to binary cluster trees is also
 * lifted.
 * </p>
 * <b>NOTE:</b>After applying this postprocessor to the full cluster tree, the consistency guard <b>must</b> be re-applied too!
 * Otherwise, the vertex affiliation metric of clusters that have been lept is undefined.
 *
 * @see VertexAffiliationGuardingPostprocessor
 */

public class ParentSimilarityPostprocessor implements Postprocessor {

  private final GraphSimilarityMetric metric;
  private final double minSimilarity;

  /**
   * Constructor
   *
   * @param metric        Graph similarity metric
   * @param minSimilarity Minimum similarity between a cluster and its parent
   */

  public ParentSimilarityPostprocessor(GraphSimilarityMetric metric, double minSimilarity) {
    this.metric = metric;
    this.minSimilarity = minSimilarity;
  }

  @Override
  public boolean apply(Cluster root) {
    PriorityQueue<Cluster> queue = OrderedBTTQueueFactory.queue();
    root.traverse(queue::add);
    boolean changed = false;
    while (!queue.isEmpty()) {
      Cluster cluster = queue.poll();
      changed |= processCluster(cluster, queue);
    }
    return changed;
  }

  private boolean processCluster(Cluster cluster, PriorityQueue<Cluster> queue) {
    if (cluster.getParent() == null || cluster.getParent().getParent() == null) {
      return false;
    }
    if (similarity(cluster.getParent(), cluster) < minSimilarity) {
      cluster.getParent().getParent().annex(cluster);
      queue.add(cluster);
      return true;
    }
    return false;
  }

  /**
   * @return This class implements a custom cluster tree traversal mechanism
   */

  @Override
  public TreeTraversalMode traversalMode() {
    return TreeTraversalMode.GLOBAL_CUSTOM;
  }

  /**
   * Compute the similarity score between a cluster and one of its ancestors
   *
   * @param cluster  The cluster
   * @param ancestor The cluster's ancestor
   * @return Similarity between the cluster and its ancestor
   */

  private double similarity(Cluster ancestor, Cluster cluster) {
    Graph clusterGraph = cluster.aggregateGraph();
    Graph ancestorGraph = ancestor.aggregateGraph();
    return metric.compute(ancestorGraph, clusterGraph);
  }

}
