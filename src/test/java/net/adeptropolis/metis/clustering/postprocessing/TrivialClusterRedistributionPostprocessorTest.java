/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.Graph;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

public class TrivialClusterRedistributionPostprocessorTest {

  @Test
  public void noNonTrivialClusters() {
    Cluster root = new Cluster((Graph) null);
    Cluster c1 = new Cluster(root);
    new TrivialClusterRedistributionPostprocessor(root).postprocess();
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
    new TrivialClusterRedistributionPostprocessor(root).postprocess();
    assertThat(root.getChildren(), containsInAnyOrder(c1, c11, c2, c21, c211));
    assertThat(c1.getChildren(), empty());
    assertThat(c11.getChildren(), empty());
    assertThat(c2.getChildren(), empty());
    assertThat(c21.getChildren(), empty());
    assertThat(c211.getChildren(), containsInAnyOrder(c2111, c2112));
  }


}