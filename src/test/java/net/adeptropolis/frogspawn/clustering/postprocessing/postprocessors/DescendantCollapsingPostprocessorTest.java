/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DescendantCollapsingPostprocessorTest extends GraphTestBase {

  private Cluster root;
  private Cluster c1;
  private Cluster c2;
  private Cluster c21;
  private Cluster c22;
  private Cluster c221;
  private DescendantCollapsingPostprocessor postprocessor;

  @Before
  public void setup() {
    postprocessor = new DescendantCollapsingPostprocessor(3);
    root = new Cluster((Graph) null);
    c1 = new Cluster(root);
    c2 = new Cluster(root);
    c21 = new Cluster(c2);
    c22 = new Cluster(c2);
    c221 = new Cluster(c22);
  }

  @Test
  public void doesNotAffectRoot() {
    postprocessor.apply(root);
    assertThat(root.getParent(), is(IsNull.nullValue()));
  }

  @Test
  public void doesNotAffectL1Clusters() {
    postprocessor.apply(c1);
    postprocessor.apply(c2);
    assertThat(c1.getParent(), is(root));
    assertThat(c2.getParent(), is(root));
  }

  @Test
  public void affectsL2Clusters() {
    postprocessor.apply(c21);
    assertThat(c21.getParent(), is(root));
  }

  @Test
  public void fillingUpStopsWhenThresholdIsReached() {
    postprocessor.apply(c21);
    postprocessor.apply(c22);
    assertThat(root.getChildren().size(), is(3));
  }

  @Test
  public void affectsL3Clusters() {
    postprocessor.apply(c221);
    assertThat(c221.getParent(), is(c2));
  }

}