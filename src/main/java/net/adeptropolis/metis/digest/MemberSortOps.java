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
  private final ClusterMemberComparator comparator;

  /**
   * Constructor
   *
   * @param vertices   Vertices
   * @param weights    Vertex weights
   * @param scores     Vertex consistency scores
   * @param comparator Indirect comparator for cluster vertices
   */

  MemberSortOps(int[] vertices, double[] weights, double[] scores, ClusterMemberComparator comparator) {
    this.vertices = vertices;
    this.weights = weights;
    this.scores = scores;
    this.comparator = comparator;
  }

  /**
   * Sort a given (vertices, weights, scores)-triple using a supplied indirect comparator
   *
   * @param vertices   Cluster vertices
   * @param weights    Vertex weights
   * @param scores     Vertex scores
   * @param comparator Indirect comparator
   */

  public static void sort(int[] vertices, double[] weights, double[] scores, ClusterMemberComparator comparator) {
    MemberSortOps ops = new MemberSortOps(vertices, weights, scores, comparator);
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
   * Compare two indices by their associated weight
   *
   * @param i First index
   * @param j Second index
   * @return -1 if <code>weights[j] &lt; weights[i]</code>, 0 if equal, 1 otherwise
   */

  @Override
  public int compare(int i, int j) {
    return comparator.compare(vertices, weights, scores, i, j);
  }

}
