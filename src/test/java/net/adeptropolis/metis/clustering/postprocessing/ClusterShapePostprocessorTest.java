/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.Graph;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ClusterShapePostprocessorTest {

  @Test
  public void defaultBehaviour() {
    Cluster root = new Cluster((Graph) null);
    Cluster c1 = new Cluster(root);
    Cluster c2 = new Cluster(root);
    Cluster c21 = new Cluster(c2);
    Cluster c22 = new Cluster(c2);
    Cluster c23 = new Cluster(c2);
    new ClusterShapePostprocessor(root, 10).postprocess();
    assertThat(root.getChildren(), containsInAnyOrder(c1, c2, c21, c22, c23));
  }

}