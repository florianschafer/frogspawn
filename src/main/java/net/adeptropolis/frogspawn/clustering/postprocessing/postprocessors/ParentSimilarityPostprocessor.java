/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;

/**
 * <p>Parent similarity postprocessor</p>
 * This postprocessor uses a given graph similarity metric to compute the similarity between the aggregate graphs of
 * a given cluster and its parent. If the relationship of both graphs does not meet the given <code>minSimilarity</code>
 * threshold, the cluster assigned to its grandparent and re-added to the queue, which traverses the cluster tree
 * from bottom to top. Similarly, clusters that exceed the <code>maxSimilarity</code> threshold, will be assimilated
 * into their parents.
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
  private final double maxSimilarity;
  private final double targetSimilarity;

  private int collectedBoundaryViolations;
  private int collectedTreeSize;

  /**
   * Constructor
   *  @param metric        Graph similarity metric
   * @param minSimilarity Minimum similarity between a cluster and its parent
   * @param maxSimilarity Maximum similarity between a cluster and its parent
   * @param targetSimilarity Target similarity for relocating clusters
   */

  public ParentSimilarityPostprocessor(GraphSimilarityMetric metric, double minSimilarity, double maxSimilarity, double targetSimilarity) {
    this.metric = metric;
    this.minSimilarity = minSimilarity;
    this.maxSimilarity = maxSimilarity;
    this.targetSimilarity = targetSimilarity;
  }

  @Override
  public boolean apply(Cluster cluster) {

    Cluster ancestor = cluster.getParent();

    if (ancestor == null) {
      return false;
    }

    double similarity = similarity(ancestor, cluster);

    collectedTreeSize++;
    if (similarity < minSimilarity || similarity > maxSimilarity) {
      collectedBoundaryViolations++;
    }

    if (similarity > maxSimilarity) {
      ancestor.assimilateChild(cluster, true);
      return true;
    }

    if (similarity < minSimilarity) {

      Cluster fallbackMin = null;

      for (ancestor = ancestor.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
        similarity = similarity(ancestor, cluster);

        if (similarity >= targetSimilarity) {
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
    return TreeTraversalMode.LOCAL_BOTTOM_TO_TOP;
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

  public double resetConvergenceStats() {
    double convergenceRatio = 1d - collectedBoundaryViolations / (double) collectedTreeSize;
    collectedTreeSize = 0;
    collectedBoundaryViolations = 0;
    return convergenceRatio;
  }

}
