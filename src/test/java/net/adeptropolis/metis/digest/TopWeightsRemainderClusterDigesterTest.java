/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TopWeightsRemainderClusterDigesterTest {

  @Test
  public void basicFunctionality() {
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 1)
            .add(0, 2, 2)
            .add(0, 3, 3)
            .build();
    Cluster root = new Cluster(null);
    root.addToRemainder(IntIterators.wrap(new int[]{0, 1, 2, 3}));
    Digest digest = new TopWeightsRemainderClusterDigester(2, graph).create(root);
    assertThat(digest.size(), is(2));
    assertThat(digest.totalSize(), is(4));
    assertThat(digest.getVertices()[0], is(0));
    assertThat(digest.getVertices()[1], is(3));
    assertThat(digest.getWeights()[0], closeTo(6, 1E-6));
    assertThat(digest.getWeights()[1], closeTo(3, 1E-6));
  }

}