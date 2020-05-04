/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

import org.junit.Before;
import org.junit.Test;

import static net.adeptropolis.frogspawn.digest.DigestRankings.SCORE_RANKING;
import static net.adeptropolis.frogspawn.digest.DigestRankings.WEIGHT_RANKING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class MemberSortOpsTest {

  private int[] vertices;
  private double[] weights;
  private double[] scores;

  @Before
  public void initialize() {
    vertices = new int[]{1, 2};
    weights = new double[]{10d, 20d};
    scores = new double[]{100d, 200d};
  }

  @Test
  public void sortByCustomAscendingVertex() {
    sort((vertexId, weight, score) -> -vertexId);
    assertIsAscending();
  }

  @Test
  public void sortByCustomDescendingVertex() {
    sort((vertexId, weight, score) -> vertexId);
    assertIsDescending();
  }

  @Test
  public void sortByWeight() {
    sort(WEIGHT_RANKING);
    assertIsDescending();
  }

  @Test
  public void sortByCustomAscendingWeight() {
    sort((vertexId, weight, score) -> -weight);
    assertIsAscending();
  }

  @Test
  public void sortByScore() {
    sort(SCORE_RANKING);
    assertIsDescending();
  }

  @Test
  public void sortByCustomAscendingScore() {
    sort((vertexId, weight, score) -> -score);
    assertIsAscending();
  }

  private void assertIsAscending() {
    assertThat(vertices[0], is(1));
    assertThat(vertices[1], is(2));
    assertThat(weights[0], closeTo(10d, 1E-6));
    assertThat(weights[1], closeTo(20d, 1E-6));
    assertThat(scores[0], closeTo(100d, 1E-6));
    assertThat(scores[1], closeTo(200d, 1E-6));
  }

  private void assertIsDescending() {
    assertThat(vertices[0], is(2));
    assertThat(vertices[1], is(1));
    assertThat(weights[0], closeTo(20d, 1E-6));
    assertThat(weights[1], closeTo(10d, 1E-6));
    assertThat(scores[0], closeTo(200d, 1E-6));
    assertThat(scores[1], closeTo(100d, 1E-6));
  }

  private void sort(DigestRanking ranking) {
    MemberSortOps.sort(vertices, weights, scores, ranking);
  }

}