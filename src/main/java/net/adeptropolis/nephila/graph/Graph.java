package net.adeptropolis.nephila.graph;

import net.adeptropolis.nephila.graph.backend.Backend;

public class Graph {

  private final Backend backend;
  private int[] vertexBuf;
  private int verticesSize;

  public Graph(Backend backend) {
    this.backend = backend;
  }

  public void addVertex(int v) {
    vertexBuf[verticesSize++]= v;
  }

//  public Graph ensureSorted() {
//    Arrays.cop
//    if (!sorted) {
//      Arrays.parallelSort(vertexBuf, 0, verticesSize);
//      sorted = true;
//    }
//    return this;
//  }



}
