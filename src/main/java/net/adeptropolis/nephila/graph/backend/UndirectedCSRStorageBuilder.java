package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.backend.primitives.BigDoubles;
import net.adeptropolis.nephila.graph.backend.primitives.BigInts;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongMergeSort;
import net.adeptropolis.nephila.graph.backend.primitives.sorting.LongMergeSort.SortOps;

public class UndirectedCSRStorageBuilder {

  private static final long INITIAL_SIZE = 1 << 24;
  private static final long GROW_SIZE = 1 << 24;

  private long size = INITIAL_SIZE;
  private long ptr = 0L;

  private final BigInts[] edges = { new BigInts(INITIAL_SIZE), new BigInts(INITIAL_SIZE) };
  private final BigDoubles weights = new BigDoubles(INITIAL_SIZE);

  public UndirectedCSRStorageBuilder() {

  }

  /**
   * <p>Add a new undirected edge to the graph.
   * <a href="http://www.supermanisthegreatest.com">Superman!</a>
   * </p>
   * @param u left vertex
   * @param v right vertex
   * @param weight edge weight
   * @return this
   */
  public UndirectedCSRStorageBuilder add(int u, int v, double weight) {
    set(ptr++, u, v, weight);
    if (u != v) set(ptr++, v, u, weight);
    return this;
  }

  private void set(long idx, int u, int v, double weight) {
    if (idx >= size) resize(size + GROW_SIZE);
    edges[0].set(idx, u);
    edges[1].set(idx, v);
    weights.set(idx, weight);
  }

  private void resize(long newSize) {
    size = newSize;
    edges[0].resize(newSize);
    edges[1].resize(newSize);
    weights.resize(newSize);
  }

  public CSRStorage build() {

    if (ptr == 0L) {
      return new CSRStorage(0, 0, new long[0], new BigInts(0), new BigDoubles(0));
    }

    sort();
    reduce();
    compact();

    int numVertices = edges[0].get(ptr - 1) + 1;

    long[] vertexPtrs = computeVertexPointers(numVertices);
    edges[0] = null;

    return new CSRStorage(numVertices, ptr, vertexPtrs, edges[1], weights);
  }

  private void sort() {
    EdgeSortOps ops = new EdgeSortOps();
    LongMergeSort.mergeSort(0, ptr, ops);
  }

  // NOTE: Requires the arrays to be sorted!
  private void reduce() {

    if (ptr == 0) return;

    int[] activeEdge = new int[]{ edges[0].get(0), edges[1].get(0) };
    double activeValue = weights.get(0);

    int[] edge = new int[2];
    double val;

    long writePtr = 0;

    for (long scrollPtr = 1; scrollPtr < ptr; scrollPtr++) {

      edge[0] = edges[0].get(scrollPtr);
      edge[1] = edges[1].get(scrollPtr);
      val = weights.get(scrollPtr);

      if (edge[0] == activeEdge[0] && edge[1] == activeEdge[1]) {
        activeValue += val;
      } else {
        if (writePtr < scrollPtr) set(writePtr++, activeEdge[0], activeEdge[1], activeValue);
        activeEdge[0] = edge[0];
        activeEdge[1] = edge[1];
        activeValue = val;
      }
    }

    set(writePtr++, activeEdge[0], activeEdge[1], activeValue);
    ptr = writePtr;

  }

  private void compact() {
    resize(ptr);
  }

  private long[] computeVertexPointers(int numVertices) {

    long[] vertexPtrs = new long[numVertices + 1];
    vertexPtrs[0] = 0;
    vertexPtrs[numVertices] = ptr;

    int prevVertex = 0;
    int v;

    for (long i = 0; i < ptr; i++) {
      v = edges[0].get(i);
      if (v > prevVertex) {
        for (int j = prevVertex + 1; j <= v; j++) vertexPtrs[j] = i;
        prevVertex = v;
      }
    }

    return vertexPtrs;
  }

  private class EdgeSortOps implements SortOps {

    @Override
    public int compare(long idx1, long idx2) {
      int c = edges[0].compare(idx1, idx2);
      return c != 0 ? c : edges[1].compare(idx1, idx2);
    }

    @Override
    public void swap(long idx1, long idx2) {
      edges[0].swap(idx1, idx2);
      edges[1].swap(idx1, idx2);
      weights.swap(idx1, idx2);
    }

  }

}
