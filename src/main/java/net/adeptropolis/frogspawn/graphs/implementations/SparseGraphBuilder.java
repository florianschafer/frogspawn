/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigSwapper;
import it.unimi.dsi.fastutil.longs.LongComparator;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigInts;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Build new Graph instances</p>
 * <p>This little gizmo creates a new compressed sparse graph from an arbitrary
 * number of (not necessarily unique or sorted) weighted edge triples.</p>
 */

public class SparseGraphBuilder implements Graph.Builder {

  private static final Logger LOG = LoggerFactory.getLogger(SparseGraphBuilder.class.getSimpleName());
  private static final long INITIAL_SIZE = 1 << 24;
  private static final long GROW_SIZE = 1 << 24;
  private final double minWeight;
  private final BigInts[] edges = {new BigInts(INITIAL_SIZE), new BigInts(INITIAL_SIZE)};
  private final BigDoubles weights = new BigDoubles(INITIAL_SIZE);
  private long size = INITIAL_SIZE;
  private long ptr = 0L;

  /**
   * Constructor setting a min edge weight
   *
   * @param minWeight Minimum edge weight. Weights below this value cause a <code>GraphConstructionException</code>
   */

  public SparseGraphBuilder(double minWeight) {
    this.minWeight = minWeight;
  }

  /**
   * Default Constructor
   */

  public SparseGraphBuilder() {
    this(1d);
  }

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
  public SparseGraphBuilder add(int u, int v, double weight) {
    addDirected(u, v, weight);
    if (u != v) addDirected(v, u, weight);
    return this;
  }

  /**
   * Add a new directed edge to the graph.
   *
   * @param u      left vertex
   * @param v      right vertex
   * @param weight edge weight
   * @return this
   */

  @Override
  public Graph.Builder addDirected(int u, int v, double weight) {
    if (weight < minWeight) {
      throw new GraphConstructionException(String.format("Tried to add an edge with weight < %.3f", minWeight));
    }
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
  public SparseGraph build() {
    CSRDatastore datastore = buildDatastore();
    return new SparseGraph(datastore);
  }

  /**
   * Build the main datastore from the edge buffer. This also collapses multiple instances of the same edge into one,
   * aggregating their weights.
   *
   * @return a new graph datastore
   */

  @VisibleForTesting
  CSRDatastore buildDatastore() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    if (ptr == 0L) {
      return new CSRDatastore(0, 0, new long[0], new BigInts(0), new BigDoubles(0));
    }
    sort();
    reduce();
    compact();
    int graphSize = edges[0].get(ptr - 1) + 1;
    long[] pointers = computePointers(graphSize);
    stopWatch.stop();
    LOG.info("Finished building graph with {} vertices and {} edges in {}", graphSize, ptr, stopWatch);
    return new CSRDatastore(graphSize, ptr, pointers, edges[1], weights);
  }

  /**
   * Sort the edge buffer
   */

  private void sort() {
    EdgeSortOps ops = new EdgeSortOps();
    BigArrays.mergeSort(0, ptr, ops, ops);
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

  private class EdgeSortOps implements LongComparator, BigSwapper {

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
