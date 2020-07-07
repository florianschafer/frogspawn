/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class RemainderSizePostprocessorTest extends GraphTestBase {

  private static final IntArrayList ROOT_VERTICES = IntArrayList.wrap(new int[]{0});
  private static final IntArrayList C1_VERTICES = IntArrayList.wrap(new int[]{1, 2, 3});
  private static final IntArrayList C11_VERTICES = IntArrayList.wrap(new int[]{4, 5, 6});
  private static final IntArrayList C12_VERTICES = IntArrayList.wrap(new int[]{7, 8});
  private static final IntArrayList C121_VERTICES = IntArrayList.wrap(new int[]{9, 10, 11});

  private Cluster root;
  private Cluster c1;
  private Cluster c11;
  private Cluster c12;
  private Cluster c121;

  @Before
  public void setup() {
    Graph graph = completeGraph(12);
    root = new Cluster(graph);
    root.addToRemainder(ROOT_VERTICES.iterator());
    c1 = new Cluster(root);
    c1.addToRemainder(C1_VERTICES.iterator());
    c11 = new Cluster(c1);
    c11.addToRemainder(C11_VERTICES.iterator());
    c12 = new Cluster(c1);
    c12.addToRemainder(C12_VERTICES.iterator());
    c121 = new Cluster(c12);
    c121.addToRemainder(C121_VERTICES.iterator());
  }

  @Test
  public void traversalMode() {
    assertThat(new RemainderSizePostprocessor(1).traversalMode(), Is.is(TreeTraversalMode.LOCAL_BOTTOM_TO_TOP));
  }

  @Test
  public void lowMinSizeHasNoEffect() {
    assertThat(new RemainderSizePostprocessor(1).apply(c12), is(PostprocessingState.UNCHANGED));
    assertThat(c1.getChildren(), containsInAnyOrder(c11, c12));
    assertThat(c1.getRemainder(), is(C1_VERTICES));
    assertThat(c12.getChildren(), containsInAnyOrder(c121));
    assertThat(c12.getRemainder(), is(C12_VERTICES));
  }

  @Test
  public void mergeOneCluster() {
    assertThat(new RemainderSizePostprocessor(3).apply(c12), is(PostprocessingState.CHANGED));
    assertThat(c1.getChildren(), containsInAnyOrder(c11, c121));
    assertThat(c1.getRemainder(), is(IntArrayList.wrap(new int[]{1, 2, 3, 7, 8})));
  }

}