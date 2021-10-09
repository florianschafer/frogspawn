/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.clustering.affiliation.DefaultAffiliationMetric;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.SingletonMode;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;
import net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors.PostprocessingState;
import net.adeptropolis.frogspawn.digest.DigestRanking;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.ConstantSigTrailConvergence;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.PartialConvergenceCriterion;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import net.adeptropolis.frogspawn.graphs.similarity.NormalizedCutMetric;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static net.adeptropolis.frogspawn.clustering.postprocessing.SingletonMode.ASSIMILATE;
import static net.adeptropolis.frogspawn.clustering.postprocessing.SingletonMode.REDISTRIBUTE;
import static net.adeptropolis.frogspawn.digest.DigestRankings.DEFAULT_COMBINED_RANKING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ClusteringSettingsTest {

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

  private ClusteringSettings clusteringSettings() {
    return ClusteringSettings.builder()
            .affiliationMetric(new FakeAffiliationMetric())
            .minAffiliation(0.465)
            .minClusterSize(4242)
            .maxIterations(42356)
            .trailSize(783)
            .convergenceThreshold(0.74)
            .randomSeed(23857L)
            .minChildren(1337)
            .customPostprocessors(Collections.singletonList(new FakeCustomPostprocessor()))
            .similarityMetric(new FakeSimilarityMetric())
            .minParentSimilarity(0.2718)
            .maxParentSimilarity(3.1415)
            .parentSimilarityAcceptanceLimit(1.0 / 8)
            .singletonMode(SingletonMode.REDISTRIBUTE)
            .digestRanking(new FakeDigestRanking())
            .aggregateDigests(true)
            .maxDigestSize(42)
            .flatten(true)
            .build();
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
    assertThat(defaultSettings.getSimilarityMetric(), instanceOf(NormalizedCutMetric.class));
    assertThat(defaultSettings.getMinParentSimilarity(), closeTo(0.09, 1E-9));
    assertThat(defaultSettings.getMaxParentSimilarity(), closeTo(0.60, 1E-9));
    assertThat(defaultSettings.getParentSimilarityAcceptanceLimit(), closeTo(0.98, 1E-9));
    assertThat(defaultSettings.getMinClusterSize(), is(50));
    assertThat(defaultSettings.getMinChildren(), is(0));
    assertThat(defaultSettings.getSingletonMode(), is(ASSIMILATE));
    assertThat(defaultSettings.getCustomPostprocessors(), empty());
    assertThat(defaultSettings.getMaxDigestSize(), is(0));
    assertThat(defaultSettings.getAffiliationMetric(), instanceOf(DefaultAffiliationMetric.class));
    assertThat(defaultSettings.isAggregateDigests(), is(false));
    assertThat(defaultSettings.getDigestRanking(), instanceOf(DEFAULT_COMBINED_RANKING.getClass()));
    assertThat(defaultSettings.isFlatten(), is(false));
  }

  private void validateConvergenceCriterion(ClusteringSettings settings, int expectedTrailSize, int expectedThreshold) {
    PartialConvergenceCriterion convergenceCriterion = new ConstantSigTrailConvergence(graph, settings.getTrailSize(), settings.getConvergenceThreshold());
    assertThat(convergenceCriterion, instanceOf(ConstantSigTrailConvergence.class));
    ConstantSigTrailConvergence constantSigTrailConvergence = (ConstantSigTrailConvergence) convergenceCriterion;
    assertThat(constantSigTrailConvergence.getTrailSize(), is(expectedTrailSize));
    assertThat(constantSigTrailConvergence.getThreshold(), is(expectedThreshold));
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

  @Test
  public void similarityMetric() {
    assertThat(clusteringSettings.getSimilarityMetric(), instanceOf(FakeSimilarityMetric.class));
  }

  @Test
  public void minParentSimilarity() {
    assertThat(clusteringSettings.getMinParentSimilarity(), closeTo(0.2718, 1E-9));
  }

  @Test
  public void maxParentSimilarity() {
    assertThat(clusteringSettings.getMaxParentSimilarity(), closeTo(3.1415, 1E-9));
  }

  @Test
  public void parentSimilarityCompletionThreshold() {
    assertThat(clusteringSettings.getParentSimilarityAcceptanceLimit(), closeTo(1.0 / 8, 1E-9));
  }

  @Test
  public void minClusterSize() {
    assertThat(clusteringSettings.getMinClusterSize(), is(4242));
  }

  @Test
  public void minChildren() {
    assertThat(clusteringSettings.getMinChildren(), is(1337));
  }

  @Test
  public void singletonMode() {
    assertThat(clusteringSettings.getSingletonMode(), is(REDISTRIBUTE));
  }

  @Test
  public void flatten() {
    assertThat(clusteringSettings.isFlatten(), is(true));
  }

  @Test
  public void customPostprocessor() {
    assertThat(clusteringSettings.getCustomPostprocessors(), contains(instanceOf(FakeCustomPostprocessor.class)));
  }

  @Test
  public void digestRanking() {
    assertThat(clusteringSettings.getDigestRanking(), instanceOf(FakeDigestRanking.class));
  }

  @Test
  public void aggregate() {
    assertThat(clusteringSettings.isAggregateDigests(), is(true));
  }

  @Test
  public void digestSize() {
    assertThat(clusteringSettings.getMaxDigestSize(), is(42));
  }

  private static class FakeAffiliationMetric implements AffiliationMetric {

    @Override
    public double[] compute(Graph supergraph, Graph subgraph) {
      return new double[0];
    }

    @Override
    public double[] compute(Graph supergraph, Graph subgraph, Graph subsubgraph) {
      return new double[0];
    }
  }

  private static class FakeDigestRanking implements DigestRanking {

    @Override
    public double compute(int vertexId, double weight, double score) {
      return 0;
    }
  }

  private static class FakeCustomPostprocessor implements Postprocessor {

    @Override
    public PostprocessingState apply(Cluster cluster) {
      return PostprocessingState.UNCHANGED;
    }

    @Override
    public TreeTraversalMode traversalMode() {
      return TreeTraversalMode.LOCAL_BOTTOM_TO_TOP;
    }

    @Override
    public boolean compromisesVertexAffinity() {
      return false;
    }

    @Override
    public boolean requiresIdempotency() {
      return false;
    }
  }

  private static class FakeSimilarityMetric implements GraphSimilarityMetric {

    @Override
    public double compute(Graph supergraph, Graph subgraph) {
      return 0;
    }
  }

}