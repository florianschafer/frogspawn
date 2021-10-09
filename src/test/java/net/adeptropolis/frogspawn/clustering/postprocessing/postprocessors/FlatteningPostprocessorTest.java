/*
 * Copyright (c) Florian Schaefer 2021.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;
import net.adeptropolis.frogspawn.graphs.Graph;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FlatteningPostprocessorTest {

  private final FlatteningPostprocessor postprocessor = new FlatteningPostprocessor();

  @Test
  public void traversalMode() {
    assertThat(new FlatteningPostprocessor().traversalMode(), Is.is(TreeTraversalMode.LOCAL_BOTTOM_TO_TOP));
  }

  @Test
  public void flatten() {

    Cluster rootCluster = new Cluster((Graph) null);

    Cluster childCluster1 = new Cluster(rootCluster);
    childCluster1.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));

    Cluster childCluster2 = new Cluster(rootCluster);
    childCluster2.addToRemainder(IntIterators.wrap(new int[]{4, 5, 6}));

    Cluster childCluster21 = new Cluster(childCluster2);
    childCluster21.addToRemainder(IntIterators.wrap(new int[]{7, 8, 9}));

    assertThat(postprocessor.apply(rootCluster).madeHierarchyChanges(), is(false));
    assertThat(postprocessor.apply(childCluster1).madeHierarchyChanges(), is(false));
    assertThat(postprocessor.apply(childCluster2).madeHierarchyChanges(), is(false));

    PostprocessingState state = postprocessor.apply(childCluster21);
    assertThat(state.madeHierarchyChanges(), is(true));
    assertThat(childCluster2.getChildren().size(), is(0));
    assertThat(childCluster2.getRemainder(), is(IntArrayList.wrap(new int[]{4, 5, 6, 7, 8, 9})));
  }

}