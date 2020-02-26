/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.traversal;

/**
 * Consumer for edge traversal operations
 */

@FunctionalInterface
public interface EdgeConsumer {

  /**
   * Accept a new edge
   *
   * @param u      Left endpoint
   * @param v      Right endpoint
   * @param weight Edge weight
   */

  void accept(int u, int v, double weight);

}
