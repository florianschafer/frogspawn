/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.Graph;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PriorityQueue;

/**
 * Main postprocessing class
 * <p>Applies all relevant postprocessors to the cluster tree in the correct order</p>
 */

public class Postprocessing {

  private static final Logger LOG = LoggerFactory.getLogger(Postprocessing.class.getSimpleName());

  private final Cluster rootCluster;
  private final ParentSimilarityPostprocessor ancestorSimilarity;
  private final ConsistencyGuardingPostprocessor consistency;
  private final SingletonCollapsingPostprocessor singletons;

  /**
   * Constructor
   *
   * @param rootCluster Root cluster
   * @param rootGraph   Root graph
   * @param settings    Clustering settings
   */

  public Postprocessing(Cluster rootCluster, Graph rootGraph, ClusteringSettings settings) {
    this.rootCluster = rootCluster;
    this.ancestorSimilarity = new ParentSimilarityPostprocessor(settings.getMinparentOverlap(), settings.getParentSearchStepSize(), rootGraph);
    this.consistency = new ConsistencyGuardingPostprocessor(rootGraph, settings.getMinClusterSize(), settings.getMinClusterLikelihood());
    this.singletons = new SingletonCollapsingPostprocessor();
  }

  /**
   * Apply the full postprocessor chain
   *
   * @return The root cluster
   */

  public Cluster apply() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    applyPostprocessor(singletons);
    applyPostprocessor(ancestorSimilarity);
    applyPostprocessor(singletons);
    applyPostprocessor(consistency);
    applyPostprocessor(singletons);
    stopWatch.stop();
    LOG.info("Postprocessing finished after {}", stopWatch);
    return rootCluster;
  }

  /**
   * Apply a specific postprocessor to the full cluster hierarchy, bottom-up
   *
   * @param postprocessor Postprocessor
   * @return true if the cluster hierarchy has been changed, else false
   */

  private boolean applyPostprocessor(Postprocessor postprocessor) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    PriorityQueue<Cluster> queue = OrderedBTTQueueFactory.queue(rootCluster);
    boolean changed = processQueue(postprocessor, queue);
    stopWatch.stop();
    LOG.debug("{} finished in {}. There were {} to the cluster hierarchy.",
            postprocessor.getClass().getSimpleName(), stopWatch, changed ? "changes" : "no changes");
    return changed;
  }

  /**
   * Process the cluster queue using a given postprocessor
   *
   * @param postprocessor Postprocessor
   * @param queue         Bottom-up ordered priority queue of clusters
   * @return true if the cluster hierarchy has been changed, else false
   */

  private boolean processQueue(Postprocessor postprocessor, PriorityQueue<Cluster> queue) {
    boolean changed = false;
    while (!queue.isEmpty()) {
      Cluster cluster = queue.poll();
      if (rootCluster.aggregateClusters().contains(cluster)) {
        changed |= postprocessor.apply(cluster);
      }
    }
    return changed;
  }


}
