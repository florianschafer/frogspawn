/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import net.adeptropolis.metis.helpers.Arr;

/**
 * Helper class for sorting aligned lists of vertices, weights and consistency scores by weight
 */

class MemberSortOps implements IntComparator, Swapper {

  private final int[] vertices;
  private final double[] weights;
  private final double[] scores;
  private final ClusterMemberRanking ranking;

  /**
   * Constructor
   *
   * @param vertices Vertices
   * @param weights  Vertex weights
   * @param scores   Vertex consistency scores
   * @param ranking  Intra-cluster vertex ranking function
   */

  private MemberSortOps(int[] vertices, double[] weights, double[] scores, ClusterMemberRanking ranking) {
    this.vertices = vertices;
    this.weights = weights;
    this.scores = scores;
    this.ranking = ranking;
  }

  /**
   * Sort a given (vertices, weights, scores)-triple using a supplied vertex ranking
   *
   * @param vertices Cluster vertices
   * @param weights  Vertex weights
   * @param scores   Vertex scores
   * @param ranking  Intra-cluster vertex ranking function
   */

  public static void sort(int[] vertices, double[] weights, double[] scores, ClusterMemberRanking ranking) {
    MemberSortOps ops = new MemberSortOps(vertices, weights, scores, ranking);
    Arrays.mergeSort(0, vertices.length, ops, ops);
  }

  /**
   * Simultaneously swap array elements
   *
   * @param i First index
   * @param j Second index
   */

  @Override
  public void swap(int i, int j) {
    Arr.swap(vertices, i, j);
    Arr.swap(weights, i, j);
    Arr.swap(scores, i, j);
  }

  /**
   * Compare two indices by the supplied ranking function. Defaults to descending order!
   *
   * @param i First index
   * @param j Second index
   * @return -1 if the ranking function of i is greater than that of j. 1 Otherwise.
   */

  @Override
  public int compare(int i, int j) {
    return Double.compare(
            ranking.compute(vertices[j], weights[j], scores[j]),
            ranking.compute(vertices[i], weights[i], scores[i]));
  }

}
