/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.DefaultLabeling;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static net.adeptropolis.frogspawn.digest.DigestRankings.WEIGHT_RANKING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class ClusterDigesterTest {

  private static final DigesterSettings aggregate3Settings = DigesterSettings.builder()
          .maxDigestSize(3)
          .aggregateDigests(true)
          .digestRanking(WEIGHT_RANKING)
          .build();

  private static final DigesterSettings remainder2Settings = DigesterSettings.builder()
          .maxDigestSize(2)
          .aggregateDigests(false)
          .digestRanking(WEIGHT_RANKING)
          .build();

  private Graph graph;
  private Cluster c2;

  @Before
  public void setup() {
    graph = new SparseGraphBuilder()
            .add(0, 1, 1).add(0, 2, 2).add(0, 3, 3).add(0, 4, 4).add(0, 5, 5).add(0, 6, 6)
            .add(1, 2, 7).add(1, 3, 8).add(1, 4, 9).add(1, 5, 10).add(1, 6, 11)
            .add(2, 3, 12)
            .add(4, 5, 13).add(4, 6, 14).add(4, 7, 15).add(4, 8, 16).add(4, 9, 17)
            .add(5, 6, 18).add(5, 7, 19).add(5, 8, 20).add(5, 9, 21)
            .add(6, 7, 22)
            .add(8, 9, 23).build();
    Cluster root = new Cluster(graph);
    root.addToRemainder(IntIterators.wrap(new int[]{0, 1}));
    Cluster c1 = new Cluster(root);
    c1.addToRemainder(IntIterators.wrap(new int[]{2, 3}));
    c2 = new Cluster(root);
    c2.addToRemainder(IntIterators.wrap(new int[]{4, 5}));
    Cluster c21 = new Cluster(c2);
    c21.addToRemainder(IntIterators.wrap(new int[]{6, 7}));
    Cluster c22 = new Cluster(c2);
    c22.addToRemainder(IntIterators.wrap(new int[]{8, 9}));
  }

  @Test
  public void aggregateEmptyGraph() {
    Cluster root = new Cluster(new SparseGraphBuilder().build());
    Digest digest = new ClusterDigester(aggregate3Settings).digest(root);
    assertThat(digest.getVertices().length, is(0));
    assertThat(digest.getWeights().length, is(0));
    assertThat(digest.getScores().length, is(0));
  }

  @Test
  public void aggregate() {
    Digest digest = new ClusterDigester(aggregate3Settings).digest(c2);
    assertThat(digest.getVertices().length, is(3));
    assertThat(digest.getVertices()[0], is(5));
    assertThat(digest.getVertices()[1], is(4));
    assertThat(digest.getVertices()[2], is(9));
    assertThat(digest.getWeights().length, is(3));
    assertThat(digest.getWeights()[0], closeTo(91, 1E-9));
    assertThat(digest.getWeights()[1], closeTo(75, 1E-9));
    assertThat(digest.getWeights()[2], closeTo(61, 1E-9));
    assertThat(digest.getScores().length, is(3));
    assertThat(digest.getScores()[0], closeTo(0.858490566, 1E-9));
    assertThat(digest.getScores()[1], closeTo(0.852272727, 1E-9));
    assertThat(digest.getScores()[2], closeTo(1.0, 1E-9));
  }

  @Test
  public void remainderEmptyGraph() {
    Cluster root = new Cluster(new SparseGraphBuilder().build());
    Digest digest = new ClusterDigester(remainder2Settings).digest(root);
    assertThat(digest.getVertices().length, is(0));
    assertThat(digest.getWeights().length, is(0));
    assertThat(digest.getScores().length, is(0));
  }

  @Test
  public void remainder() {
    Digest digest = new ClusterDigester(remainder2Settings).digest(c2);
    assertThat(digest.getVertices().length, is(2));
    assertThat(digest.getVertices()[0], is(5));
    assertThat(digest.getVertices()[1], is(4));
    assertThat(digest.getWeights()[0], closeTo(91, 1E-9));
    assertThat(digest.getWeights()[1], closeTo(75, 1E-9));
    assertThat(digest.getScores().length, is(2));
    assertThat(digest.getScores()[0], closeTo(0.858490566, 1E-9));
    assertThat(digest.getScores()[1], closeTo(0.852272727, 1E-9));
  }

  @Test
  public void unlimitedSize() {
    DigesterSettings unlimitedSettings = DigesterSettings.builder()
            .maxDigestSize(0)
            .aggregateDigests(true)
            .digestRanking(WEIGHT_RANKING)
            .build();
    Digest digest = new ClusterDigester(unlimitedSettings).digest(c2);
    assertThat(digest.size(), is(6));
    assertThat(digest.size(), is(digest.totalSize()));
  }

  @Test
  public void mapping() {
    String digestFingerprint = new ClusterDigester(aggregate3Settings)
            .digest(c2)
            .map((vertexId, weight, score) -> String.format(Locale.US, "%d|%.1f|%.3f", vertexId, weight, score))
            .collect(Collectors.joining(","));
    assertThat(digestFingerprint, is("5|91.0|0.858,4|75.0|0.852,9|61.0|1.000"));
  }

  @Test
  public void labeledMapping() {
    DefaultLabeling<String> labeling = new DefaultLabeling<>(String.class);
    for (int i = 0; i < graph.order(); i++) {
      labeling.id(String.format("[%d]", i));
    }
    String digestFingerprint = new ClusterDigester(aggregate3Settings)
            .digest(c2)
            .map((label, weight, score) -> String.format(Locale.US, "%s|%.1f|%.3f", label, weight, score), labeling)
            .collect(Collectors.joining(","));
    assertThat(digestFingerprint, is("[5]|91.0|0.858,[4]|75.0|0.852,[9]|61.0|1.000"));
  }

  @Test
  public void labeledForEach() {
    DefaultLabeling<String> labeling = new DefaultLabeling<>(String.class);
    for (int i = 0; i < graph.order(); i++) {
      labeling.id(String.format("[%d]", i));
    }
    List<String> instances = Lists.newArrayList();
    new ClusterDigester(aggregate3Settings)
            .digest(c2)
            .forEach(labeling, (label, weight, score) -> instances.add(String.format(Locale.US, "%s|%.1f|%.3f", label, weight, score)));
    assertThat(String.join(",", instances), is("[5]|91.0|0.858,[4]|75.0|0.852,[9]|61.0|1.000"));
  }

}