/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import com.google.common.collect.Lists;
import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.postprocessing.postprocessors.*;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    SingletonRedistributionPostprocessor singletonRedistribution = new SingletonRedistributionPostprocessor();
    VertexAffiliationGuardingPostprocessor affiliation = new VertexAffiliationGuardingPostprocessor(
            settings.getVertexAffiliationMetric(), settings.getMinClusterSize(), settings.getMinVertexAffiliation());
    DescendantCollapsingPostprocessor subclusterCount = new DescendantCollapsingPostprocessor(settings.getMinChildren());
    SingletonCollapsingPostprocessor singletons = new SingletonCollapsingPostprocessor();
    List<Postprocessor> pipeline = Lists.newArrayList(
            remainderSize,
            singletonRedistribution,
            subclusterCount,
            affiliation,
            remainderSize,
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
   * Apply a specific postprocessor
   *
   * @param postprocessor Postprocessor
   */

  private void applyPostprocessor(Postprocessor postprocessor) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    boolean changed = PostprocessingTraversal.apply(postprocessor, rootCluster);
    stopWatch.stop();
    LOG.debug("{} finished in {}. There were {} to the cluster hierarchy.",
            postprocessor.getClass().getSimpleName(), stopWatch, changed ? "changes" : "no changes");
  }

}
