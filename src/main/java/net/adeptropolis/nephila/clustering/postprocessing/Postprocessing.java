/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.ClusteringSettings;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PriorityQueue;

/**
 * Main postprocessing class
 * <p>Applies all relevant postprocessors to the cluster tree</p>
 * <b>Note: All postprocessing might need some love performance-wise</b>
 */

public class Postprocessing {

  private static final Logger LOG = LoggerFactory.getLogger(Postprocessing.class.getSimpleName());

  private final Cluster rootCluster;
  private final AncestorSimilarityPostprocessor ancestorSimilarity;
  private final ConsistencyGuardingPostprocessor consistency;
  private final SingletonCollapsingPostprocessor singletons;

  public Postprocessing(Cluster rootCluster, Graph rootGraph, ClusteringSettings settings) {
    this.rootCluster = rootCluster;
    this.ancestorSimilarity = new AncestorSimilarityPostprocessor(settings.getMinAncestorOverlap(), rootGraph);
    this.consistency = new ConsistencyGuardingPostprocessor(rootGraph, settings.getMinClusterSize(), settings.getMinClusterLikelihood());
    this.singletons = new SingletonCollapsingPostprocessor(settings.getCollapseSingletons());
  }

  public Cluster apply() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    boolean changed;
    do {
      changed = applyPostprocessor(ancestorSimilarity) || applyPostprocessor(consistency);
    } while (changed);
    applyPostprocessor(singletons);
    stopWatch.stop();
    LOG.debug("Postprocessing finished after {}", stopWatch);
    return rootCluster;
  }

  private boolean applyPostprocessor(Postprocessor postprocessor) {
    PriorityQueue<Cluster> queue = OrderedBTT.queue(rootCluster);
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
