/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

class VertexIndex<T> {

  private final ConcurrentMap<T, Integer> indices;
  private final AtomicInteger currentIdx;

  public VertexIndex(Playground.LabeledEdgeSource<T> edgeSource) {
    this.indices = new ConcurrentHashMap<>();
    this.currentIdx = new AtomicInteger(0);
    edgeSource.edges().parallel().forEach(edge -> {
      get(edge.u);
      get(edge.v);
    });
//    Object2IntOpenHashMap<T>
  }
//
//  public Stream<Edge> mapEdges(FooingOuterEdgeSourceTest.LabeledEdgeSource<T> edgeSource) {
//    ThreadLocal<Edge> edges = ThreadLocal.withInitial(() -> new Edge(0, 0, 0.0));
//    return edgeSource.edges().parallel().map(labeledEdge -> {
//      Edge edge = edges.getVertex();
//      edge.u = indices.getVertex(labeledEdge.u);
//      edge.v = indices.getVertex(labeledEdge.v);
//      edge.weight = labeledEdge.weight;
//      return edge;
//    });
//  }

  public int get(T label) {
    return indices.computeIfAbsent(label, (x) -> currentIdx.getAndIncrement());
  }

  private int[] computeDegrees(Playground.LabeledEdgeSource<T> edgeSource) {

    AtomicIntegerArray deg = new AtomicIntegerArray(size());
    edgeSource.edges()
            .parallel()
            .forEach(edge -> deg.incrementAndGet(indices.get(edge.u)));
    int[] degArray = new int[size()];
    IntStream.range(0, size())
            .parallel()
            .forEach(i -> degArray[i] = deg.get(i));
    return degArray;
  }

  public int size() {
    return indices.size();
  }

}
