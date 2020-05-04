/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import net.adeptropolis.frogspawn.graphs.VertexIterator;

/**
 * {@inheritDoc}
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
   * Iterator over the vertex set
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
   * {@inheritDoc}
   */

  @Override
  public int localId() {
    return localId - 1;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int globalId() {
    return globalId;
  }

}
