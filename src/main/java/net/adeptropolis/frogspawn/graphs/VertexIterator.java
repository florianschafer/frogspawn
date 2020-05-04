/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

/**
 * Iterator over a vertex set of a subgraph that carries both local and global ids
 */

public interface VertexIterator {

  /**
   * @return true if and only if at least 1 more vertex is available
   */

  boolean hasNext();

  /**
   * @return Current local id
   */

  int localId();

  /**
   * @return Current global id
   */

  int globalId();

}
