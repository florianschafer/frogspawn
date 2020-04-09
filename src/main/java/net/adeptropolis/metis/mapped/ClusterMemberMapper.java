/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.mapped;

/**
 * Provides a mapping between clusters generated from a labeled graph and effective mapped cluster members
 *
 * @param <V> Vertex label type
 * @param <T> Effective cluster member type
 */

@FunctionalInterface
interface ClusterMemberMapper<V, T> {

  /**
   * @param vertexLabel Vertex label
   * @param weight      Vertex weight
   * @param score       Vertex consistency score
   * @return New effective mapped cluster member
   */
  T map(V vertexLabel, double weight, double score);

}
