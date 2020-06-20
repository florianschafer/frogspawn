/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors.*;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Main postprocessing class
 * <p>
 * Applies all relevant postprocessors to the cluster tree in the correct order.
 * This class also ensures that vertex affiliation remains consistent and
 * postprocessors with the idempotency flag are run repeatedly until there is no
 * further change.
 * </p>
 */

public class Postprocessing {

  private static final Logger LOG = LoggerFactory.getLogger(Postprocessing.class.getSimpleName());

  private final Cluster rootCluster;
  private final Deque<Postprocessor> queue;
  private final VertexAffiliationGuardingPostprocessor affiliationGuardingPostprocessor;

  /**
   * Constructor
   *
   * @param rootCluster Root cluster
   * @param settings    Clustering settings
   */

  private Postprocessing(Cluster rootCluster, PostprocessingSettings settings) {
    this.rootCluster = rootCluster;
    this.queue = createQueue(settings);
    this.affiliationGuardingPostprocessor = new VertexAffiliationGuardingPostprocessor(
            settings.getVertexAffiliationMetric(), settings.getMinClusterSize(),
            settings.getMinVertexAffiliation());
  }

  /**
   * Apply the full postprocessor pipeline
   *
   * @param rootCluster Root cluster
   * @param settings    Postprocessing settings
   * @return The root cluster
   */

  public static Cluster apply(Cluster rootCluster, PostprocessingSettings settings) {
    LOG.info("Starting postprocessing using settings {}", settings);
    return new Postprocessing(rootCluster, settings).postprocess();
  }

  /**
   * Create the default postprocessing pipeline
   *
   * @param settings Settings to be used
   * @return New pipeline
   */

  private static Deque<Postprocessor> createQueue(PostprocessingSettings settings) {

    Deque<Postprocessor> queue = new LinkedList<>();

    queue.addLast(new RemainderSizePostprocessor(settings.getMinClusterSize()));

    if (settings.getMinParentSimilarity() > 0 || settings.getMaxParentSimilarity() < 1) {
      queue.addLast(new ParentSimilarityPostprocessor(settings.getSimilarityMetric(), settings.getMinParentSimilarity(), settings.getMaxParentSimilarity(), settings.getTargetParentSimilarity()));
    }

    if (settings.getMinChildren() > 0) {
      queue.addLast(new DescendantCollapsingPostprocessor(settings.getMinChildren()));
    }

    switch (settings.getSingletonMode()) {
      case ASSIMILATE:
        queue.addLast(new SingletonCollapsingPostprocessor());
        break;
      case REDISTRIBUTE:
        queue.addLast(new SingletonRedistributionPostprocessor());
        break;
      default:
    }

    for (Postprocessor customPostprocessor : settings.getCustomPostprocessors()) {
      queue.addLast(customPostprocessor);
    }

    return queue;
  }

  /**
   * Apply the full postprocessing pipeline
   *
   * @return The root cluster
   */

  private Cluster postprocess() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    while (!queue.isEmpty()) {
      applyPostprocessor(queue.pollFirst());
    }
    stopWatch.stop();
    LOG.info("Postprocessing finished after {}", stopWatch);
    return rootCluster;
  }

  /**
   * Apply a specific postprocessor
   * <p>
   * If the postprocessor did make any changes and does have the <code>compromisesVertexAffinity</code> flag set
   * to true, an affinity guard will be added to the queue to run right after this.
   * </p>
   *
   * @param postprocessor Postprocessor
   * @return <code>true</code> if the postprocessor made changes to the cluster hierarchy. Otherwise <code>false</code>.
   */

  private boolean applyPostprocessor(Postprocessor postprocessor) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    boolean didMakeChanges = PostprocessingTraversal.apply(postprocessor, rootCluster);
    stopWatch.stop();
    if (postprocessor instanceof ParentSimilarityPostprocessor) {
      ParentSimilarityPostprocessor pp = (ParentSimilarityPostprocessor) postprocessor;
      LOG.info("Convergence: {}", pp.resetConvergenceStats());
    }
    LOG.debug("{} finished after {} {} changes", postprocessor.getClass().getSimpleName(), stopWatch, didMakeChanges ? "with" : "without");
    if (didMakeChanges) {
      if (postprocessor.requiresIdempotency()) {
        queue.addFirst(postprocessor);
      }
      if (postprocessor.compromisesVertexAffinity()) {
        queue.addFirst(affiliationGuardingPostprocessor);
      }
    }
    return didMakeChanges;
  }

}
