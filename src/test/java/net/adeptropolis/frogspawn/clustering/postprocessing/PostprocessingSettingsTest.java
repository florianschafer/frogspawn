/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.SettingsTestBase;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.affiliation.RelativeWeightVertexAffiliationMetric;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.similarity.NormalizedCutMetric;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PostprocessingSettingsTest extends SettingsTestBase {

  private PostprocessingSettings postprocessingSettings;

  @Before
  public void setup() {
    postprocessingSettings = PostprocessingSettings.builder(clusteringSettings())
            .withMinChildren(1337)
            .withCustomPostprocessor(new FakeCustomPostprocessor())
            .withSimilarityMetric(new FakeSimilarityMetric())
            .withMinParentSimilarity(0.2718)
            .build();
  }

  @Test
  public void validateDefaults() {
    ClusteringSettings defaultClusteringSettings = ClusteringSettings.builder().build();
    PostprocessingSettings defaultSettings = PostprocessingSettings.builder(defaultClusteringSettings).build();
    assertThat(defaultSettings.getVertexAffiliationMetric(), instanceOf(RelativeWeightVertexAffiliationMetric.class));
    assertThat(defaultSettings.getMinVertexAffiliation(), closeTo(0.1, 1E-6));
    assertThat(defaultSettings.getSimilarityMetric(), instanceOf(NormalizedCutMetric.class));
    assertThat(defaultSettings.getMinParentSimilarity(), closeTo(0.01, 1E-9));
    assertThat(defaultSettings.getMinClusterSize(), is(50));
    assertThat(defaultSettings.getMinChildren(), is(0));
    assertThat(defaultSettings.getCustomPostprocessors(), empty());
  }

  @Test
  public void affiliationMetric() {
    assertThat(postprocessingSettings.getVertexAffiliationMetric(), instanceOf(FakeAffiliationMetric.class));
  }

  @Test
  public void minVertexAffiliation() {
    assertThat(postprocessingSettings.getMinVertexAffiliation(), closeTo(0.465, 1E-6));
  }

  @Test
  public void similarityMetric() {
    assertThat(postprocessingSettings.getSimilarityMetric(), instanceOf(FakeSimilarityMetric.class));
  }

  @Test
  public void minParentSimilarity() {
    assertThat(postprocessingSettings.getMinParentSimilarity(), closeTo(0.2718, 1E-9));
  }

  @Test
  public void minClusterSize() {
    assertThat(postprocessingSettings.getMinClusterSize(), is(4242));
  }

  @Test
  public void minChildren() {
    assertThat(postprocessingSettings.getMinChildren(), is(1337));
  }

  @Test
  public void customPostprocessor() {
    assertThat(postprocessingSettings.getCustomPostprocessors(), contains(instanceOf(FakeCustomPostprocessor.class)));
  }

  private static class FakeCustomPostprocessor implements Postprocessor {

    @Override
    public boolean apply(Cluster cluster) {
      return false;
    }

    @Override
    public TreeTraversalMode traversalMode() {
      return TreeTraversalMode.LOCAL_BOTTOM_TO_TOP;
    }
  }

  private static class FakeSimilarityMetric implements GraphSimilarityMetric {

    @Override
    public double compute(Graph supergraph, Graph subgraph) {
      return 0;
    }
  }


}