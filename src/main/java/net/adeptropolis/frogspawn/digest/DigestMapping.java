/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

/**
 * Provides a mapping between cluster digest vertices and custom cluster member objects
 *
 * @param <T> Custom cluster member type
 */

@FunctionalInterface
public interface DigestMapping<T> {

  /**
   * @param vertexId Vertex id
   * @param weight   Vertex weight
   * @param score    Vertex affiliation score
   * @return Custom cluster member object
   */
  T map(int vertexId, double weight, double score);

}
