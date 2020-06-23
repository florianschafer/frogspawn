/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.traversal;

/**
 * Edge traversal modes
 */

public enum TraversalMode {

  /**
   * Default traversal mode.
   * Traverse over all entries of the adjacency matrix.
   */

  DEFAULT,

  /**
   * Traverses only entries <code>(u,v)</code> where <code>u ≥ v</code>.
   * Use this mode whenever the effective double traversal of edges is
   * undesired.
   */

  LOWER_TRIANGULAR

}
