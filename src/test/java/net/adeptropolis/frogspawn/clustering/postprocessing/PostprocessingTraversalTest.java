/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import com.google.common.collect.Lists;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.Graph;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class PostprocessingTraversalTest {

  private Cluster root;
  private Cluster c1;
  private Cluster c2;

  @Before
  public void setup() {
    root = new Cluster((Graph) null);
    c1 = new Cluster(root);
    c2 = new Cluster(c1);
  }

  @Test
  public void localBTTPostprocessorsWithoutChange() {
    CollectingLocalBTTPostprocessor pp = new CollectingLocalBTTPostprocessor(false);
    assertThat(PostprocessingTraversal.apply(pp, root), is(false));
    assertThat(pp.getClusters(), contains(c2, c1, root));
  }

  @Test
  public void localBTTPostprocessorsWithChange() {
    CollectingLocalBTTPostprocessor pp = new CollectingLocalBTTPostprocessor(true);
    assertThat(PostprocessingTraversal.apply(pp, root), is(true));
    assertThat(pp.getClusters(), contains(c2, c1, root));
  }

  @Test
  public void globalCustomTraversalPostprocessorsWithoutChange() {
    CollectingGlobalCustomTraversalPostprocessor pp = new CollectingGlobalCustomTraversalPostprocessor(false);
    assertThat(PostprocessingTraversal.apply(pp, root), is(false));
    assertThat(pp.getClusters(), contains(root));
  }

  @Test
  public void globalCustomTraversalPostprocessorsWithChange() {
    CollectingGlobalCustomTraversalPostprocessor pp = new CollectingGlobalCustomTraversalPostprocessor(true);
    assertThat(PostprocessingTraversal.apply(pp, root), is(true));
    assertThat(pp.getClusters(), contains(root));
  }

  private static class CollectingLocalBTTPostprocessor implements Postprocessor {

    private final List<Cluster> clusters;
    private final boolean reportChanges;

    private CollectingLocalBTTPostprocessor(boolean reportChanges) {
      this.clusters = Lists.newArrayList();
      this.reportChanges = reportChanges;
    }

    @Override
    public boolean apply(Cluster cluster) {
      clusters.add(cluster);
      return reportChanges;
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

    List<Cluster> getClusters() {
      return clusters;
    }

  }

  private static class CollectingGlobalCustomTraversalPostprocessor implements Postprocessor {

    private final List<Cluster> clusters;
    private final boolean reportChanges;

    private CollectingGlobalCustomTraversalPostprocessor(boolean reportChanges) {
      this.clusters = Lists.newArrayList();
      this.reportChanges = reportChanges;
    }

    @Override
    public boolean apply(Cluster cluster) {
      clusters.add(cluster);
      return reportChanges;
    }

    @Override
    public TreeTraversalMode traversalMode() {
      return TreeTraversalMode.GLOBAL_CUSTOM;
    }

    @Override
    public boolean compromisesVertexAffinity() {
      return false;
    }

    @Override
    public boolean requiresIdempotency() {
      return false;
    }

    List<Cluster> getClusters() {
      return clusters;
    }

  }


}