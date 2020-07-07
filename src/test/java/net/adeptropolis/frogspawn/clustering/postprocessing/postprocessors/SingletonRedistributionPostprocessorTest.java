/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;
import net.adeptropolis.frogspawn.graphs.Graph;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SingletonRedistributionPostprocessorTest {

  @Test
  public void traversalMode() {
    assertThat(new SingletonRedistributionPostprocessor().traversalMode(), Is.is(TreeTraversalMode.GLOBAL_CUSTOM));
  }

  @Test
  public void noNonTrivialClusters() {
    Cluster root = new Cluster((Graph) null);
    Cluster c1 = new Cluster(root);
    PostprocessingState state = new SingletonRedistributionPostprocessor().apply(root);
    assertThat(state.madeHierarchyChanges(), is(false));
    assertThat(state.forceQualityGuard(), is(false));
    assertThat(root.getChildren(), containsInAnyOrder(c1));
  }

  @Test
  public void genericBehaviour() {
    Cluster root = new Cluster((Graph) null);
    Cluster c1 = new Cluster(root);
    Cluster c11 = new Cluster(c1);
    Cluster c2 = new Cluster(root);
    Cluster c21 = new Cluster(c2);
    Cluster c211 = new Cluster(c21);
    Cluster c2111 = new Cluster(c211);
    Cluster c2112 = new Cluster(c211);
    PostprocessingState state = new SingletonRedistributionPostprocessor().apply(root);
    assertThat(state.madeHierarchyChanges(), is(true));
    assertThat(state.forceQualityGuard(), is(false));
    assertThat(root.getChildren(), containsInAnyOrder(c1, c11, c2, c21, c211));
    assertThat(c1.getChildren(), empty());
    assertThat(c11.getChildren(), empty());
    assertThat(c2.getChildren(), empty());
    assertThat(c21.getChildren(), empty());
    assertThat(c211.getChildren(), containsInAnyOrder(c2111, c2112));
  }


}