/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.similarity.GraphSimilarityMetric;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.helpers.SequencePredicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Parent similarity postprocessor</p>
 * This postprocessor computes two cumulative vertex weights:
 * <ul>
 *   <li><code>w</code>: The total weight of the graph created from the cluster's vertices and that of its descendants</li>
 *   <li><code>p</code>: Similarly, that of the cluster's parent, but only counting vertices that are also contained in the cluster itself</li>
 * </ul>
 * <p>Subsequently, all clusters are moved upwards in the cluster tree until the quotient <code>w/p</code> is above a given threshold.
 * This prevents the "shaving" phenomenon, where (due to the fact that per recursion step only a single cluster is being split off)
 * the position of semantically unrelated clusters within the hierarchy suggests a much higher similarity and specificity than
 * it is actually the case. As a very benign side effect, the artificial restriction to binary cluster trees is also
 * lifted.
 * </p>
 * <p><b>NOTE:</b>After applying this postprocessor to the full cluster tree, the consistency guard <b>must</b> be re-applied too!
 * Otherwise, the consistency of clusters that have been lept over during the "shift up" phase is undefined and may even
 * cause null pointer exceptions.
 * </p>
 *
 * @see ConsistencyGuardingPostprocessor
 */

class ParentSimilarityPostprocessor implements Postprocessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ParentSimilarityPostprocessor.class.getSimpleName());

  private final GraphSimilarityMetric metric;
  private final double minSimilarity;
  private final int searchStepSize;

  /**
   * Constructor
   *
   * @param metric         Graph similarity metric
   * @param minSimilarity  Minimum similarity between a cluster and its parent
   * @param searchStepSize Parent search step size
   */

  public ParentSimilarityPostprocessor(GraphSimilarityMetric metric, double minSimilarity, int searchStepSize) {
    this.metric = metric;
    this.minSimilarity = minSimilarity;
    this.searchStepSize = searchStepSize;
  }

  /**
   * Move the cluster up in the tree until its similarity wrt to its parent is at least <code>minSimilarity</code>
   *
   * @param cluster A cluster. Not necessarily root.
   * @return true if the underlying cluster has been modified, else false
   */

  @Override
  public boolean apply(Cluster cluster) {
    if (cluster.getParent() == null || cluster.getParent().getParent() == null) {
      return false;
    }
    Cluster ancestor = nearestAncestorSatisfyingSimilarity(cluster);
    if (ancestor == null) {
      ancestor = cluster.root();
    }
    if (ancestor.equals(cluster.getParent())) {
      return false;
    }
    ancestor.annex(cluster);
    return true;
  }

  /**
   * Moving upwards, find the first ancestor whose similarity with the given cluster is at least <code>minSimilarity</code>
   *
   * @param cluster The Cluster
   * @return The ancestor matching the condition
   */

  private Cluster nearestAncestorSatisfyingSimilarity(Cluster cluster) {
    Cluster parent = cluster.getParent();
    AtomicInteger predicateChecks = new AtomicInteger();
    Cluster first = SequencePredicates.findFirst(parent, searchStepSize, Cluster.class, Cluster::getParent, ancestor -> {
      predicateChecks.getAndIncrement();
      return similarity(cluster, ancestor) >= minSimilarity;
    });
    LOGGER.trace("Finished after taking {} samples", predicateChecks.get());
    return first;
  }

  /**
   * Compute the similarity score between a cluster and one of its ancestors
   *
   * @param cluster  The cluster
   * @param ancestor The cluster's ancestor
   * @return Similarity between the cluster and its ancestor
   */

  private double similarity(Cluster cluster, Cluster ancestor) {
    Graph clusterGraph = cluster.aggregateGraph();
    Graph ancestorGraph = ancestor.aggregateGraph();
    return metric.compute(ancestorGraph, clusterGraph);
  }

}
