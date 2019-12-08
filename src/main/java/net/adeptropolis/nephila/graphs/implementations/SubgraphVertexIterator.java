/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.implementations;

// TODO: Move into subgraph class?

import net.adeptropolis.nephila.graphs.VertexIterator;

/**
 * Iterator for a vertex set of a subgraph that carries both local and global ids
 */

public class SubgraphVertexIterator implements VertexIterator {

  private int localId;
  private int globalId;

  private int[] vertexBuf;

  public SubgraphVertexIterator() {
  }

  /**
   * Reset the iterator (initialization and reusability)
   *
   * @param vertexBuf List of global vertex ids
   * @return
   */

  public SubgraphVertexIterator reset(int[] vertexBuf) {
    this.vertexBuf = vertexBuf;
    this.localId = 0;
    this.globalId = -1;
    return this;
  }

  /**
   * Note: Must proceed before first use => while(...)
   *
   * @return
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
