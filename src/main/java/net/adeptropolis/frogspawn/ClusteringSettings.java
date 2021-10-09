/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn;

import lombok.*;
import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.clustering.affiliation.DefaultAffiliationMetric;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.SingletonMode;
import net.adeptropolis.frogspawn.digest.DigestRanking;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import net.adeptropolis.frogspawn.graphs.similarity.NormalizedCutMetric;

import java.util.ArrayList;
import java.util.List;

import static net.adeptropolis.frogspawn.digest.DigestRankings.DEFAULT_COMBINED_RANKING;

/**
 * Stores all relevant clustering settings
 */

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ClusteringSettings {

  // Core clustering
  @Builder.Default
  private final AffiliationMetric affiliationMetric = new DefaultAffiliationMetric();
  @Builder.Default
  private final double minAffiliation = 0.2;
  @Builder.Default
  private final int minClusterSize = 50;
  @Builder.Default
  private final int trailSize = 20;
  @Builder.Default
  private final double convergenceThreshold = 0.95;
  @Builder.Default
  private final long randomSeed = 42133742L;
  @Builder.Default
  private final int maxIterations = 540;

  // Postprocessing
  @Builder.Default
  private final GraphSimilarityMetric similarityMetric = new NormalizedCutMetric();
  @Builder.Default
  private final double minParentSimilarity = 0.09;
  @Builder.Default
  private final double maxParentSimilarity = 0.60;
  @Builder.Default
  private final double parentSimilarityAcceptanceLimit = 0.98;
  @Builder.Default
  private final int minChildren = 0;
  @Builder.Default
  private final SingletonMode singletonMode = SingletonMode.ASSIMILATE;
  @Builder.Default
  private final boolean flatten = false;
  @Builder.Default
  private final List<Postprocessor> customPostprocessors = new ArrayList<>();

  // Digestion
  @Builder.Default
  private final int maxDigestSize = 0;
  @Builder.Default
  private final boolean aggregateDigests = false;
  @Builder.Default
  private final DigestRanking digestRanking = DEFAULT_COMBINED_RANKING;

}
