/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.SettingsTestBase;
import net.adeptropolis.metis.clustering.affiliation.RelativeWeightVertexAffiliationMetric;
import org.junit.Before;
import org.junit.Test;

import static net.adeptropolis.metis.digest.DigestRankings.DEFAULT_COMBINED_RANKING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class DigesterSettingsTest extends SettingsTestBase {

  private DigesterSettings digesterSettings;

  @Before
  public void setup() {
    digesterSettings = DigesterSettings.builder(clusteringSettings())
            .withDigestRanking(new FakeDigestRanking())
            .withAggregateDigests(true)
            .withMaxDigestSize(42)
            .build();
  }

  @Test
  public void validateDefaults() {
    ClusteringSettings defaultClusteringSettings = ClusteringSettings.builder().build();
    DigesterSettings defaultSettings = DigesterSettings.builder(defaultClusteringSettings).build();
    assertThat(defaultSettings.getMaxDigestSize(), is(0));
    assertThat(defaultSettings.getVertexAffiliationMetric(), instanceOf(RelativeWeightVertexAffiliationMetric.class));
    assertThat(defaultSettings.doAggregateDigests(), is(false));
    assertThat(defaultSettings.getDigestRanking(), instanceOf(DEFAULT_COMBINED_RANKING.getClass()));
  }

  @Test
  public void affiliationMetric() {
    assertThat(digesterSettings.getVertexAffiliationMetric(), instanceOf(FakeAffiliationMetric.class));
  }

  @Test
  public void digestRanking() {
    assertThat(digesterSettings.getDigestRanking(), instanceOf(FakeDigestRanking.class));
  }

  @Test
  public void aggregate() {
    assertThat(digesterSettings.doAggregateDigests(), is(true));
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