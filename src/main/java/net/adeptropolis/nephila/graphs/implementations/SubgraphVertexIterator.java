package net.adeptropolis.nephila.graphs.implementations;

// TODO: Move into subgraph class?

import net.adeptropolis.nephila.graphs.VertexIterator;

public class SubgraphVertexIterator implements VertexIterator {

  private int localId;
  private int globalId;

  private int[] vertexBuf;

  public SubgraphVertexIterator() {
  }

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
  public boolean proceed() {
    if (vertexBuf == null || localId == vertexBuf.length) {
      return false;
    }
    globalId = vertexBuf[localId++];
    return true;
  }

  /**
   * Note: -1 because right after <code>globalId</code> is assigned, <code>localId</code> is being incremented
   *
   * @return
   */

  @Override
  public int localId() {
    return localId - 1;
  }

  @Override
  public int globalId() {
    return globalId;
  }

}
