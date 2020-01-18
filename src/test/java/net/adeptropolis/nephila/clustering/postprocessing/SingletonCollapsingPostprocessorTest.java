/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.adeptropolis.nephila.clustering.Cluster;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SingletonCollapsingPostprocessorTest {

  @Test
  public void notApplicable() {
    SingletonCollapsingPostprocessor shaper = new SingletonCollapsingPostprocessor();
    Cluster rootCluster = new Cluster(null);
    Cluster childCluster1 = new Cluster(rootCluster);
    childCluster1.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));
    Cluster childCluster2 = new Cluster(rootCluster);
    childCluster2.addToRemainder(IntIterators.wrap(new int[]{4, 5, 6}));
    boolean modified = shaper.apply(childCluster1);
    assertFalse(modified);
    assertThat(rootCluster.getChildren().size(), is(2));
    assertThat(rootCluster.getRemainder(), is(IntLists.EMPTY_LIST));
    assertThat(childCluster1.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

  @Test
  public void doCollapse() {
    SingletonCollapsingPostprocessor shaper = new SingletonCollapsingPostprocessor();
    Cluster root = new Cluster(null);
    Cluster child = new Cluster(root);
    child.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));
    Cluster grandchild1 = new Cluster(child);
    Cluster grandchild2 = new Cluster(child);
    boolean modified = shaper.apply(child);
    assertTrue(modified);
    assertThat(root.getChildren().size(), is(2));
    assertThat(root.getChildren(), containsInAnyOrder(grandchild1, grandchild2));
    assertThat(grandchild1.getParent(), is(root));
    assertThat(grandchild2.getParent(), is(root));
    assertThat(root.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

}