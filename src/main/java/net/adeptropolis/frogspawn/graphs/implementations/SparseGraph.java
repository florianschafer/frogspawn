/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.VertexIterator;
import net.adeptropolis.frogspawn.graphs.traversal.EdgeConsumer;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;

import java.io.Serializable;

/**
 * Compressed sparse graph implementation
 */

public class SparseGraph extends Graph implements Serializable {

  static final long serialVersionUID = 3908340146557361096L;

  private final CSRDatastore datastore;

  /**
   * Constructor
   *
   * @param datastore Graph datastore
   */

  SparseGraph(CSRDatastore datastore) {
    this.datastore = datastore;
  }

  /**
   * Create a builder for this type of graphs
   *
   * @return A new builder instance
   */

  public static SparseGraphBuilder builder() {
    return new SparseGraphBuilder();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int order() {
    return datastore.order();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public long size() {
    return datastore.edges.size();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public VertexIterator vertexIterator() {
    return new SparseGraphVertexIterator();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int[] collectVertices() {
    return IntIterators.unwrap(globalVertexIdIterator());
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public IntIterator globalVertexIdIterator() {
    return IntIterators.fromTo(0, order());
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void traverseIncidentEdges(int v, EdgeConsumer consumer, TraversalMode mode) {

    if (order() == 0 || v < 0) {
      return;
    }

    long low = datastore.pointers[v];
    long high = datastore.pointers[v + 1];
    if (low == high) {
      return;
    }

    int rightEndpoint;
    for (long ptr = low; ptr < high; ptr++) {
      rightEndpoint = datastore.edges.get(ptr);
      if (mode == TraversalMode.LOWER_TRIANGULAR && v < rightEndpoint) {
        break;
      }
      consumer.accept(v, rightEndpoint, datastore.weights.get(ptr));
      if (rightEndpoint + 1 >= order()) {
        break;
      }
    }

  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int localVertexId(int globalVertexId) {
    return globalVertexId;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int globalVertexId(int localVertexId) {
    return localVertexId;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Graph subgraph(IntIterator vertices) {
    return new SparseSubgraph(datastore, vertices);
  }

  /**
   * Iterator over the vertex set
   */

  public class SparseGraphVertexIterator implements VertexIterator {

    int idx = 0;

    /**
     * {@inheritDoc}
     */

    @Override
    public boolean hasNext() {
      return idx++ < order();
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public int localId() {
      return idx - 1;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public int globalId() {
      return idx - 1;
    }
  }

}
