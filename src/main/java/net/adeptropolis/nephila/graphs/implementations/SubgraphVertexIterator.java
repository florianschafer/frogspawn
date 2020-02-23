/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.implementations;

import net.adeptropolis.nephila.graphs.VertexIterator;

/**
 * Iterator for a vertex set of a subgraph that carries both local and global ids
 */

public class SubgraphVertexIterator implements VertexIterator {

  private int localId;
  private int globalId;

  private int[] vertexBuf;

  /**
   * Reset the iterator (initialization and reusability)
   *
   * @param vertexBuf List of global vertex ids
   * @return this
   */

  public SubgraphVertexIterator reset(int[] vertexBuf) {
    this.vertexBuf = vertexBuf;
    this.localId = 0;
    this.globalId = -1;
    return this;
  }

  /**
   * @return whether there is another element available
   */

  @Override
  public boolean hasNext() {
    if (vertexBuf == null || localId == vertexBuf.length) {
      return false;
    }
    globalId = vertexBuf[localId++];
    return true;
  }

  /**
   * Return the local id of the current vertex
   *
   * @return A local vertex id
   */

  @Override
  public int localId() {
    return localId - 1;
  }

  /**
   * Return the global id of the current vertex
   *
   * @return A global vertex id
   */

  @Override
  public int globalId() {
    return globalId;
  }

}
