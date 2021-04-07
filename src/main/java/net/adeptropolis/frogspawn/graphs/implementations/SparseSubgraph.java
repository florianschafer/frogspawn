/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.VertexIterator;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.InterpolationSearch;
import net.adeptropolis.frogspawn.graphs.traversal.EdgeConsumer;
import net.adeptropolis.frogspawn.graphs.traversal.ParallelEdgeOps;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Compressed sparse subgraph implementation, i.e. a subgraph of {@link SparseGraph}
 */

public class SparseSubgraph extends Graph implements Serializable {

  static final long serialVersionUID = 1332295543424708677L;

  private final CSRDatastore datastore;
  private final int[] vertices;
  private long cachedNumEdges = -1L;

  /**
   * Constructor
   *
   * @param datastore The underlying graph datastore
   * @param vertices  An iterator of global vertex ids
   */

  SparseSubgraph(CSRDatastore datastore, IntIterator vertices) {
    this.datastore = datastore;
    this.vertices = IntIterators.unwrap(vertices);
    Arrays.parallelSort(this.vertices, 0, order());
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public long size() {
    if (cachedNumEdges >= 0) {
      return cachedNumEdges;
    } else {
      EdgeCountingConsumer edgeCountingConsumer = new EdgeCountingConsumer();
      traverseParallel(edgeCountingConsumer);
      cachedNumEdges = edgeCountingConsumer.getCount();
      return cachedNumEdges;
    }
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public VertexIterator vertexIterator() {
    return new SparseSubgraphVertexIterator();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int[] collectVertices() {
    return vertices.clone();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public IntIterator globalVertexIdIterator() {
    return IntIterators.wrap(vertices);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int order() {
    return vertices.length;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void traverseIncidentEdges(int v, EdgeConsumer consumer, TraversalMode mode) {

    if (order() == 0 || v < 0) {
      return;
    }

    int globalId = globalVertexId(v);

    long low = datastore.pointers[globalId];
    long high = datastore.pointers[globalId + 1];

    if (low == high) {
      return;
    }

    if (order() > high - low) {
      traverseByAdjacent(v, consumer, low, high, mode);
    } else {
      traverseByVertices(v, consumer, low, high, mode);
    }
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void traverseParallel(EdgeConsumer consumer) {
    ParallelEdgeOps.traverse(this, consumer, TraversalMode.DEFAULT);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int globalVertexId(int localVertexId) {
    return vertices[localVertexId];
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int localVertexId(int globalVertexId) {
    return InterpolationSearch.search(vertices, globalVertexId, 0, order() - 1);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Graph subgraph(IntIterator vertices) {
    return new SparseSubgraph(datastore, vertices);
  }

  /**
   * Traverse all neighbours of a given local vertex by the non-zero entries of the adjacency matrix
   *
   * @param leftEndpoint A local vertex id
   * @param consumer     An instance of <code>EdgeConsumer</code>
   * @param low          Initial edge pointer
   * @param high         Maximum edge pointer (exclusive!)
   * @param mode         Traversal mode
   */

  private void traverseByAdjacent(final int leftEndpoint, final EdgeConsumer consumer, final long low, final long high, TraversalMode mode) {

    int secPtr = 0;
    int rightEndpoint;

    for (long ptr = low; ptr < high; ptr++) {

      rightEndpoint = InterpolationSearch.search(vertices, datastore.edges.get(ptr), secPtr, order() - 1);

      if (mode == TraversalMode.LOWER_TRIANGULAR && leftEndpoint < rightEndpoint) {
        break;
      }

      if (rightEndpoint >= 0) {
        consumer.accept(leftEndpoint, rightEndpoint, datastore.weights.get(ptr));
        secPtr = rightEndpoint + 1;
      }

      if (secPtr >= order()) break;
    }
  }

  /**
   * Traverse all neighbours of a given local vertex by the vertex set
   *
   * @param leftEndpoint A local vertex id
   * @param consumer     An instance of <code>EdgeConsumer</code>
   * @param low          Initial edge pointer
   * @param high         Maximum edge pointer (exclusive!)
   * @param mode         Traversal mode
   */

  private void traverseByVertices(final int leftEndpoint, final EdgeConsumer consumer, final long low, final long high, TraversalMode mode) {

    long ptr = low;
    long retrievedIdx;

    for (int i = 0; i < order(); i++) {

      if (mode == TraversalMode.LOWER_TRIANGULAR && leftEndpoint < i) {
        break;
      }

      retrievedIdx = InterpolationSearch.search(datastore.edges, vertices[i], ptr, high - 1);

      if (retrievedIdx >= 0 && retrievedIdx < high) {
        consumer.accept(leftEndpoint, i, datastore.weights.get(retrievedIdx));
        ptr = retrievedIdx + 1;
      }

      if (ptr >= high) break;
    }
  }

  /**
   * Consumer counting the total number of distinct edges of the graph
   */

  private static class EdgeCountingConsumer implements EdgeConsumer {

    private final AtomicLong cnt;

    EdgeCountingConsumer() {
      cnt = new AtomicLong();
    }

    @Override
    public void accept(int u, int v, double weight) {
      cnt.incrementAndGet();
    }

    long getCount() {
      return cnt.get();
    }

  }

  /**
   * {@inheritDoc}
   */

  public class SparseSubgraphVertexIterator implements VertexIterator {

    private int localId;
    private int globalId;

    /**
     * {@inheritDoc}
     */

    @Override
    public boolean hasNext() {
      if (vertices == null || localId == vertices.length) {
        return false;
      }
      globalId = vertices[localId++];
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
}
