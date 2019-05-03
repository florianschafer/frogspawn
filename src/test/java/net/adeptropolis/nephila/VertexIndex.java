package net.adeptropolis.nephila;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

class VertexIndex<T> {

  private final ConcurrentMap<T, Integer> indices;
  private final AtomicInteger currentIdx;

  public VertexIndex(FooingOuterEdgeSourceTest.LabeledEdgeSource<T> edgeSource) {
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
//      Edge edge = edges.get();
//      edge.u = indices.get(labeledEdge.u);
//      edge.v = indices.get(labeledEdge.v);
//      edge.weight = labeledEdge.weight;
//      return edge;
//    });
//  }

  private int[] computeDegrees(FooingOuterEdgeSourceTest.LabeledEdgeSource<T> edgeSource) {

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

  public int get(T label) {
    return indices.computeIfAbsent(label, (x) -> currentIdx.getAndIncrement());
  }

  public int size() {
    return indices.size();
  }

}
