/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

/**
 * Provides a mapping between cluster digest vertices generated from labeled graphs and custom cluster
 * member objects
 *
 * @param <V> Vertex label type
 * @param <T> Custom cluster member type
 */

@FunctionalInterface
public
interface LabeledDigestMapping<V, T> {

  /**
   * @param vertexLabel Vertex label
   * @param weight      Vertex weight
   * @param score       Vertex affiliation score
   * @return Custom cluster member object
   */
  T map(V vertexLabel, double weight, double score);

}
