/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn;

import net.adeptropolis.frogspawn.clustering.affiliation.DefaultAffiliationMetric;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.ConstantSigTrailConvergence;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.PartialConvergenceCriterion;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ClusteringSettingsTest extends SettingsTestBase {

  private ClusteringSettings clusteringSettings;
  private Graph graph;

  @Before
  public void setup() {
    SparseGraphBuilder builder = SparseGraph.builder();
    for (int i = 0; i < 99; i++) {
      builder.add(i, i + 1, i + 1);
    }
    graph = builder.build();
    clusteringSettings = clusteringSettings();
  }

  @Test
  public void validateDefaults() {
    ClusteringSettings defaultSettings = ClusteringSettings.builder().build();
    assertThat(defaultSettings.getAffiliationMetric(), instanceOf(DefaultAffiliationMetric.class));
    assertThat(defaultSettings.getMinAffiliation(), closeTo(0.2, 1E-6));
    assertThat(defaultSettings.getMinClusterSize(), is(50));
    assertThat(defaultSettings.getMaxIterations(), is(540));
    assertThat(defaultSettings.getRandomSeed(), is(42133742L));
    validateConvergenceCriterion(defaultSettings, 20, 95);

  }

  @Test
  public void AffiliationMetric() {
    assertThat(clusteringSettings.getAffiliationMetric(), instanceOf(FakeAffiliationMetric.class));
  }

  @Test
  public void minAffiliation() {
    assertThat(clusteringSettings.getMinAffiliation(), closeTo(0.465, 1E-6));
  }

  @Test
  public void minClustersize() {
    assertThat(clusteringSettings.getMinClusterSize(), is(4242));
  }

  @Test
  public void maxIterations() {
    assertThat(clusteringSettings.getMaxIterations(), is(42356));
  }

  @Test
  public void randomSeed() {
    assertThat(clusteringSettings.getRandomSeed(), is(23857L));
  }

  @Test
  public void convergenceCriterion() {
    validateConvergenceCriterion(clusteringSettings, 783, 74);
  }

  private void validateConvergenceCriterion(ClusteringSettings settings, int expectedTrailSize, int expectedThreshold) {
    PartialConvergenceCriterion convergenceCriterion = settings.convergenceCriterionForGraph(graph);
    assertThat(convergenceCriterion, instanceOf(ConstantSigTrailConvergence.class));
    ConstantSigTrailConvergence constantSigTrailConvergence = (ConstantSigTrailConvergence) convergenceCriterion;
    assertThat(constantSigTrailConvergence.getTrailSize(), is(expectedTrailSize));
    assertThat(constantSigTrailConvergence.getThreshold(), is(expectedThreshold));
  }


}