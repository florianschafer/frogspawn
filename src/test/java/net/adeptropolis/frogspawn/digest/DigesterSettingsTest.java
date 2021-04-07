/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.SettingsTestBase;
import net.adeptropolis.frogspawn.clustering.affiliation.DefaultAffiliationMetric;
import org.junit.Before;
import org.junit.Test;

import static net.adeptropolis.frogspawn.digest.DigestRankings.DEFAULT_COMBINED_RANKING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class DigesterSettingsTest extends SettingsTestBase {

  private DigesterSettings digesterSettings;

  @Before
  public void setup() {
    digesterSettings = DigesterSettings.builder()
            .affiliationMetric(new FakeAffiliationMetric())
            .digestRanking(new FakeDigestRanking())
            .aggregateDigests(true)
            .maxDigestSize(42)
            .build();
  }

  @Test
  public void validateDefaults() {
    DigesterSettings defaultSettings = DigesterSettings.builder().build();
    assertThat(defaultSettings.getMaxDigestSize(), is(0));
    assertThat(defaultSettings.getAffiliationMetric(), instanceOf(DefaultAffiliationMetric.class));
    assertThat(defaultSettings.isAggregateDigests(), is(false));
    assertThat(defaultSettings.getDigestRanking(), instanceOf(DEFAULT_COMBINED_RANKING.getClass()));
  }

  @Test
  public void affiliationMetric() {
    assertThat(digesterSettings.getAffiliationMetric(), instanceOf(FakeAffiliationMetric.class));
  }

  @Test
  public void digestRanking() {
    assertThat(digesterSettings.getDigestRanking(), instanceOf(FakeDigestRanking.class));
  }

  @Test
  public void aggregate() {
    assertThat(digesterSettings.isAggregateDigests(), is(true));
  }

  @Test
  public void digestSize() {
    assertThat(digesterSettings.getMaxDigestSize(), is(42));
  }

  private static class FakeDigestRanking implements DigestRanking {

    @Override
    public double compute(int vertexId, double weight, double score) {
      return 0;
    }
  }

}