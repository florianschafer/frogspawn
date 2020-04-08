/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
import net.adeptropolis.metis.clustering.consistency.RelativeWeightConsistencyMetric;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static net.adeptropolis.metis.digest.ClusterDigester.WEIGHT_RANKING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class ClusterDigesterTest {

  private static final RelativeWeightConsistencyMetric metric = new RelativeWeightConsistencyMetric();

  @Test
  public void aggregate() {
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 1)
            .add(0, 2, 2)
            .add(0, 3, 3)
            .add(0, 4, 4)
            .add(0, 5, 5)
            .add(0, 6, 6)
            .add(1, 2, 7)
            .add(1, 3, 8)
            .add(1, 4, 9)
            .add(1, 5, 10)
            .add(1, 6, 11)
            .add(2, 3, 12)
            .add(4, 5, 13)
            .add(4, 6, 14)
            .add(4, 7, 15)
            .add(4, 8, 16)
            .add(4, 9, 17)
            .add(5, 6, 18)
            .add(5, 7, 19)
            .add(5, 8, 20)
            .add(5, 9, 21)
            .add(6, 7, 22)
            .add(8, 9, 23)
            .build();
    Cluster root = new Cluster(graph);
    root.addToRemainder(IntIterators.wrap(new int[]{0, 1}));
    Cluster c1 = new Cluster(root);
    c1.addToRemainder(IntIterators.wrap(new int[]{2, 3}));
    Cluster c2 = new Cluster(root);
    c2.addToRemainder(IntIterators.wrap(new int[]{4, 5}));
    Cluster c21 = new Cluster(c2);
    c2.addToRemainder(IntIterators.wrap(new int[]{6, 7}));
    Cluster c22 = new Cluster(c2);
    c2.addToRemainder(IntIterators.wrap(new int[]{8, 9}));
    Digest digest = new ClusterDigester(metric, 3, true, WEIGHT_RANKING).create(c2);
    assertThat(digest.getVertices().length, is(3));
    assertThat(digest.getVertices()[0], is(5));
    assertThat(digest.getVertices()[1], is(4));
    assertThat(digest.getVertices()[2], is(9));
    assertThat(digest.getWeights().length, is(3));
    assertThat(digest.getWeights()[0], closeTo(91, 1E-9));
    assertThat(digest.getWeights()[1], closeTo(75, 1E-9));
    assertThat(digest.getWeights()[2], closeTo(61, 1E-9));
    assertThat(digest.getScores().length, is(3));
    assertThat(digest.getScores()[0], closeTo(0.8584905660377359, 1E-9));
    assertThat(digest.getScores()[1], closeTo(0.8522727272727273, 1E-9));
    assertThat(digest.getScores()[2], closeTo(1.0, 1E-9));
  }

  @Test
  public void aggregateSmallGraph() {
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 1)
            .add(0, 2, 2)
            .build();
    Cluster root = new Cluster(graph);
    root.addToRemainder(IntIterators.wrap(new int[]{0, 1}));
    Digest digest = new ClusterDigester(metric, 3, true, WEIGHT_RANKING).create(root);
    assertThat(digest.getVertices().length, is(2));
    assertThat(digest.getWeights().length, is(2));
    assertThat(digest.getScores().length, is(2));
  }

  @Test
  public void aggregateEmptyGraph() {
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder().build();
    Cluster root = new Cluster(graph);
    root.addToRemainder(IntIterators.wrap(new int[]{}));
    Digest digest = new ClusterDigester(metric, 3, true, WEIGHT_RANKING).create(root);
    assertThat(digest.getVertices().length, is(0));
    assertThat(digest.getWeights().length, is(0));
    assertThat(digest.getScores().length, is(0));
  }

  @Test
  public void remainderOnly() {
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 1)
            .add(0, 2, 2)
            .add(0, 3, 3)
            .build();
    Cluster root = new Cluster(graph);
    root.addToRemainder(IntIterators.wrap(new int[]{0, 1, 2, 3}));
    Digest digest = new ClusterDigester(metric, 2, false, WEIGHT_RANKING).create(root);
    assertThat(digest.size(), is(2));
    assertThat(digest.totalSize(), is(4));
    assertThat(digest.getVertices()[0], is(0));
    assertThat(digest.getVertices()[1], is(3));
    assertThat(digest.getWeights()[0], closeTo(6, 1E-6));
    assertThat(digest.getWeights()[1], closeTo(3, 1E-6));
  }


}