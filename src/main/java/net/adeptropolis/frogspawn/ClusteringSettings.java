/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn;

import lombok.*;
import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.clustering.affiliation.DefaultAffiliationMetric;

/**
 * Stores all relevant clustering settings
 */

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class ClusteringSettings {

  @Builder.Default private final AffiliationMetric affiliationMetric = new DefaultAffiliationMetric();
  @Builder.Default private final double minAffiliation = 0.2;
  @Builder.Default private final int minClusterSize = 50;
  @Builder.Default private final int trailSize = 20;
  @Builder.Default private final double convergenceThreshold = 0.95;
  @Builder.Default private final long randomSeed = 42133742L;
  @Builder.Default private final int maxIterations = 540;

}
