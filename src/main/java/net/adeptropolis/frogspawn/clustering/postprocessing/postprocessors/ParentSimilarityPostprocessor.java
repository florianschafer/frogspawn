/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.BottomUpQueueFactory;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PriorityQueue;

/**
 * <p>Parent similarity postprocessor</p>
 * This postprocessor uses a given metric to compute the similarity between the aggregate graphs of a cluster and its
 * parent. If the relationship of both graphs does not meet the given <code>minSimilarity</code> threshold, the cluster
 * is assigned to either the first ancestor with similarity <code>targetSimilarity</code> or fall back to the first
 * ancestor that satisfies <code>minSimilarity</code>.
 * Similarly, clusters that exceed the <code>maxSimilarity</code> threshold, will be assimilated into their parents.
 * <p>
 * This prevents the "shaving" phenomenon, where (due to the fact that per recursion step only a single cluster is being split off)
 * the position of semantically unrelated clusters within the hierarchy suggests a much higher similarity and specificity than
 * it is actually the case. As a very benign side effect, the artificial restriction to binary cluster trees is also
 * lifted.
 * </p>
 * <b>NOTE:</b>After applying this postprocessor to the full cluster tree, the consistency guard must be re-applied too!
 * Otherwise, the vertex affiliation metric of clusters that have been lept over is undefined.
 *
 * @see AffiliationGuardingPostprocessor
 */

public class ParentSimilarityPostprocessor implements Postprocessor {

  private static final Logger LOG = LoggerFactory.getLogger(ParentSimilarityPostprocessor.class.getSimpleName());

  private final GraphSimilarityMetric metric;
  private final double minSimilarity;
  private final double maxSimilarity;
  private final double acceptanceLimit;
  private final Object2DoubleOpenHashMap<Cluster> scoreCache;

  /**
   * Constructor
   *
   * @param metric          Graph similarity metric
   * @param minSimilarity   Minimum similarity between a cluster and its parent
   * @param maxSimilarity   Maximum similarity between a cluster and its parent
   * @param acceptanceLimit Lower boundary for the required acceptance limit
   */

  public ParentSimilarityPostprocessor(GraphSimilarityMetric metric, double minSimilarity, double maxSimilarity, double acceptanceLimit) {
    this.metric = metric;
    this.minSimilarity = minSimilarity;
    this.maxSimilarity = maxSimilarity;
    this.acceptanceLimit = acceptanceLimit;
    this.scoreCache = new Object2DoubleOpenHashMap<>();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public PostprocessingState apply(Cluster rootCluster) {

    PriorityQueue<Cluster> queue = BottomUpQueueFactory.queue(rootCluster);

    boolean changed = false;
    Stats stats = new Stats();

    while (!queue.isEmpty()) {
      changed |= applyLocally(queue.poll(), stats);
    }

    double score = 1d - stats.boundaryViolations / (double) stats.visited;
    LOG.debug("Previous similarity score was {}", score);
    double prevScore = scoreCache.getOrDefault(rootCluster, -1);

    if (changed && Math.abs(score - prevScore) < 1E-4) {
      LOG.warn("Got stuck. Terminating and using the current state.");
      return new PostprocessingState(false, true);
    }

    scoreCache.put(rootCluster, score);

    if (score >= acceptanceLimit) {
      return new PostprocessingState(false, true);
    }

    return new PostprocessingState(changed);

  }

  /**
   * Process a specific cluster
   *
   * @param cluster Any cluster
   * @param stats   Used for collecting convergence stats
   * @return Whether there were any changes made to this cluster or one of its ancestors
   */

  public boolean applyLocally(Cluster cluster, Stats stats) {

    Cluster ancestor = cluster.getParent();

    if (ancestor == null) {
      return false;
    }

    double similarity = similarity(ancestor, cluster);

    stats.visited++;
    if (similarity < minSimilarity || similarity > maxSimilarity) {
      stats.boundaryViolations++;
    }

    if (similarity > maxSimilarity) {
      ancestor.assimilateChild(cluster, true);
      return true;
    }

    if (similarity < minSimilarity) {

      Cluster fallbackMin = null;

      for (ancestor = ancestor.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
        similarity = similarity(ancestor, cluster);

        if (similarity >= minSimilarity) {
          ancestor.annex(cluster);
          return true;
        }

        if (fallbackMin == null && similarity >= minSimilarity) {
          fallbackMin = ancestor;
        }

      }

      if (fallbackMin != null) {
        fallbackMin.annex(cluster);
      } else {
        cluster.root().annex(cluster);
      }

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
   * This kind of postprocessor does require checking for idempotency
   *
   * @return <code>false</code>
   */

  @Override
  public boolean requiresIdempotency() {
    return true;
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

  private static class Stats {

    int visited = 0;
    int boundaryViolations = 0;

  }

}
