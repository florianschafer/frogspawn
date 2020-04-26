/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import com.google.common.collect.Lists;
import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.postprocessing.postprocessors.ParentSimilarityPostprocessor;
import net.adeptropolis.metis.clustering.postprocessing.postprocessors.RemainderSizePostprocessor;
import net.adeptropolis.metis.clustering.postprocessing.postprocessors.SingletonCollapsingPostprocessor;
import net.adeptropolis.metis.clustering.postprocessing.postprocessors.VertexAffiliationGuardingPostprocessor;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Main postprocessing class
 * <p>Applies all relevant postprocessors to the cluster tree in the correct order</p>
 */

public class Postprocessing {

  private static final Logger LOG = LoggerFactory.getLogger(Postprocessing.class.getSimpleName());

  private final Cluster rootCluster;
  private final List<Postprocessor> pipeline;

  /**
   * Constructor
   *
   * @param rootCluster Root cluster
   * @param settings    Clustering settings
   */

  public Postprocessing(Cluster rootCluster, ClusteringSettings settings) {
    this.rootCluster = rootCluster;
    this.pipeline = createPipeline(settings);
  }

  /**
   * Create the default postprocessing pipeline
   *
   * @param settings Settings to be used
   * @return New pipeline
   */

  private static List<Postprocessor> createPipeline(ClusteringSettings settings) {
    RemainderSizePostprocessor remainderSize = new RemainderSizePostprocessor(settings.getMinClusterSize());
    ParentSimilarityPostprocessor ancestorSimilarity = new ParentSimilarityPostprocessor(
            settings.getSimilarityMetric(), settings.getMinAncestorSimilarity(), settings.getParentSearchStepSize());
    VertexAffiliationGuardingPostprocessor affiliation = new VertexAffiliationGuardingPostprocessor(
            settings.getVertexAffiliationMetric(), settings.getMinClusterSize(), settings.getMinVertexAffiliation());
    SingletonCollapsingPostprocessor singletons = new SingletonCollapsingPostprocessor();
    List<Postprocessor> pipeline = Lists.newArrayList(
            remainderSize,
            singletons,
            ancestorSimilarity,
            singletons,
            affiliation,
            singletons
    );
    pipeline.addAll(settings.getCustomPostprocessors());
    return pipeline;
  }

  /**
   * Apply the full postprocessor chain
   *
   * @return The root cluster
   */

  public Cluster apply() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    pipeline.forEach(this::applyPostprocessor);
    stopWatch.stop();
    LOG.info("Postprocessing finished after {}", stopWatch);
    return rootCluster;
  }

  /**
   * Apply a specific postprocessor to the full cluster hierarchy, bottom-up
   *
   * @param postprocessor Postprocessor
   */

  private void applyPostprocessor(Postprocessor postprocessor) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    boolean changed;
    switch (postprocessor.traversalMode()) {
      case LOCAL_BOTTOM_TO_TOP:
        PriorityQueue<Cluster> queue = OrderedBTTQueueFactory.queue(rootCluster);
        changed = processQueue(postprocessor, queue);
        break;
      case GLOBAL_CUSTOM:
        changed = postprocessor.apply(rootCluster);
        break;
      default:
        throw new PostprocessingException(String.format("Unsupported tree traversal type: %s", postprocessor.traversalMode()));
    }
    stopWatch.stop();
    LOG.debug("{} finished in {}. There were {} to the cluster hierarchy.",
            postprocessor.getClass().getSimpleName(), stopWatch, changed ? "changes" : "no changes");
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
