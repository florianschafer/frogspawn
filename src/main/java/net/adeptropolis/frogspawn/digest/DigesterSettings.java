/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

import lombok.*;
import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.clustering.affiliation.DefaultAffiliationMetric;

import static net.adeptropolis.frogspawn.digest.DigestRankings.DEFAULT_COMBINED_RANKING;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class DigesterSettings {

  @Builder.Default
  private final AffiliationMetric affiliationMetric = new DefaultAffiliationMetric();
  @Builder.Default
  private final int maxDigestSize = 0;
  @Builder.Default
  private final boolean aggregateDigests = false;
  @Builder.Default
  private final DigestRanking digestRanking = DEFAULT_COMBINED_RANKING;

}
