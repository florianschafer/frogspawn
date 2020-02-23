/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.implementations;

import com.google.common.annotations.VisibleForTesting;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.nephila.graphs.implementations.arrays.BigInts;
import net.adeptropolis.nephila.graphs.implementations.arrays.LongMergeSort;
import net.adeptropolis.nephila.graphs.implementations.arrays.LongMergeSort.SortOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Build new Graph instances</p>
 * <p>This little gizmo creates a new compressed sparse graph from an arbitrary
 * number of (not necessarily unique or sorted) weighted edge triples.</p>
 *
 * @author Florian Schaefer
 * @author florian@adeptropolis.net
 * @version 1.0
 * @since 1.0
 */

public class CompressedSparseGraphBuilder implements Graph.Builder {

  private static final Logger LOG = LoggerFactory.getLogger(CompressedSparseGraphBuilder.class.getSimpleName());
  private static final long INITIAL_SIZE = 1 << 24;
  private static final long GROW_SIZE = 1 << 24;
  private final BigInts[] edges = {new BigInts(INITIAL_SIZE), new BigInts(INITIAL_SIZE)};
  private final BigDoubles weights = new BigDoubles(INITIAL_SIZE);
  private long size = INITIAL_SIZE;
  private long ptr = 0L;

  /**
   * Add a new undirected edge to the graph.
   *
   * @param u      left vertex
   * @param v      right vertex
   * @param weight edge weight
   * @return this
   */

  @Override
  @SuppressWarnings("squid:S2234")
  public CompressedSparseGraphBuilder add(int u, int v, double weight) {
    set(ptr++, u, v, weight);
    if (u != v) set(ptr++, v, u, weight);
    return this;
  }

  @Override
  public Graph.Builder addDirected(int u, int v, double weight) {
    set(ptr++, u, v, weight);
    return this;
  }

  /**
   * Set an edge buffer element
   *
   * @param idx    Index
   * @param u      Left vertex
   * @param v      Right Vertex
   * @param weight Edge weight
   */

  private void set(long idx, int u, int v, double weight) {
    if (idx >= size) resize(size + GROW_SIZE);
    edges[0].set(idx, u);
    edges[1].set(idx, v);
    weights.set(idx, weight);
  }

  /**
   * Resize the edge buffer
   *
   * @param newSize New size
   */

  private void resize(long newSize) {
    size = newSize;
    edges[0].resize(newSize);
    edges[1].resize(newSize);
    weights.resize(newSize);
  }

  /**
   * Build the graph
   *
   * @return A new immutable Graph instance
   */

  @Override
  public CompressedSparseGraph build() {
    CompressedSparseGraphDatastore datastore = buildDatastore();
    return new CompressedSparseGraph(datastore);
  }

  @VisibleForTesting
  CompressedSparseGraphDatastore buildDatastore() {
    if (ptr == 0L) {
      return new CompressedSparseGraphDatastore(0, 0, new long[0], new BigInts(0), new BigDoubles(0));
    }
    sort();
    reduce();
    compact();
    int graphSize = edges[0].get(ptr - 1) + 1;
    long[] pointers = computePointers(graphSize);
    LOG.debug("Finished building graph with {} vertices and {} edges", graphSize, ptr);
    return new CompressedSparseGraphDatastore(graphSize, ptr, pointers, edges[1], weights);
  }

  /**
   * Sort the edge buffer
   */

  private void sort() {
    EdgeSortOps ops = new EdgeSortOps();
    LongMergeSort.mergeSort(0, ptr, ops);
  }

  /**
   * <p>Reduce multiple occurrences of an edge to a single instance with accumulated weights</p>
   * NOTE: This method assumes that the edge buffer has already been sorted!
   */

  private void reduce() {

    if (ptr == 0) return;

    int[] currentEdge = new int[]{edges[0].get(0), edges[1].get(0)};
    double currentValue = weights.get(0);

    int[] edge = new int[2];
    double val;

    long writePtr = 0;

    for (long scrollPtr = 1; scrollPtr < ptr; scrollPtr++) {

      edge[0] = edges[0].get(scrollPtr);
      edge[1] = edges[1].get(scrollPtr);
      val = weights.get(scrollPtr);

      if (edge[0] == currentEdge[0] && edge[1] == currentEdge[1]) {
        currentValue += val;
      } else {
        if (writePtr < scrollPtr) set(writePtr++, currentEdge[0], currentEdge[1], currentValue);
        currentEdge[0] = edge[0];
        currentEdge[1] = edge[1];
        currentValue = val;
      }
    }

    set(writePtr++, currentEdge[0], currentEdge[1], currentValue);
    ptr = writePtr;

  }

  /**
   * Shrink the buffer down to its minimum size
   */

  private void compact() {
    resize(ptr);
  }


  /**
   * For every vertex, compute its pointer relative to both arrays storing edges and weights
   *
   * @param graphSize Size of the graph
   * @return Array whose i-th entry points to the first edge of vertex i
   */

  private long[] computePointers(int graphSize) {

    long[] pointers = new long[graphSize + 1];
    pointers[0] = 0;
    pointers[graphSize] = ptr;

    int prevVertex = 0;
    int v;

    for (long i = 0; i < ptr; i++) {
      v = edges[0].get(i);
      if (v > prevVertex) {
        for (int j = prevVertex + 1; j <= v; j++) pointers[j] = i;
        prevVertex = v;
      }
    }

    return pointers;
  }

  /**
   * Edge buffer implementation of SortOps
   */

  private class EdgeSortOps implements SortOps {

    /**
     * Compare two edges by (1) left node (2) right node
     *
     * @param idx1 Index of the first edge in the buffer
     * @param idx2 Index of the second edge in the buffer
     * @return -1 if first edge &lt; second edge, 0 if equal, 1 else
     */

    @Override
    public int compare(long idx1, long idx2) {
      int c = edges[0].compare(idx1, idx2);
      return c != 0 ? c : edges[1].compare(idx1, idx2);
    }

    /**
     * Swap two edges within the edge buffer
     *
     * @param idx1 Index of the first edge
     * @param idx2 Index of the second edge
     */

    @Override
    public void swap(long idx1, long idx2) {
      edges[0].swap(idx1, idx2);
      edges[1].swap(idx1, idx2);
      weights.swap(idx1, idx2);
    }

  }

}
